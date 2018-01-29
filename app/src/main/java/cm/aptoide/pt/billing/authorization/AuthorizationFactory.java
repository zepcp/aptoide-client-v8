package cm.aptoide.pt.billing.authorization;

import cm.aptoide.pt.billing.product.Price;

public class AuthorizationFactory {

  public Authorization create(long id, String customerId, long paymentMethodId, String icon,
      String name, String description, String type, boolean defaultAuthorization,
      Authorization.Status status, String metadata, Price price, String productDescription,
      String session) {

    switch (type) {
      case Authorization.PAYPAL_SDK:
        return new PayPalAuthorization(id, customerId, status, metadata, price, description, icon,
            name, defaultAuthorization, Authorization.PAYPAL_SDK, productDescription,
            paymentMethodId);
      case Authorization.ADYEN_SDK:
        return new CreditCardAuthorization(id, customerId, status, session, metadata, icon, name,
            description, defaultAuthorization, Authorization.ADYEN_SDK, paymentMethodId);
      default:
        throw new IllegalArgumentException("Unsupported authorization type: " + type);
    }
  }
}