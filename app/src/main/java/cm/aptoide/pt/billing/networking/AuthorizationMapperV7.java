/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 16/12/2016.
 */

package cm.aptoide.pt.billing.networking;

import cm.aptoide.pt.billing.authorization.Authorization;
import cm.aptoide.pt.billing.authorization.AuthorizationFactory;
import cm.aptoide.pt.billing.product.Price;
import cm.aptoide.pt.dataprovider.ws.v7.V7;
import cm.aptoide.pt.dataprovider.ws.v7.billing.CreateAuthorizationRequest;
import cm.aptoide.pt.dataprovider.ws.v7.billing.GetAuthorizationsRequest;
import java.util.ArrayList;
import java.util.List;
import retrofit2.Response;
import rx.Single;

public class AuthorizationMapperV7 {

  private final AuthorizationFactory authorizationFactory;

  public AuthorizationMapperV7(AuthorizationFactory authorizationFactory) {
    this.authorizationFactory = authorizationFactory;
  }

  public Single<Authorization> map(long authorizationId, String customerId, long paymentMethodId,
      String type, Response<CreateAuthorizationRequest.ResponseBody> response) {

    if (response.code() == 409) {

      return Single.just(
          authorizationFactory.create(authorizationId, customerId, paymentMethodId, null, null,
              null, type, false, Authorization.Status.FAILED, null, null, null, null));
    }

    if (response.isSuccessful() && response.body() != null && response.body()
        .isOk()) {
      return Single.just(map(response.body()
          .getData()));
    }

    return Single.error(new IllegalStateException(V7.getErrorMessage(response.body())));
  }

  public Single<List<Authorization>> map(Response<GetAuthorizationsRequest.ResponseBody> response) {

    if (response.isSuccessful()) {

      if (response.body() != null && response.body()
          .isOk()) {
        final List<Authorization> result = new ArrayList<>();

        for (GetAuthorizationsRequest.ResponseBody.Authorization authorization : response.body()
            .getData()
            .getList()) {
          result.add(map(authorization));
        }
        return Single.just(result);
      }
    }
    return Single.error(new IllegalStateException(V7.getErrorMessage(response.body())));
  }

  private Authorization map(GetAuthorizationsRequest.ResponseBody.Authorization authorization) {

    Price price = null;
    if (authorization.getPrice() != null) {
      price = new Price(authorization.getPrice()
          .getAmount(), authorization.getPrice()
          .getCurrency(), authorization.getPrice()
          .getCurrencySymbol());
    }

    final GetAuthorizationsRequest.ResponseBody.Authorization.Metadata metadata =
        authorization.getData();

    String productDescription = null;
    String session = null;
    if (metadata != null) {
      productDescription = metadata.getDescription();
      session = metadata.getSession();
    }

    return authorizationFactory.create(authorization.getId(), String.valueOf(authorization.getUser()
            .getId()), authorization.getServiceId(), authorization.getIcon(), authorization.getName(),
        authorization.getDescription(), authorization.getType(),
        authorization.isDefaultAuthorization(),
        Authorization.Status.valueOf(authorization.getStatus()), null, price, productDescription,
        session);
  }
}