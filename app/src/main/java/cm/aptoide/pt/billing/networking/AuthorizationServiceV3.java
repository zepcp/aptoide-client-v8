package cm.aptoide.pt.billing.networking;

import android.content.SharedPreferences;
import android.content.res.Resources;
import cm.aptoide.pt.billing.BillingIdManager;
import cm.aptoide.pt.billing.authorization.Authorization;
import cm.aptoide.pt.billing.authorization.AuthorizationFactory;
import cm.aptoide.pt.billing.authorization.AuthorizationService;
import cm.aptoide.pt.billing.transaction.TransactionPersistence;
import cm.aptoide.pt.dataprovider.interfaces.TokenInvalidator;
import cm.aptoide.pt.dataprovider.ws.BodyInterceptor;
import cm.aptoide.pt.dataprovider.ws.v3.BaseBody;
import cm.aptoide.pt.dataprovider.ws.v3.CreateTransactionRequest;
import cm.aptoide.pt.dataprovider.ws.v3.GetApkInfoRequest;
import okhttp3.OkHttpClient;
import retrofit2.Converter;
import rx.Single;

public class AuthorizationServiceV3 implements AuthorizationService {

  private final AuthorizationFactory authorizationFactory;
  private final AuthorizationMapperV3 authorizationMapper;
  private final TransactionMapperV3 transactionMapper;
  private final TransactionPersistence transactionPersistence;
  private final BodyInterceptor<BaseBody> bodyInterceptorV3;
  private final OkHttpClient httpClient;
  private final Converter.Factory converterFactory;
  private final TokenInvalidator tokenInvalidator;
  private final SharedPreferences sharedPreferences;
  private final Resources resources;
  private final BillingIdManager billingIdManager;
  private final String authorizationName;
  private final String authorizationIcon;

  public AuthorizationServiceV3(AuthorizationFactory authorizationFactory,
      AuthorizationMapperV3 authorizationMapper, TransactionMapperV3 transactionMapper,
      TransactionPersistence transactionPersistence, BodyInterceptor<BaseBody> bodyInterceptorV3,
      OkHttpClient httpClient, Converter.Factory converterFactory,
      TokenInvalidator tokenInvalidator, SharedPreferences sharedPreferences, Resources resources,
      BillingIdManager billingIdManager, String authorizationName, String authorizationIcon) {
    this.authorizationFactory = authorizationFactory;
    this.authorizationMapper = authorizationMapper;
    this.transactionMapper = transactionMapper;
    this.transactionPersistence = transactionPersistence;
    this.bodyInterceptorV3 = bodyInterceptorV3;
    this.httpClient = httpClient;
    this.converterFactory = converterFactory;
    this.tokenInvalidator = tokenInvalidator;
    this.sharedPreferences = sharedPreferences;
    this.resources = resources;
    this.billingIdManager = billingIdManager;
    this.authorizationName = authorizationName;
    this.authorizationIcon = authorizationIcon;
  }

  @Override
  public Single<Authorization> updateAuthorization(String customerId, String authorizationId,
      String metadata) {
    return GetApkInfoRequest.of(billingIdManager.resolveTransactionId(authorizationId),
        bodyInterceptorV3, httpClient, converterFactory, tokenInvalidator, sharedPreferences,
        resources)
        .observe(true)
        .toSingle()
        .flatMap(paidApp -> {

          if (paidApp.isOk()) {
            return CreateTransactionRequest.of(paidApp.getPayment()
                    .getMetadata()
                    .getProductId(), 1, paidApp.getPath()
                    .getStoreName(), metadata, bodyInterceptorV3, httpClient, converterFactory,
                tokenInvalidator, sharedPreferences, 3, paidApp.getApp()
                    .getName())
                .observe(true)
                .toSingle()
                .flatMap(response -> {

                  final Authorization authorization =
                      authorizationMapper.map(billingIdManager.generateAuthorizationId(1),
                          customerId, authorizationId, response, paidApp, authorizationIcon,
                          authorizationName);

                  if (authorization.isActive()) {
                    return transactionPersistence.saveTransaction(
                        transactionMapper.map(customerId, authorizationId, response,
                            billingIdManager.generateProductId(
                                billingIdManager.resolveTransactionId(authorizationId))))
                        .andThen(Single.just(authorization));
                  }

                  return Single.just(authorization);
                });
          }

          return Single.just(
              authorizationFactory.create(billingIdManager.generateAuthorizationId(1), customerId,
                  Authorization.PAYPAL_SDK, Authorization.Status.FAILED, null, null, null,
                  null, authorizationIcon, authorizationName));
        });
  }
}