package cm.aptoide.pt.billing.payment;

import cm.aptoide.pt.billing.BillingService;
import cm.aptoide.pt.billing.PaymentService;
import cm.aptoide.pt.billing.authorization.Authorization;
import cm.aptoide.pt.billing.customer.AuthorizationPersistence;
import cm.aptoide.pt.billing.transaction.Transaction;
import rx.Single;

public class PayPalPaymentService implements PaymentService<String> {

  @Override
  public Single<Transaction> pay(String customerId, long productId, long authorizationId,
      BillingService billingService, String payload) {
    return billingService.createTransaction(customerId, authorizationId, productId, payload);
  }

  @Override public Single<Authorization> authorize(String customerId, String payKey,
      AuthorizationPersistence authorizationPersistence, BillingService billingService,
      long paymentMethodId) {
    return Single.error(new IllegalStateException("Not implemented!"));
  }
}
