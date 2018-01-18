/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 18/08/2016.
 */

package cm.aptoide.pt.database.realm;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;

public class RealmAuthorization extends RealmObject {

  public static final String ID = "id";

  @PrimaryKey private String id;
  @Required private String customerId;
  @Required private String status;
  @Required private String type;
  @Required private String metadata;
  @Required private String icon;
  @Required private String name;
  @Required private String description;
  @Required private String transactionId;

  private double amount;
  private String currency;
  private String currencySymbol;

  public RealmAuthorization() {
  }

  public RealmAuthorization(String id, String customerId, String status, String transactionId,
      String metadata, String description, double amount, String currency, String currencySymbol,
      String type, String icon, String name) {
    this.id = id;
    this.transactionId = transactionId;
    this.metadata = metadata;
    this.status = status;
    this.customerId = customerId;
    this.description = description;
    this.amount = amount;
    this.currency = currency;
    this.currencySymbol = currencySymbol;
    this.type = type;
    this.icon = icon;
    this.name = name;
  }

  public String getIcon() {
    return icon;
  }

  public String getName() {
    return name;
  }

  public String getId() {
    return id;
  }

  public String getCustomerId() {
    return customerId;
  }

  public String getMetadata() {
    return metadata;
  }

  public String getStatus() {
    return status;
  }

  public String getTransactionId() {
    return transactionId;
  }

  public double getAmount() {
    return amount;
  }

  public String getCurrency() {
    return currency;
  }

  public String getCurrencySymbol() {
    return currencySymbol;
  }

  public String getDescription() {
    return description;
  }

  public String getType() {
    return type;
  }
}