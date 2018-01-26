/*
 * Copyright (c) 2017.
 * Modified by Marcelo Benites on 02/01/2017.
 */

package cm.aptoide.pt.billing.authorization;

public abstract class Authorization {

  public static final String PAYPAL_SDK = "PAYPAL_SDK";
  public static final String ADYEN_SDK = "ADYEN_SDK";
  private final long id;
  private final String customerId;
  private final Status status;
  private final String icon;
  private final String name;
  private final String type;
  private final String description;
  private final boolean defaultAuthorization;
  private final long paymentMethodId;

  public Authorization(long id, String customerId, Status status, String icon, String name,
      String type, String description, boolean defaultAuthorization, long paymentMethodId) {
    this.id = id;
    this.customerId = customerId;
    this.status = status;
    this.icon = icon;
    this.name = name;
    this.type = type;
    this.description = description;
    this.defaultAuthorization = defaultAuthorization;
    this.paymentMethodId = paymentMethodId;
  }

  public long getId() {
    return id;
  }

  public String getCustomerId() {
    return customerId;
  }

  public String getIcon() {
    return icon;
  }

  public String getName() {
    return name;
  }

  public String getType() {
    return type;
  }

  public String getDescription() {
    return description;
  }

  public boolean isProcessing() {
    return Status.PROCESSING.equals(status) || Status.PENDING.equals(status);
  }

  public boolean isFailed() {
    return Status.FAILED.equals(status) || Status.EXPIRED.equals(status);
  }

  public boolean isActive() {
    return Status.ACTIVE.equals(status) || Status.REDEEMED.equals(status);
  }

  public Status getStatus() {
    return status;
  }

  public boolean isDefault() {
    return defaultAuthorization;
  }

  public long getPaymentMethodId() {
    return paymentMethodId;
  }

  public boolean isPending() {
    return Status.PENDING.equals(status);
  }

  public enum Status {
    PENDING, PROCESSING, REDEEMED, ACTIVE, FAILED, EXPIRED
  }

  @Override public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    Authorization that = (Authorization) o;

    if (id != that.id) return false;

    return true;
  }

  @Override public int hashCode() {
    return (int) (id ^ (id >>> 32));
  }

  @Override public String toString() {
    return "Authorization{"
        + "id="
        + id
        + ", customerId='"
        + customerId
        + '\''
        + ", status="
        + status
        + ", icon='"
        + icon
        + '\''
        + ", name='"
        + name
        + '\''
        + ", type='"
        + type
        + '\''
        + ", description='"
        + description
        + '\''
        + ", defaultAuthorization="
        + defaultAuthorization
        + ", paymentMethodId="
        + paymentMethodId
        + '}';
  }
}
