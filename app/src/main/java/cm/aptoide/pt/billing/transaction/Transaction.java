package cm.aptoide.pt.billing.transaction;

public class Transaction {

  private final long id;
  private final String customerId;
  private final long productId;
  private final Status status;
  private final long paymentMethodId;

  public Transaction(long id, Status status, String customerId, long productId,
      long paymentMethodId) {
    this.status = status;
    this.id = id;
    this.customerId = customerId;
    this.productId = productId;
    this.paymentMethodId = paymentMethodId;
  }

  public String getCustomerId() {
    return customerId;
  }

  public long getPaymentMethodId() {
    return paymentMethodId;
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

  public enum Status {
    NEW, PENDING_SERVICE_AUTHORIZATION, PROCESSING, COMPLETED, FAILED
  }
}