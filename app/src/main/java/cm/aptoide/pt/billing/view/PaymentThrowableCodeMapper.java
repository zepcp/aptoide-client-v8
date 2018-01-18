/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 26/08/2016.
 */

package cm.aptoide.pt.billing.view;

import cm.aptoide.pt.billing.external.ExternalBillingBinder;
import java.io.IOException;

public class PaymentThrowableCodeMapper {

  public int map(Throwable throwable) {
    int errorCode = ExternalBillingBinder.RESULT_ERROR;

    if (throwable instanceof IOException) {
      errorCode = ExternalBillingBinder.RESULT_SERVICE_UNAVAILABLE;
    }

    if (throwable instanceof IllegalArgumentException) {
      errorCode = ExternalBillingBinder.RESULT_DEVELOPER_ERROR;
    }

    return errorCode;
  }

  public Throwable map(int errorCode) {

    Throwable throwable = new IllegalStateException("Unknown error code " + errorCode);

    if (errorCode == ExternalBillingBinder.RESULT_SERVICE_UNAVAILABLE) {
      throwable = new IOException();
    }

    if (errorCode == ExternalBillingBinder.RESULT_DEVELOPER_ERROR) {
      throwable = new IllegalArgumentException();
    }

    return throwable;
  }
}
