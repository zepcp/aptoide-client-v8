/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 10/08/2016.
 */

package cm.aptoide.pt.billing.payment;

public class PaymentMethod {

  public static final String PAYPAL = "PAYPAL";
  public static final String CREDIT_CARD = "ADYEN";

  private final long id;
  private final String type;
  private final String name;
  private final String description;
  private final String icon;
  private final boolean defaultPaymentMethod;

  public PaymentMethod(long id, String type, String name, String description, String icon,
      boolean defaultPaymentMethod) {
    this.id = id;
    this.type = type;
    this.name = name;
    this.description = description;
    this.icon = icon;
    this.defaultPaymentMethod = defaultPaymentMethod;
  }

  public long getId() {
    return id;
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

  public String getIcon() {
    return icon;
  }

  public boolean isDefault() {
    return defaultPaymentMethod;
  }

  @Override public String toString() {
    return "PaymentMethod{"
        + "id="
        + id
        + ", type='"
        + type
        + '\''
        + ", name='"
        + name
        + '\''
        + ", description='"
        + description
        + '\''
        + ", icon='"
        + icon
        + '\''
        + ", defaultPaymentMethod="
        + defaultPaymentMethod
        + '}';
  }
}