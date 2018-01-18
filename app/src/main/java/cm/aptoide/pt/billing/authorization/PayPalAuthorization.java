/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 10/08/2016.
 */

package cm.aptoide.pt.billing.authorization;

import cm.aptoide.pt.billing.Price;

public class PayPalAuthorization extends MetadataAuthorization {

  private final Price price;

  public PayPalAuthorization(String id, String customerId, Status status, String transactionId,
      String metadata, Price price, String description, String icon, String name,
      boolean defaultAuthorization) {
    super(id, customerId, status, transactionId, metadata, icon, name, description,
        defaultAuthorization);
    this.price = price;
  }

  public Price getPrice() {
    return price;
  }

}
