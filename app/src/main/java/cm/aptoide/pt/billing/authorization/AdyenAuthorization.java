package cm.aptoide.pt.billing.authorization;

public class AdyenAuthorization extends MetadataAuthorization {

  private String session;

  public AdyenAuthorization(String id, String customerId, Status status, String transactionId,
      String session, String metadata, String icon, String name, String description,
      boolean defaultAuthorization) {
    super(id, customerId, status, transactionId, metadata, icon, name, description,
        defaultAuthorization);
    this.session = session;
  }

  public String getSession() {
    return session;
  }
}
