/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 11/08/2016.
 */

package cm.aptoide.pt.billing.networking;

import android.content.SharedPreferences;
import android.content.res.Resources;
import cm.aptoide.pt.billing.BillingService;
import cm.aptoide.pt.billing.Merchant;
import cm.aptoide.pt.billing.authorization.Authorization;
import cm.aptoide.pt.billing.authorization.CreditCardAuthorization;
import cm.aptoide.pt.billing.authorization.PayPalAuthorization;
import cm.aptoide.pt.billing.payment.PaymentMethod;
import cm.aptoide.pt.billing.product.Product;
import cm.aptoide.pt.billing.purchase.Purchase;
import cm.aptoide.pt.billing.transaction.Transaction;
import cm.aptoide.pt.billing.transaction.TransactionFactory;
import cm.aptoide.pt.dataprovider.interfaces.TokenInvalidator;
import cm.aptoide.pt.dataprovider.model.v3.PaidApp;
import cm.aptoide.pt.dataprovider.ws.BodyInterceptor;
import cm.aptoide.pt.dataprovider.ws.v3.BaseBody;
import cm.aptoide.pt.dataprovider.ws.v3.GetApkInfoRequest;
import cm.aptoide.pt.dataprovider.ws.v3.GetTransactionRequest;
import java.util.Collections;
import java.util.List;
import okhttp3.OkHttpClient;
import retrofit2.Converter;
import rx.Completable;
import rx.Single;

public class BillingServiceV3 implements BillingService {

  private final BodyInterceptor<BaseBody> bodyInterceptorV3;
  private final OkHttpClient httpClient;
  private final Converter.Factory converterFactory;
  private final TokenInvalidator tokenInvalidator;
  private final SharedPreferences sharedPreferences;
  private final PurchaseMapperV3 purchaseMapper;
  private final ProductMapperV3 productMapper;
  private final AuthorizationMapperV3 authorizationMapper;
  private final Resources resources;
  private final PaymentMethod paymentMethod;
  private final int currentAPILevel;
  private final int serviceMinimumAPILevel;
  private final String marketName;
  private final TransactionMapperV3 transactionMapper;
  private final TransactionFactory transactionFactory;

  public BillingServiceV3(BodyInterceptor<BaseBody> bodyInterceptorV3, OkHttpClient httpClient,
      Converter.Factory converterFactory, TokenInvalidator tokenInvalidator,
      SharedPreferences sharedPreferences, PurchaseMapperV3 purchaseMapper,
      ProductMapperV3 productMapper, AuthorizationMapperV3 authorizationMapper, Resources resources,
      PaymentMethod paymentMethod, int currentAPILevel, int serviceMinimumAPILevel,
      String marketName, TransactionMapperV3 transactionMapper,
      TransactionFactory transactionFactory) {
    this.bodyInterceptorV3 = bodyInterceptorV3;
    this.httpClient = httpClient;
    this.converterFactory = converterFactory;
    this.tokenInvalidator = tokenInvalidator;
    this.sharedPreferences = sharedPreferences;
    this.purchaseMapper = purchaseMapper;
    this.productMapper = productMapper;
    this.authorizationMapper = authorizationMapper;
    this.resources = resources;
    this.paymentMethod = paymentMethod;
    this.currentAPILevel = currentAPILevel;
    this.serviceMinimumAPILevel = serviceMinimumAPILevel;
    this.marketName = marketName;
    this.transactionMapper = transactionMapper;
    this.transactionFactory = transactionFactory;
  }

  @Override public Single<List<PaymentMethod>> getPaymentMethods() {
    if (currentAPILevel >= serviceMinimumAPILevel) {
      return Single.just(Collections.singletonList(paymentMethod));
    }
    return Single.just(Collections.emptyList());
  }

  @Override public Single<Merchant> getMerchant(String packageName, int versionCode) {
    return Single.just(new Merchant(-1, marketName, packageName, versionCode));
  }

