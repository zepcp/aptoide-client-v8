/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 22/11/2016.
 */

package cm.aptoide.pt.billing.sync;

import cm.aptoide.pt.billing.UserPersistence;
import cm.aptoide.pt.billing.transaction.TransactionPersistence;
import cm.aptoide.pt.billing.transaction.TransactionService;
import cm.aptoide.pt.sync.Sync;
import rx.Completable;

public class TransactionsSync extends Sync {

  private final UserPersistence userPersistence;
  private final TransactionPersistence transactionPersistence;
  private final TransactionService transactionService;
  private final String productId;

  public TransactionsSync(String id, UserPersistence userPersistence,
      TransactionPersistence transactionPersistence, TransactionService transactionService,
      boolean periodic, boolean exact, long interval, long trigger, String productId) {
    super(id, periodic, exact, trigger, interval);
    this.userPersistence = userPersistence;
    this.transactionPersistence = transactionPersistence;
    this.transactionService = transactionService;
    this.productId = productId;
  }

  @Override public Completable execute() {
    return userPersistence.getUser()
        .first().toSingle()
        .flatMap(customer -> transactionService.getTransaction(customer.getId(), productId))
        .flatMapCompletable(transaction -> transactionPersistence.saveTransaction(transaction));
  }
}
