/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 11/08/2016.
 */

package cm.aptoide.pt.billing.networking;

import android.content.SharedPreferences;
import cm.aptoide.pt.billing.BillingService;
import cm.aptoide.pt.billing.Merchant;
import cm.aptoide.pt.billing.authorization.Authorization;
import cm.aptoide.pt.billing.authorization.CreditCardAuthorization;
import cm.aptoide.pt.billing.authorization.PayPalAuthorization;
import cm.aptoide.pt.billing.payment.PaymentMethod;
import cm.aptoide.pt.billing.product.Product;
import cm.aptoide.pt.billing.purchase.Purchase;
import cm.aptoide.pt.billing.transaction.Transaction;
import cm.aptoide.pt.dataprovider.interfaces.TokenInvalidator;
import cm.aptoide.pt.dataprovider.ws.BodyInterceptor;
import cm.aptoide.pt.dataprovider.ws.v7.BaseBody;
import cm.aptoide.pt.dataprovider.ws.v7.V7;
import cm.aptoide.pt.dataprovider.ws.v7.billing.CreateAuthorizationRequest;
import cm.aptoide.pt.dataprovider.ws.v7.billing.CreateTransactionRequest;
import cm.aptoide.pt.dataprovider.ws.v7.billing.DeletePurchaseRequest;
import cm.aptoide.pt.dataprovider.ws.v7.billing.GetAuthorizationsRequest;
import cm.aptoide.pt.dataprovider.ws.v7.billing.GetMerchantRequest;
import cm.aptoide.pt.dataprovider.ws.v7.billing.GetProductsRequest;
import cm.aptoide.pt.dataprovider.ws.v7.billing.GetPurchaseRequest;
import cm.aptoide.pt.dataprovider.ws.v7.billing.GetPurchasesRequest;
import cm.aptoide.pt.dataprovider.ws.v7.billing.GetServicesRequest;
import cm.aptoide.pt.dataprovider.ws.v7.billing.GetTransactionRequest;
import cm.aptoide.pt.dataprovider.ws.v7.billing.UpdateAuthorizationRequest;
import cm.aptoide.pt.networking.AuthenticationPersistence;
import java.util.List;
import okhttp3.OkHttpClient;
import retrofit2.Converter;
import rx.Completable;
import rx.Single;

public class BillingServiceV7 implements BillingService {

  private final OkHttpClient httpClient;
  private final Converter.Factory converterFactory;
  private final TokenInvalidator tokenInvalidator;
  private final SharedPreferences sharedPreferences;
  private final PurchaseMapperV7 purchaseMapper;
  private final ProductMapperV7 productMapperV7;
  private final PaymentMethodMapper serviceMapper;
  private final BodyInterceptor<BaseBody> bodyInterceptorV7;
  private final AuthenticationPersistence authenticationPersistence;
  private final AuthorizationMapperV7 authorizationMapper;
  private final TransactionMapperV7 transactionMapper;

  public BillingServiceV7(BodyInterceptor<BaseBody> bodyInterceptorV7, OkHttpClient httpClient,
      Converter.Factory converterFactory, TokenInvalidator tokenInvalidator,
      SharedPreferences sharedPreferences, PurchaseMapperV7 purchaseMapper,
      ProductMapperV7 productMapperV7, PaymentMethodMapper serviceMapper,
      AuthenticationPersistence authenticationPersistence,
      AuthorizationMapperV7 authorizationMapper, TransactionMapperV7 transactionMapper) {
    this.httpClient = httpClient;
    this.converterFactory = converterFactory;
    this.tokenInvalidator = tokenInvalidator;
    this.sharedPreferences = sharedPreferences;
    this.purchaseMapper = purchaseMapper;
    this.productMapperV7 = productMapperV7;
    this.serviceMapper = serviceMapper;
    this.bodyInterceptorV7 = bodyInterceptorV7;
    this.authenticationPersistence = authenticationPersistence;
    this.authorizationMapper = authorizationMapper;
    this.transactionMapper = transactionMapper;
  }

  @Override public Single<List<PaymentMethod>> getPaymentMethods() {
    return GetServicesRequest.of(sharedPreferences, httpClient, converterFactory, bodyInterceptorV7,
        tokenInvalidator)
        .observe(false, false)
        .toSingle()
        .flatMap(response -> serviceMapper.map(response));
  }

  @Override public Single<Merchant> getMerchant(String merchantName, int versionCode) {
    return GetMerchantRequest.of(merchantName, bodyInterceptorV7, httpClient, converterFactory,
        tokenInvalidator, sharedPreferences)
        .observe(false, false)
        .toSingle()
        .flatMap(response -> {
          if (response != null && response.isOk()) {
            return Single.just(new Merchant(response.getData()
                .getId(), response.getData()
                .getName(), response.getData()
                .getPackageName(), versionCode));
          } else {
            return Single.error(new IllegalArgumentException(V7.getErrorMessage(response)));
          }
        });
  }

  @Override public Completable deletePurchase(long purchaseId) {
    return DeletePurchaseRequest.of(purchaseId, httpClient, converterFactory, bodyInterceptorV7,
        tokenInvalidator, sharedPreferences)
        .observe(true, false)
        .first()
        .toSingle()
        .flatMapCompletable(response -> {
          if (response != null && response.isOk()) {
            return Completable.complete();
          }
          return Completable.error(new IllegalArgumentException(V7.getErrorMessage(response)));
        });
  }

