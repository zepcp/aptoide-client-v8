package cm.aptoide.pt.billing.payment;

public class PayPalResult {

  private final boolean pending;
  private final boolean success;
  private final String payKey;
  private final long productId;
  private final long authorizationId;

  public PayPalResult(boolean pending, boolean success, String payKey, long productId,
      long authorizationId) {
    this.pending = pending;
    this.success = success;
    this.payKey = payKey;
    this.productId = productId;
    this.authorizationId = authorizationId;
  }

  public boolean isSuccess() {
    return success;
  }

  public String getPayKey() {
    return payKey;
  }

  public boolean isPending() {
    return pending;
  }

  public long getProductId() {
    return productId;
  }

  public long getAuthorizationId() {
    return authorizationId;
  }
}
