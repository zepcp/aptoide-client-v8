package cm.aptoide.pt.billing.authorization;

import cm.aptoide.pt.billing.Price;

public class AuthorizationFactory {

  public Authorization create(String id, String customerId, String type,
      Authorization.Status status, String metadata, Price price, String description, String session,
      String icon, String name) {

    if (type == null) {
      return new Authorization(id, customerId, status, icon, name, type, description,
          false);
    }

    switch (type) {
      case Authorization.PAYPAL_SDK:
        return new PayPalAuthorization(id, customerId, status, metadata, price, description, icon,
            name, false, Authorization.PAYPAL_SDK);
      case Authorization.ADYEN_SDK:
        return new AdyenAuthorization(id, customerId, status, session, metadata, icon, name,
            description, false, Authorization.ADYEN_SDK);
      default:
        return new Authorization(id, customerId, status, icon, name, type, description,
            false);
    }
  }

  public String getType(Authorization authorization) {
    if (authorization instanceof AdyenAuthorization) {
      return Authorization.ADYEN_SDK;
    }

    if (authorization instanceof PayPalAuthorization) {
      return Authorization.PAYPAL_SDK;
    }

    return null;
  }
}