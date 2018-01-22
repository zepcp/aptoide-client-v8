/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 10/08/2016.
 */

package cm.aptoide.pt.billing.authorization;

import cm.aptoide.pt.billing.Price;

public class PayPalAuthorization extends Authorization {

  private final String metadata;
  private final Price price;

  public PayPalAuthorization(String id, String customerId, Status status, String metadata,
      Price price, String description, String icon, String name, boolean defaultAuthorization,
      String type) {
    super(id, customerId, status, icon, name, type, description, defaultAuthorization);
    this.metadata = metadata;
    this.price = price;
  }

  public Price getPrice() {
    return price;
  }

  public String getPayKey() {
    return metadata;
  }
}
