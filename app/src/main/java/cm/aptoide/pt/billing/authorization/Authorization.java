/*
 * Copyright (c) 2017.
 * Modified by Marcelo Benites on 02/01/2017.
 */

package cm.aptoide.pt.billing.authorization;

public class Authorization {

  public static final String PAYPAL_SDK = "PAYPAL_SDK";
  public static final String ADYEN_SDK = "ADYEN_SDK";
  private final String id;
  private final String customerId;
  private final Status status;
  private final String icon;
  private final String name;
  private final String type;
  private final String description;
  private final boolean defaultAuthorization;

  public Authorization(String id, String customerId, Status status, String icon, String name,
      String type, String description, boolean defaultAuthorization) {
    this.id = id;
    this.customerId = customerId;
    this.status = status;
    this.icon = icon;
    this.name = name;
    this.type = type;
    this.description = description;
    this.defaultAuthorization = defaultAuthorization;
  }

  public String getId() {
    return id;
  }

  public String getCustomerId() {
    return customerId;
  }

  public boolean isPending() {
    return Status.PENDING.equals(status);
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
    return Status.PENDING_SYNC.equals(status) || Status.PROCESSING.equals(status);
  }

  public boolean isPendingSync() {
    return Status.PENDING_SYNC.equals(status);
  }

  public boolean isFailed() {
    return Status.FAILED.equals(status) || Status.EXPIRED.equals(status);
  }

  public boolean isActive() {
    return Status.ACTIVE.equals(status) || Status.REDEEMED.equals(status);
  }

  public boolean isRedeemed() {
    return Status.REDEEMED.equals(status);
  }

  public Status getStatus() {
    return status;
  }

  public boolean isDefault() {
    return defaultAuthorization;
  }

  public enum Status {
    NEW, PENDING, PENDING_SYNC, PROCESSING, REDEEMED, ACTIVE, FAILED, EXPIRED
  }
}
