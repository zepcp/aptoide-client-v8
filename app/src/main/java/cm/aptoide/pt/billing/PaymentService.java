package cm.aptoide.pt.billing;

import cm.aptoide.pt.billing.authorization.Authorization;
import cm.aptoide.pt.billing.authorization.AuthorizationPersistence;
import cm.aptoide.pt.billing.authorization.AuthorizationService;
import cm.aptoide.pt.billing.transaction.Transaction;
import cm.aptoide.pt.billing.transaction.TransactionService;
import rx.Single;

public interface PaymentService<T> {

  public Single<Transaction> pay(String customerId, String productId,
      String authorizationId, TransactionService transactionService);

  public Single<Authorization> authorize(String customerId, String authorizationId, T metadata,
      AuthorizationService authorizationService, AuthorizationPersistence authorizationPersistence);
}