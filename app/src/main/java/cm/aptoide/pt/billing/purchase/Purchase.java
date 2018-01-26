package cm.aptoide.pt.billing.purchase;

public class Purchase {

  private final Status status;
  private final long productId;
  private final String sku;
  private final String signature;
  private final String signatureData;

  public Purchase(Status status, long productId, String sku, String signature,
      String signatureData) {
    this.status = status;
    this.productId = productId;
    this.sku = sku;
    this.signature = signature;
    this.signatureData = signatureData;
  }

  public String getSku() {
    return sku;
  }

  public Status getStatus() {
    return status;
  }

  public long getProductId() {
    return productId;
  }

  public String getSignature() {
    return signature;
  }

  public String getSignatureData() {
    return signatureData;
  }

  public boolean isCompleted() {
    return Status.COMPLETED.equals(status);
  }

  public boolean isFailed() {
    return Status.FAILED.equals(status);
  }

  public static enum Status {
    COMPLETED, FAILED
  }
}