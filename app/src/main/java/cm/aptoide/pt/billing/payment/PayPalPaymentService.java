package cm.aptoide.pt.billing.payment;

import cm.aptoide.pt.billing.PaymentService;
import cm.aptoide.pt.billing.authorization.Authorization;
import cm.aptoide.pt.billing.authorization.AuthorizationPersistence;
import cm.aptoide.pt.billing.authorization.AuthorizationService;
import cm.aptoide.pt.billing.transaction.Transaction;
import cm.aptoide.pt.billing.transaction.TransactionService;
import rx.Single;

public class PayPalPaymentService implements PaymentService<String> {

  @Override
  public Single<Transaction> pay(String customerId, String productId, String authorizationId,
      TransactionService transactionService) {
    return transactionService.createTransaction(customerId, productId, authorizationId);
  }

  @Override
  public Single<Authorization> authorize(String customerId, String authorizationId, String payKey,
      AuthorizationService authorizationService,
      AuthorizationPersistence authorizationPersistence) {
    return Single.just(null);
  }
}
