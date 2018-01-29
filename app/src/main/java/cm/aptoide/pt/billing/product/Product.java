/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 30/08/2016.
 */

package cm.aptoide.pt.billing.product;

public class Product {

  private final long id;
  private final String icon;
  private final String title;
  private final String description;
  private final Price price;
  private final String sku;

  public Product(long id, String icon, String title, String description, Price price, String sku) {
    this.id = id;
    this.icon = icon;
    this.title = title;
    this.description = description;
    this.price = price;
    this.sku = sku;
  }

  public long getId() {
    return id;
  }

  public String getIcon() {
    return icon;
  }

  public String getTitle() {
    return title;
  }

  public Price getPrice() {
    return price;
  }

  public String getDescription() {
    return description;
  }

  public String getSku() {
    return sku;
  }
}