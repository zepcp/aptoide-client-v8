/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 11/08/2016.
 */

package cm.aptoide.pt.billing.networking;

import cm.aptoide.pt.billing.payment.PaymentMethod;
import cm.aptoide.pt.dataprovider.ws.v7.V7;
import cm.aptoide.pt.dataprovider.ws.v7.billing.GetServicesRequest;
import cm.aptoide.pt.logger.Logger;
import java.util.ArrayList;
import java.util.List;
import rx.Single;

public class PaymentMethodMapper {

  private final int currentAPILevel;
  private final int minimumAPILevelAdyen;
  private final int minimumAPILevelPayPal;

  public PaymentMethodMapper(int currentAPILevel, int minimumAPILevelAdyen,
      int minimumAPILevelPayPal) {
    this.currentAPILevel = currentAPILevel;
    this.minimumAPILevelAdyen = minimumAPILevelAdyen;
    this.minimumAPILevelPayPal = minimumAPILevelPayPal;
  }

  public Single<List<PaymentMethod>> map(GetServicesRequest.ResponseBody response) {
    if (response != null && response.isOk()) {

      final List<PaymentMethod> paymentMethods = new ArrayList<>();
      Logger.d("Billing", "Payment Methods: " + response.getList()
          .size());
      for (GetServicesRequest.ResponseBody.Service service : response.getList()) {
        switch (service.getName()) {
          case PaymentMethod.PAYPAL:
            if (currentAPILevel >= minimumAPILevelPayPal) {
              paymentMethods.add(
                  new PaymentMethod(service.getId(), service.getName(), service.getLabel(),
                      service.getDescription(), service.getIcon(), service.isDefaultService()));
            }
            break;
          case PaymentMethod.CREDIT_CARD:
            if (currentAPILevel >= minimumAPILevelAdyen) {
              paymentMethods.add(
                  new PaymentMethod(service.getId(), service.getName(), service.getLabel(),
                      service.getDescription(), service.getIcon(), service.isDefaultService()));
            }
            break;
        }
      }
      return Single.just(paymentMethods);
    } else {
      return Single.error(new IllegalStateException(V7.getErrorMessage(response)));
    }
  }
}