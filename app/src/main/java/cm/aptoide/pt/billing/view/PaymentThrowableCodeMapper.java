/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 26/08/2016.
 */

package cm.aptoide.pt.billing.view;

import cm.aptoide.pt.billing.binder.BillingBinder;
import java.io.IOException;

public class PaymentThrowableCodeMapper {
  public int map(Throwable throwable) {
    int errorCode = BillingBinder.RESULT_ERROR;

    if (throwable instanceof IOException) {
      errorCode = BillingBinder.RESULT_SERVICE_UNAVAILABLE;
    }

    if (throwable instanceof IllegalArgumentException) {
      errorCode = BillingBinder.RESULT_DEVELOPER_ERROR;
    }

    return errorCode;
  }

  public Throwable map(int errorCode) {

    Throwable throwable = new IllegalStateException("Unknown error code " + errorCode);

    if (errorCode == BillingBinder.RESULT_SERVICE_UNAVAILABLE) {
      throwable = new IOException();
    }

    if (errorCode == BillingBinder.RESULT_DEVELOPER_ERROR) {
      throwable = new IllegalArgumentException();
    }

    return throwable;
  }
}
