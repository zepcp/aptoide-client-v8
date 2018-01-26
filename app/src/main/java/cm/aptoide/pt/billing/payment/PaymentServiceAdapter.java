package cm.aptoide.pt.billing.payment;

import cm.aptoide.pt.billing.BillingService;
import cm.aptoide.pt.billing.PaymentService;
import cm.aptoide.pt.billing.authorization.Authorization;
import cm.aptoide.pt.billing.authorization.AuthorizationPersistence;
import cm.aptoide.pt.billing.transaction.Transaction;
import java.util.Map;
import rx.Single;

public class PaymentServiceAdapter {

  private final Map<String, PaymentService> adapters;
  private final AuthorizationPersistence authorizationPersistence;
  private final BillingService billingService;

  public PaymentServiceAdapter(Map<String, PaymentService> adapters, BillingService billingService,
      AuthorizationPersistence authorizationPersistence) {
    this.adapters = adapters;
    this.billingService = billingService;
    this.authorizationPersistence = authorizationPersistence;
  }

  public Single<Transaction> pay(String type, long authorizationId, String customerId,
      long productId, String payload) {
    return adapters.get(type)
        .pay(customerId, productId, authorizationId, billingService, payload);
  }

  public <T> Single<Authorization> authorize(String type, T metadata, String customerId,
      long paymentMethodId) {
    return adapters.get(type)
        .authorize(customerId, metadata, authorizationPersistence, billingService, paymentMethodId);
  }
}