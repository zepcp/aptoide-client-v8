package cm.aptoide.pt.billing.networking;

import android.content.SharedPreferences;
import cm.aptoide.pt.billing.BillingIdManager;
import cm.aptoide.pt.billing.authorization.Authorization;
import cm.aptoide.pt.billing.authorization.AuthorizationFactory;
import cm.aptoide.pt.billing.authorization.AuthorizationService;
import cm.aptoide.pt.dataprovider.interfaces.TokenInvalidator;
import cm.aptoide.pt.dataprovider.ws.BodyInterceptor;
import cm.aptoide.pt.dataprovider.ws.v7.BaseBody;
import cm.aptoide.pt.dataprovider.ws.v7.billing.UpdateAuthorizationRequest;
import okhttp3.OkHttpClient;
import retrofit2.Converter;
import rx.Single;

public class AuthorizationServiceV7 implements AuthorizationService {

  private final AuthorizationMapperV7 authorizationMapper;
  private final OkHttpClient httpClient;
  private final Converter.Factory converterFactory;
  private final TokenInvalidator tokenInvalidator;
  private final SharedPreferences sharedPreferences;
  private final BodyInterceptor<BaseBody> bodyInterceptorV7;
  private final BillingIdManager billingIdManager;
  private final AuthorizationFactory authorizationFactory;

  public AuthorizationServiceV7(AuthorizationMapperV7 authorizationMapper, OkHttpClient httpClient,
      Converter.Factory converterFactory, TokenInvalidator tokenInvalidator,
      SharedPreferences sharedPreferences, BodyInterceptor<BaseBody> bodyInterceptorV7,
      BillingIdManager billingIdManager, AuthorizationFactory authorizationFactory) {
    this.authorizationMapper = authorizationMapper;
    this.httpClient = httpClient;
    this.converterFactory = converterFactory;
    this.tokenInvalidator = tokenInvalidator;
    this.sharedPreferences = sharedPreferences;
    this.bodyInterceptorV7 = bodyInterceptorV7;
    this.billingIdManager = billingIdManager;
    this.authorizationFactory = authorizationFactory;
  }

  @Override
  public Single<Authorization> updateAuthorization(String customerId, String authorizationId,
      String metadata) {
    return UpdateAuthorizationRequest.of(authorizationId,
        metadata, sharedPreferences, httpClient, converterFactory, bodyInterceptorV7,
        tokenInvalidator)
        .observe(true, false)
        .toSingle()
        .flatMap(response -> {
          if (response != null && response.isOk()) {
            return Single.just(authorizationMapper.map(response.getData()));
          }
          return Single.just(
              authorizationFactory.create(billingIdManager.generateAuthorizationId(), customerId,
                  null, Authorization.Status.FAILED, null, null, null, null, "", ""));
        });
  }
}
