/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 11/08/2016.
 */

package cm.aptoide.pt.billing.networking;

import android.content.SharedPreferences;
import cm.aptoide.pt.billing.BillingService;
import cm.aptoide.pt.billing.Merchant;
import cm.aptoide.pt.billing.authorization.Authorization;
import cm.aptoide.pt.billing.authorization.AuthorizationFactory;
import cm.aptoide.pt.billing.authorization.CreditCardAuthorization;
import cm.aptoide.pt.billing.authorization.PayPalAuthorization;
import cm.aptoide.pt.billing.payment.PaymentMethod;
import cm.aptoide.pt.billing.product.Product;
import cm.aptoide.pt.billing.purchase.Purchase;
import cm.aptoide.pt.billing.purchase.PurchaseFactory;
import cm.aptoide.pt.billing.transaction.Transaction;
import cm.aptoide.pt.billing.transaction.TransactionFactory;
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
import java.util.Collections;
import java.util.List;
import okhttp3.OkHttpClient;
import retrofit2.Converter;
import rx.Completable;
import rx.Observable;
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
  private final PurchaseFactory purchaseFactory;
  private final AuthenticationPersistence authenticationPersistence;
  private final AuthorizationMapperV7 authorizationMapper;
  private final TransactionMapperV7 transactionMapper;
  private final TransactionFactory transactionFactory;
  private final AuthorizationFactory authorizationFactory;

  public BillingServiceV7(BodyInterceptor<BaseBody> bodyInterceptorV7, OkHttpClient httpClient,
      Converter.Factory converterFactory, TokenInvalidator tokenInvalidator,
      SharedPreferences sharedPreferences, PurchaseMapperV7 purchaseMapper,
      ProductMapperV7 productMapperV7, PaymentMethodMapper serviceMapper,
      PurchaseFactory purchaseFactory, AuthenticationPersistence authenticationPersistence,
      AuthorizationMapperV7 authorizationMapper, TransactionMapperV7 transactionMapper,
      TransactionFactory transactionFactory, AuthorizationFactory authorizationFactory) {
    this.httpClient = httpClient;
    this.converterFactory = converterFactory;
    this.tokenInvalidator = tokenInvalidator;
    this.sharedPreferences = sharedPreferences;
    this.purchaseMapper = purchaseMapper;
    this.productMapperV7 = productMapperV7;
    this.serviceMapper = serviceMapper;
    this.bodyInterceptorV7 = bodyInterceptorV7;
    this.purchaseFactory = purchaseFactory;
    this.authenticationPersistence = authenticationPersistence;
    this.authorizationMapper = authorizationMapper;
    this.transactionMapper = transactionMapper;
    this.transactionFactory = transactionFactory;
    this.authorizationFactory = authorizationFactory;
  }

  @Override public Single<List<PaymentMethod>> getPaymentMethods() {
    return GetServicesRequest.of(sharedPreferences, httpClient, converterFactory, bodyInterceptorV7,
        tokenInvalidator)
        .observe(false, false)
        .toSingle()
        .flatMap(response -> {
          if (response != null && response.isOk()) {
            return Single.just(serviceMapper.map(response.getList()));
          } else {
            return Single.error(new IllegalStateException(V7.getErrorMessage(response)));
          }
        });
  }

  @Override public Single<Merchant> getMerchant(String merchantName, int versionCode) {
    return GetMerchantRequest.of(merchantName, bodyInterceptorV7, httpClient, converterFactory,
        tokenInvalidator, sharedPreferences)
        .observe(false, false)
        .toSingle()
        .flatMap(response -> {
          if (response != null && response.isOk()) {
            return Single.just(new Merchant(response.getData()
                .getId(), "Trivial Drive", response.getData()
                .getName(), versionCode));
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
        .flatMap(response -> {

          if (response.isSuccessful()) {
            return Single.just(purchaseMapper.map(response.body()
                .getList()));
          }

          // If user not logged in return a empty purchase list.
          return Single.<List<Purchase>>just(Collections.emptyList());
        });
  }

  @Override public Single<Purchase> getPurchase(long productId) {
    return GetPurchaseRequest.of(productId, bodyInterceptorV7, httpClient, converterFactory,
        tokenInvalidator, sharedPreferences)
        .observe(true, false)
        .toSingle()
        .flatMap(response -> {

          if (response.isSuccessful()) {
            return Single.just(purchaseMapper.map(response.body()
                .getData()));
          }

          return Single.just(
              purchaseFactory.create(productId, null, null, Purchase.Status.FAILED, null));
        });
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
        .flatMap(response -> {

          if (response.isSuccessful()) {

            if (response.body() != null && response.body()
                .isOk()) {
              return Single.just(authorizationMapper.map(response.body()
                  .getData()
                  .getList()));
            }
          }
          return Single.error(new IllegalStateException(V7.getErrorMessage(response.body())));
        });
  }

  @Override public Single<PayPalAuthorization> updatePayPalAuthorization(String customerId,
      long transactionId, String payKey, long paymentMethodId, long authorizationId) {
    return UpdateAuthorizationRequest.ofPayPal(transactionId, payKey, sharedPreferences, httpClient,
        converterFactory, bodyInterceptorV7, tokenInvalidator)
        .observe(true, false)
        .toSingle()
        .flatMap(response -> {

          if (response.isSuccessful()) {
            final UpdateAuthorizationRequest.ResponseBody responseBody = response.body();
            if (responseBody != null && responseBody.isOk()) {
              return Single.just(
                  (PayPalAuthorization) authorizationMapper.map(responseBody.getData()));
            }
          }

          return Single.just(
              new PayPalAuthorization(authorizationId, customerId, Authorization.Status.FAILED,
                  null, null, null, null, "", false, Authorization.PAYPAL_SDK, null,
                  paymentMethodId, transactionId));
        });
  }

  @Override public Single<Transaction> getTransaction(String customerId, long productId) {
    return authenticationPersistence.getAuthentication()
        .flatMapObservable(authentication -> GetTransactionRequest.of(bodyInterceptorV7, httpClient,
            converterFactory, tokenInvalidator, sharedPreferences, productId,
            authentication.getAccessToken(), customerId)
            .observe())
        .toSingle()
        .flatMap(response -> {

          if (response.isSuccessful()) {

            final GetTransactionRequest.ResponseBody responseBody = response.body();
            if (responseBody != null && responseBody.isOk()) {
              return Single.just(transactionMapper.map(responseBody.getData()));
            }
            return Single.error(new IllegalArgumentException(V7.getErrorMessage(responseBody)));
          }

          if (response.code() == 404) {
            return Single.just(
                transactionFactory.create(-1, customerId, productId, Transaction.Status.NEW, -1));
          }

          return Single.just(
              transactionFactory.create(-1, customerId, productId, Transaction.Status.FAILED, -1));
        });
  }

  @Override public Single<Transaction> createTransaction(String customerId, long productId,
      long authorizationId) {
    return CreateTransactionRequest.of(productId, authorizationId, bodyInterceptorV7, httpClient,
        converterFactory, tokenInvalidator, sharedPreferences)
        .observe(true, false)
        .toSingle()
        .flatMap(response -> {
          if (response != null && response.isOk()) {
            return Single.just(transactionMapper.map(response.getData()));
          }
          return Single.just(
              transactionFactory.create(-1, customerId, productId, Transaction.Status.FAILED, -1));
        });
  }

  @Override
  public Single<PayPalAuthorization> createPayPalAuthorization(String customerId, String token) {
    return Single.error(new IllegalStateException("Not implemented!"));
  }

  @Override public Single<CreditCardAuthorization> updateCreditCardAuthorization(String customerId,
      long authorizationId, String metadata, long paymentMethodId) {
    return UpdateAuthorizationRequest.ofAdyen(authorizationId, metadata, sharedPreferences,
        httpClient, converterFactory, bodyInterceptorV7, tokenInvalidator)
        .observe(true, false)
        .flatMap(response -> {

          if (response.isSuccessful()) {
            final UpdateAuthorizationRequest.ResponseBody responseBody = response.body();
            if (responseBody != null && responseBody.isOk()) {
              return Observable.just(authorizationMapper.map(responseBody.getData()));
            }
          }

          return Observable.just(
              authorizationFactory.create(authorizationId, customerId, Authorization.ADYEN_SDK,
                  Authorization.Status.FAILED, null, null, null, null, null, null, null, false,
                  paymentMethodId, -1));
        })
        .cast(CreditCardAuthorization.class)
        .toSingle();
  }

  @Override public Single<CreditCardAuthorization> createCreditCardAuthorization(String customerId,
      String token, long paymentMethodId) {
    return CreateAuthorizationRequest.ofAdyen(token, sharedPreferences, httpClient,
        converterFactory, bodyInterceptorV7, tokenInvalidator, paymentMethodId)
        .observe(true, false)
        .flatMap(response -> {

          if (response.isSuccessful()) {

            final CreateAuthorizationRequest.ResponseBody responseBody = response.body();

            if (responseBody != null && responseBody.isOk()) {
              return Observable.just(authorizationMapper.map(responseBody.getData()));
            }
          }

          return Observable.just(
              authorizationFactory.create(-1, customerId, Authorization.ADYEN_SDK,
                  Authorization.Status.FAILED, token, null, null, null, null, null, null, false,
                  paymentMethodId, -1));
        })
        .cast(CreditCardAuthorization.class)
        .toSingle();
  }
}
