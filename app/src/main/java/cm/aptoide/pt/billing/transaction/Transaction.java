package cm.aptoide.pt.billing.transaction;

public class Transaction {

  private final long id;
  private final String customerId;
  private final long authorizationId;
  private final long productId;
  private final Status status;

  public Transaction(long id, Status status, String customerId, long productId,
      long authorizationId) {
    this.status = status;
    this.id = id;
    this.customerId = customerId;
    this.productId = productId;
    this.authorizationId = authorizationId;
  }

  public String getCustomerId() {
    return customerId;
  }

  public long getAuthorizationId() {
    return authorizationId;
  }

  public long getProductId() {
    return productId;
  }

  public long getId() {
    return id;
  }

  public boolean isNew() {
    return Status.NEW.equals(status);
  }

  public boolean isCompleted() {
    return Status.COMPLETED.equals(status);
  }

  public boolean isProcessing() {
    return Status.PROCESSING.equals(status);
  }

  public boolean isFailed() {
    return Status.FAILED.equals(status);
  }

  public boolean isPendingServiceAuthorization() {
    return Status.PENDING_SERVICE_AUTHORIZATION.equals(status);
  }

  public enum Status {
    NEW, PENDING_SERVICE_AUTHORIZATION, PROCESSING, COMPLETED, FAILED
  }
}