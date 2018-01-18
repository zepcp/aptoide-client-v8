package cm.aptoide.pt.billing.authorization;

public class MetadataAuthorization extends Authorization {

  private final String metadata;

  public MetadataAuthorization(String id, String customerId, Status status, String transactionId,
      String metadata, String icon, String name, String description, boolean defaultAuthorization) {
    super(id, customerId, status, transactionId, icon, name, description, defaultAuthorization);
    this.metadata = metadata;
  }

  public String getMetadata() {
    return metadata;
  }
}
