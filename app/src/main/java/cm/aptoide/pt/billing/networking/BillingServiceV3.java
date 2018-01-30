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
import cm.aptoide.pt.billing.authorization.AuthorizationFactory;
import cm.aptoide.pt.billing.authorization.CreditCardAuthorization;
import cm.aptoide.pt.billing.authorization.PayPalAuthorization;
import cm.aptoide.pt.billing.payment.PaymentMethod;
import cm.aptoide.pt.billing.product.Price;
import cm.aptoide.pt.billing.product.Product;
import cm.aptoide.pt.billing.purchase.Purchase;
import cm.aptoide.pt.billing.transaction.Transaction;
import cm.aptoide.pt.billing.transaction.TransactionFactory;
import cm.aptoide.pt.dataprovider.interfaces.TokenInvalidator;
import cm.aptoide.pt.dataprovider.model.v3.PaidApp;
import cm.aptoide.pt.dataprovider.ws.BodyInterceptor;
import cm.aptoide.pt.dataprovider.ws.v3.BaseBody;
import cm.aptoide.pt.dataprovider.ws.v3.GetApkInfoRequest;
import cm.aptoide.pt.dataprovider.ws.v3.V3;
import java.util.Collections;
import java.util.List;
import okhttp3.OkHttpClient;
import retrofit2.Converter;
import rx.Completable;
import rx.Observable;
import rx.Single;

public class BillingServiceV3 implements BillingService {

  private final BodyInterceptor<BaseBody> bodyInterceptorV3;
  private final OkHttpClient httpClient;
  private final Converter.Factory converterFactory;
  private final TokenInvalidator tokenInvalidator;
  private final SharedPreferences sharedPreferences;
  private final PurchaseMapperV3 purchaseMapper;
  private final ProductMapperV3 productMapper;
  private final Resources resources;
  private final PaymentMethod paymentMethod;
  private final int currentAPILevel;
  private final int serviceMinimumAPILevel;
  private final String marketName;
  private final TransactionFactory transactionFactory;
  private final String payPalIcon;
  private final AuthorizationFactory authorizationFactory;

  public BillingServiceV3(BodyInterceptor<BaseBody> bodyInterceptorV3, OkHttpClient httpClient,
      Converter.Factory converterFactory, TokenInvalidator tokenInvalidator,
      SharedPreferences sharedPreferences, PurchaseMapperV3 purchaseMapper,
      ProductMapperV3 productMapper, Resources resources, PaymentMethod paymentMethod,
      int currentAPILevel, int serviceMinimumAPILevel, String marketName,
      TransactionFactory transactionFactory, String payPalIcon,
      AuthorizationFactory authorizationFactory) {
    this.bodyInterceptorV3 = bodyInterceptorV3;
    this.httpClient = httpClient;
    this.converterFactory = converterFactory;
    this.tokenInvalidator = tokenInvalidator;
    this.sharedPreferences = sharedPreferences;
    this.purchaseMapper = purchaseMapper;
    this.productMapper = productMapper;
    this.resources = resources;
    this.paymentMethod = paymentMethod;
    this.currentAPILevel = currentAPILevel;
    this.serviceMinimumAPILevel = serviceMinimumAPILevel;
    this.marketName = marketName;
    this.transactionFactory = transactionFactory;
    this.payPalIcon = payPalIcon;
    this.authorizationFactory = authorizationFactory;
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
    return getServerPaidApp(productId).map(
        app -> purchaseMapper.map(app, productId));
  }

  @Override public Single<List<Product>> getProducts(String merchantName, List<String> productIds) {
    return Single.error(new IllegalStateException("Not implemented!"));
  }

  @Override public Single<Product> getProduct(String sku, String merchantName) {
    return getServerPaidApp(Long.valueOf(sku)).map(
        paidApp -> productMapper.map(paidApp));
  }

  @Override public Single<List<Authorization>> getAuthorizations(String customerId) {
    return Single.just(Collections.emptyList());
  }

