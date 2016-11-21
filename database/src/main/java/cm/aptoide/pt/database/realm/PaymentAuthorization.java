/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 15/11/2016.
 */

package cm.aptoide.pt.database.realm;

import cm.aptoide.pt.model.v3.ProductPaymentResponse;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by marcelobenites on 15/11/16.
 */

public class PaymentAuthorization extends RealmObject {

  public static final String PAYMENT_ID = "paymentId";
  @PrimaryKey private int paymentId;
  private String url;
  private String redirectUrl;
  private String status;

  public PaymentAuthorization() {
  }

  public PaymentAuthorization(int paymentId, String url, String redirectUrl, String status) {
    this.paymentId = paymentId;
    this.url = url;
    this.redirectUrl = redirectUrl;
    this.status = status;
  }

  public int getPaymentId() {
    return paymentId;
  }

  public String getUrl() {
    return url;
  }

  public String getRedirectUrl() {
    return redirectUrl;
  }

  public String getStatus() {
    return status;
  }
}