  @Override public Completable deletePurchase(long purchaseId) {
    return Completable.error(new IllegalStateException("Not implemented!"));
  }

  @Override public Single<List<Purchase>> getPurchases(String merchantName) {
    return Single.error(new IllegalStateException("Not implemented!"));
  }

  @Override public Single<Purchase> getPurchase(long productId) {
    return getServerPaidApp(true, productId).map(
        app -> purchaseMapper.map(app, productId));
  }

  @Override public Single<List<Product>> getProducts(String merchantName, List<String> productIds) {
    return Single.error(new IllegalStateException("Not implemented!"));
  }

  @Override public Single<Product> getProduct(String sku, String merchantName) {
    return getServerPaidApp(false, Long.valueOf(sku)).map(
        paidApp -> productMapper.map(paidApp));
  }

  @Override public Single<List<Authorization>> getAuthorizations(String customerId) {
    return Single.error(new IllegalStateException("Not implemented."));
  }

  @Override public Single<PayPalAuthorization> updatePayPalAuthorization(String customerId,
      long transactionId, String payKey, long paymentMethodId, long authorizationId) {
    return Single.error(new IllegalStateException("Not implemented!"));
  }

  @Override
  public Single<PayPalAuthorization> createPayPalAuthorization(String customerId, String token) {
    return Single.error(new IllegalStateException("Not implemented!"));
  }

  @Override public Single<CreditCardAuthorization> updateCreditCardAuthorization(String customerId,
      long authorizationId, String metadata, long paymentMethodId) {
    return Single.error(new IllegalStateException("Not implemented!"));
  }

  @Override public Single<CreditCardAuthorization> createCreditCardAuthorization(String customerId,
      String token, long paymentMethodId) {
    return Single.error(new IllegalStateException("Not implemented!"));
  }

  @Override public Single<Transaction> getTransaction(String customerId, long productId) {
    return GetApkInfoRequest.of(productId, bodyInterceptorV3, httpClient, converterFactory,
        tokenInvalidator, sharedPreferences, resources)
        .observe(false)
        .toSingle()
        .flatMap(response -> {
          if (response.isOk()) {
            if (response.isPaid()) {
              return GetTransactionRequest.of(response.getPayment()
                      .getMetadata()
                      .getProductId(), bodyInterceptorV3, httpClient, converterFactory,
                  tokenInvalidator, sharedPreferences)
                  .observe(true)
                  .toSingle()
                  .map(transactionResponse -> transactionMapper.map(customerId, productId,
                      transactionResponse, productId));
            }
            return Single.just(transactionFactory.create(productId, customerId, productId,
                Transaction.Status.COMPLETED, 1));
          }

          return Single.just(
              transactionFactory.create(productId, customerId, productId, Transaction.Status.FAILED,
                  1));
        });
  }

  @Override public Single<Transaction> createTransaction(String customerId, long productId,
      long authorizationId) {
    return GetApkInfoRequest.of(productId, bodyInterceptorV3, httpClient, converterFactory,
        tokenInvalidator, sharedPreferences, resources)
        .observe(true)
        .toSingle()
        .map(response -> {

          if (response.isOk()) {
            if (response.isPaid()) {
              return transactionFactory.create(productId, customerId, productId,
                  Transaction.Status.PENDING_SERVICE_AUTHORIZATION, 1);
            }
            return transactionFactory.create(productId, customerId, productId,
                Transaction.Status.COMPLETED, 1);
          }
          return transactionFactory.create(productId, customerId, productId,
              Transaction.Status.FAILED, 1);
        });
  }

  private Single<PaidApp> getServerPaidApp(boolean bypassCache, long appId) {
    return GetApkInfoRequest.of(appId, bodyInterceptorV3, httpClient, converterFactory,
        tokenInvalidator, sharedPreferences, resources)
        .observe(bypassCache)
        .first()
        .toSingle();
  }
}