  @Override public Single<PayPalAuthorization> updatePayPalAuthorization(String customerId, String payKey, long paymentMethodId, long authorizationId) {
    return getServerPaidApp(authorizationId).flatMap(response -> {
      if (response.isOk()) {

        if (response.isPaid()) {

          if (response.getPayment()
              .isPaid()) {
            return Observable.just(
                authorizationFactory.create(-1, customerId, paymentMethodId, payPalIcon, "PayPal",
                    null, Authorization.PAYPAL_SDK, false, Authorization.Status.ACTIVE, null, null,
                    null, null))
                .cast(PayPalAuthorization.class)
                .toSingle();
          }

          return cm.aptoide.pt.dataprovider.ws.v3.CreateTransactionRequest.of(response.getPayment()
                  .getMetadata()
                  .getProductId(), (int) paymentMethodId, response.getPath()
                  .getStoreName(), payKey, bodyInterceptorV3, httpClient, converterFactory,
              tokenInvalidator, sharedPreferences, response.getPath()
                  .getVersionCode(), response.getApp()
                  .getName())
              .observe(true)
              .map(transactionResponse -> authorizationFactory.create(authorizationId, customerId,
                  paymentMethodId, payPalIcon, "PayPal", null, Authorization.PAYPAL_SDK, false,
                  Authorization.Status.ACTIVE, payKey, null, response.getApp()
                      .getName(), null))
              .cast(PayPalAuthorization.class)
              .toSingle();
        }

        return Observable.just(
            authorizationFactory.create(-1, customerId, paymentMethodId, payPalIcon, "PayPal", null,
                Authorization.PAYPAL_SDK, false, Authorization.Status.FAILED, null, null, null,
                null))
            .cast(PayPalAuthorization.class)
            .toSingle();
      }

      return Single.error(new IllegalArgumentException(V3.getErrorMessage(response)));
    });
  }

  @Override
  public Single<PayPalAuthorization> createPayPalAuthorization(String customerId, long productId,
      long paymentMethodId) {
    return getServerPaidApp(productId)
        .flatMap(response -> {
          if (response.isOk()) {

            if (response.isPaid()) {

              if (response.getPayment()
                  .isPaid()) {
                return Observable.just(
                    authorizationFactory.create(productId, customerId, paymentMethodId, payPalIcon,
                        "PayPal", null, Authorization.PAYPAL_SDK, false,
                        Authorization.Status.ACTIVE, null, null, null, null))
                    .cast(PayPalAuthorization.class)
                    .toSingle();
              }

              final Price price = new Price(response.getPayment()
                  .getAmount(), response.getPayment()
                  .getPaymentServices()
                  .get(0)
                  .getCurrency(), response.getPayment()
                  .getSymbol());

              return Observable.just(
                  authorizationFactory.create(productId, customerId, paymentMethodId, payPalIcon,
                      "PayPal", null, Authorization.PAYPAL_SDK, false, Authorization.Status.PENDING,
                      null, price, null, null))
                  .cast(PayPalAuthorization.class)
                  .toSingle();
            }

            return Observable.just(
                authorizationFactory.create(productId, customerId, paymentMethodId, payPalIcon,
                    "PayPal", null, Authorization.PAYPAL_SDK, false, Authorization.Status.FAILED,
                    null, null, null, null))
                .cast(PayPalAuthorization.class)
                .toSingle();
          }

          return Single.error(new IllegalArgumentException(V3.getErrorMessage(response)));
        });
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
    return getServerPaidApp(productId)
        .map(response -> {
          if (response.isOk() && response.isPaid() && response.getPayment()
              .isPaid()) {
            return transactionFactory.create(productId, customerId, -1, productId,
                Transaction.Status.COMPLETED);
          }
          return transactionFactory.create(productId, customerId, -1, productId,
              Transaction.Status.FAILED);
        });
  }

  @Override public Single<Transaction> createTransaction(String customerId, long authorizationId,
      long productId, String payload) {
    return getTransaction(customerId, productId);
  }

  private Single<PaidApp> getServerPaidApp(long appId) {
    return GetApkInfoRequest.of(appId, bodyInterceptorV3, httpClient, converterFactory,
        tokenInvalidator, sharedPreferences, resources)
        .observe(true)
        .toSingle();
  }
}
