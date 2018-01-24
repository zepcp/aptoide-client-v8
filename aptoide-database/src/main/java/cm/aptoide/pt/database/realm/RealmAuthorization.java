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
  public static final String CUSTOMER_ID = "customerId";

  @PrimaryKey private long id;
  @Required private String customerId;
  @Required private String type;
  @Required private String metadata;
  private long paymentMethodId;
  private long transactionId;

  public RealmAuthorization() {
  }

  public RealmAuthorization(long id, String customerId, String metadata, String type,
      long paymentMethodId, long transactionId) {
    this.id = id;
    this.metadata = metadata;
    this.customerId = customerId;
    this.type = type;
    this.paymentMethodId = paymentMethodId;
    this.transactionId = transactionId;
  }

  public long getId() {
    return id;
  }

  public String getCustomerId() {
    return customerId;
  }

  public String getMetadata() {
    return metadata;
  }

  public String getType() {
    return type;
  }

  public long getPaymentMethodId() {
    return paymentMethodId;
  }

  public long getTransactionId() {
    return transactionId;
  }
}