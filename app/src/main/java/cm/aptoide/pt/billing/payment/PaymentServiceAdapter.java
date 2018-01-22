package cm.aptoide.pt.billing.payment;

import cm.aptoide.pt.billing.PaymentService;
import cm.aptoide.pt.billing.authorization.Authorization;
import cm.aptoide.pt.billing.authorization.AuthorizationPersistence;
import cm.aptoide.pt.billing.authorization.AuthorizationService;
import cm.aptoide.pt.billing.transaction.Transaction;
import cm.aptoide.pt.billing.transaction.TransactionService;
import java.util.Map;
import rx.Single;

public class PaymentServiceAdapter {

  private final Map<String, PaymentService> adapters;
  private final AuthorizationService authorizationService;
  private final TransactionService transactionService;
  private final AuthorizationPersistence authorizationPersistence;

  public PaymentServiceAdapter(Map<String, PaymentService> adapters,
      AuthorizationService authorizationService, TransactionService transactionService,
      AuthorizationPersistence authorizationPersistence) {
    this.adapters = adapters;
    this.authorizationService = authorizationService;
    this.transactionService = transactionService;
    this.authorizationPersistence = authorizationPersistence;
  }

  public Single<Transaction> createTransaction(String type, String serviceId, String payload,
      String customerId, String productId) {
    return adapters.get(type)
        .pay(customerId, productId, serviceId, transactionService);
  }

  public <T> Single<Authorization> authorize(String type, T metadata, String customerId,
      String transactionId) {
    return adapters.get(type)
        .authorize(customerId, transactionId, metadata, authorizationService,
            authorizationPersistence);
  }
}