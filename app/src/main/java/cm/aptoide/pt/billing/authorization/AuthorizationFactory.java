package cm.aptoide.pt.billing.authorization;

import cm.aptoide.pt.billing.Price;

public class AuthorizationFactory {

  public Authorization create(long id, String customerId, String type, Authorization.Status status,
      String metadata, Price price, String productDescription, String session, String icon,
      String name, String description, boolean defaultAuthorization, long paymentMethodId,
      long transactionId) {

    if (type == null) {
      return new Authorization(id, customerId, status, icon, name, type, productDescription, false,
          paymentMethodId);
    }

    switch (type) {
      case Authorization.PAYPAL_SDK:
        return new PayPalAuthorization(id, customerId, status, metadata, price, description, icon,
            name, defaultAuthorization, Authorization.PAYPAL_SDK, productDescription,
            paymentMethodId, transactionId);
      case Authorization.ADYEN_SDK:
        return new CreditCardAuthorization(id, customerId, status, session, metadata, icon, name,
            description, defaultAuthorization, Authorization.ADYEN_SDK, paymentMethodId);
      default:
        return new Authorization(id, customerId, status, icon, name, type, description,
            defaultAuthorization, paymentMethodId);
    }
  }

  public String getType(Authorization authorization) {
    if (authorization instanceof CreditCardAuthorization) {
      return Authorization.ADYEN_SDK;
    }

    if (authorization instanceof PayPalAuthorization) {
      return Authorization.PAYPAL_SDK;
    }

    return null;
  }
}