  @Override public Single<List<Purchase>> getPurchases(String merchantName) {
    return GetPurchasesRequest.of(merchantName, bodyInterceptorV7, httpClient, converterFactory,
        tokenInvalidator, sharedPreferences)
        .observe(true, false)
        .toSingle()
        .flatMap(response -> purchaseMapper.map(response));
  }

  @Override public Single<Purchase> getPurchase(long productId) {
    return GetPurchaseRequest.of(productId, bodyInterceptorV7, httpClient, converterFactory,
        tokenInvalidator, sharedPreferences)
        .observe(true, false)
        .toSingle()
        .flatMap(response -> purchaseMapper.map(response, productId));
  }

  @Override public Single<List<Product>> getProducts(String merchantName, List<String> skus) {
    return GetProductsRequest.of(merchantName, skus, bodyInterceptorV7, httpClient,
        converterFactory, tokenInvalidator, sharedPreferences)
        .observe(false, false)
        .first()
        .toSingle()
        .flatMap(response -> {
          if (response != null && response.isOk()) {
            return Single.just(productMapperV7.map(response.getList()));
          } else {
            return Single.<List<Product>>error(
                new IllegalStateException(V7.getErrorMessage(response)));
          }
        });
  }

  @Override public Single<Product> getProduct(String sku, String merchantName) {
    return GetProductsRequest.of(merchantName, sku, bodyInterceptorV7, httpClient, converterFactory,
        tokenInvalidator, sharedPreferences)
        .observe(false, false)
        .first()
        .toSingle()
        .flatMap(response -> {
          if (response != null && response.isOk()) {
            return Single.just(productMapperV7.map(response.getData()));
          } else {
            return Single.error(new IllegalArgumentException("No product found for sku: " + sku));
          }
        });
  }

  @Override public Single<List<Authorization>> getAuthorizations(String customerId) {
    return authenticationPersistence.getAuthentication()
        .flatMapObservable(
            authentication -> GetAuthorizationsRequest.of(sharedPreferences, httpClient,
                converterFactory, bodyInterceptorV7, tokenInvalidator,
                authentication.getAccessToken(), customerId)
                .observe())
        .toSingle()
        .flatMap(response -> authorizationMapper.map(response));
  }

  @Override
  public Single<PayPalAuthorization> updatePayPalAuthorization(String customerId, String payKey,
      long paymentMethodId, long authorizationId) {
    return UpdateAuthorizationRequest.of(authorizationId, payKey, sharedPreferences, httpClient,
        converterFactory, bodyInterceptorV7, tokenInvalidator)
        .observe(true, false)
        .flatMapSingle(
            response -> authorizationMapper.map(authorizationId, customerId, paymentMethodId,
                Authorization.PAYPAL_SDK, response))
        .cast(PayPalAuthorization.class)
        .toSingle();
  }

  @Override public Single<Transaction> getTransaction(String customerId, long productId) {
    return authenticationPersistence.getAuthentication()
        .flatMapObservable(authentication -> GetTransactionRequest.of(bodyInterceptorV7, httpClient,
            converterFactory, tokenInvalidator, sharedPreferences, productId,
            authentication.getAccessToken(), customerId)
            .observe())
        .toSingle()
        .flatMap(response -> transactionMapper.map(response, -1, customerId, -1, productId));
  }

  @Override public Single<Transaction> createTransaction(String customerId, long authorizationId,
      long productId, String payload) {
    return CreateTransactionRequest.of(productId, authorizationId, bodyInterceptorV7, httpClient,
        converterFactory, tokenInvalidator, sharedPreferences, payload)
        .observe(true, false)
        .toSingle()
        .flatMap(response -> transactionMapper.map(response, -1, customerId, authorizationId,
            productId));
  }

  @Override
  public Single<PayPalAuthorization> createPayPalAuthorization(String customerId, long productId,
      long paymentMethodId) {
    return CreateAuthorizationRequest.ofPayPal(productId, sharedPreferences, httpClient,
        converterFactory, bodyInterceptorV7, tokenInvalidator, paymentMethodId)
        .observe(true, false)
        .flatMapSingle(response -> authorizationMapper.map(-1, customerId, paymentMethodId,
            Authorization.PAYPAL_SDK, response))
        .cast(PayPalAuthorization.class)
        .toSingle();
  }

  @Override public Single<CreditCardAuthorization> updateCreditCardAuthorization(String customerId,
      long authorizationId, String metadata, long paymentMethodId) {
    return UpdateAuthorizationRequest.of(authorizationId, metadata, sharedPreferences, httpClient,
        converterFactory, bodyInterceptorV7, tokenInvalidator)
        .observe(true, false)
        .flatMapSingle(
            response -> authorizationMapper.map(authorizationId, customerId, paymentMethodId,
                Authorization.ADYEN_SDK, response))
        .cast(CreditCardAuthorization.class)
        .toSingle();
  }

  @Override public Single<CreditCardAuthorization> createCreditCardAuthorization(String customerId,
      String token, long paymentMethodId) {
    return CreateAuthorizationRequest.ofAdyen(token, sharedPreferences, httpClient,
        converterFactory, bodyInterceptorV7, tokenInvalidator, paymentMethodId)
        .observe(true, false)
        .flatMapSingle(response -> authorizationMapper.map(-1, customerId, paymentMethodId,
            Authorization.ADYEN_SDK, response))
        .cast(CreditCardAuthorization.class)
        .toSingle();
  }
}
