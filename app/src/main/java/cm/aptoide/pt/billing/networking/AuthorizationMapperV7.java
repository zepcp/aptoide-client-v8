/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 16/12/2016.
 */

package cm.aptoide.pt.billing.networking;

import cm.aptoide.pt.billing.Price;
import cm.aptoide.pt.billing.authorization.Authorization;
import cm.aptoide.pt.billing.authorization.AuthorizationFactory;
import cm.aptoide.pt.dataprovider.ws.v7.billing.GetAuthorizationRequest;
import java.util.ArrayList;
import java.util.List;

public class AuthorizationMapperV7 {

  private final AuthorizationFactory authorizationFactory;

  public AuthorizationMapperV7(AuthorizationFactory authorizationFactory) {
    this.authorizationFactory = authorizationFactory;
  }

  public Authorization map(GetAuthorizationRequest.ResponseBody.Authorization response) {

    Price price = null;
    if (response.getPrice() != null) {
      price = new Price(response.getPrice()
          .getAmount(), response.getPrice()
          .getCurrency(), response.getPrice()
          .getCurrencySymbol());
    }

    final GetAuthorizationRequest.ResponseBody.Authorization.Metadata metadata = response.getData();
    String productDescription = null;
    String session = null;
    if (metadata != null) {
      productDescription = metadata.getDescription();
      session = metadata.getSession();
    }

    return authorizationFactory.create(response.getId(),
        String.valueOf(response.getUser()
            .getId()), response.getType(), Authorization.Status.valueOf(response.getStatus()), null,
        price, productDescription, session, response.getIcon(), response.getName(),
        response.getDescription(), response.isDefaultAuthorization(), response.getServiceId(), -1);
  }

  public List<Authorization> map(
      List<GetAuthorizationRequest.ResponseBody.Authorization> authorizations) {
    final List<Authorization> result = new ArrayList<>();

    for (GetAuthorizationRequest.ResponseBody.Authorization authorization : authorizations) {
      result.add(map(authorization));
    }
    return result;
  }
}