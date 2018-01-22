package cm.aptoide.pt.billing.authorization;

public class AdyenAuthorization extends Authorization {

  private String session;
  private String payload;

  public AdyenAuthorization(String id, String customerId, Status status, String session,
      String payload, String icon, String name, String description, boolean defaultAuthorization,
      String type) {
    super(id, customerId, status, icon, name, type, description,
        defaultAuthorization);
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
