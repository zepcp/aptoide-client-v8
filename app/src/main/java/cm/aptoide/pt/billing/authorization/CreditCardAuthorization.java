package cm.aptoide.pt.billing.authorization;

public class CreditCardAuthorization extends Authorization {

  private String session;
  private String payload;

  public CreditCardAuthorization(long id, String customerId, Status status, String session,
      String payload, String icon, String name, String description, boolean defaultAuthorization,
      String type, long paymentMethodId) {
    super(id, customerId, status, icon, name, type, description, defaultAuthorization,
        paymentMethodId);
    this.session = session;
    this.payload = payload;
  }

  public String getSession() {
    return session;
  }

  public String getPayload() {
    return payload;
  }
}
