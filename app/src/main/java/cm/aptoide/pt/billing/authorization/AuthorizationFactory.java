package cm.aptoide.pt.billing.authorization;

import cm.aptoide.pt.billing.Price;

public class AuthorizationFactory {

  public static final String PAYPAL_SDK = "PAYPAL_SDK";
  public static final String ADYEN_SDK = "ADYEN_SDK";

  public Authorization create(String id, String customerId, String type,
      Authorization.Status status, String metadata, Price price, String description,
      String transactionId, String session, String icon, String name) {

    if (type == null) {
      return new Authorization(id, customerId, status, transactionId, icon, name, description,
          false);
    }

    switch (type) {
      case PAYPAL_SDK:
        return new PayPalAuthorization(id, customerId, status, transactionId, metadata, price,
            description, icon, name, false);
      case ADYEN_SDK:
        return new AdyenAuthorization(id, customerId, status, transactionId, session, metadata,
            icon, name, description, false);
      default:
        return new Authorization(id, customerId, status, transactionId, icon, name, description,
            false);
    }
  }

  public String getType(Authorization authorization) {
    if (authorization instanceof AdyenAuthorization) {
      return ADYEN_SDK;
    }

    if (authorization instanceof PayPalAuthorization) {
      return PAYPAL_SDK;
    }

    return null;
  }
}