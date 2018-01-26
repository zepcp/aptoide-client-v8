/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 16/12/2016.
 */

package cm.aptoide.pt.billing.networking;

import cm.aptoide.pt.billing.transaction.Transaction;
import cm.aptoide.pt.billing.transaction.TransactionFactory;
import cm.aptoide.pt.dataprovider.ws.v7.V7;
import cm.aptoide.pt.dataprovider.ws.v7.billing.GetTransactionRequest;
import retrofit2.Response;
import rx.Single;

public class TransactionMapperV7 {

  private final TransactionFactory transactionFactory;

  public TransactionMapperV7(TransactionFactory transactionFactory) {
    this.transactionFactory = transactionFactory;
  }

  public Single<Transaction> map(Response<GetTransactionRequest.ResponseBody> response,
      int transactionId, String customerId, long authorizationId, long productId) {

    if (response.code() == 404) {
      return Single.just(
          transactionFactory.create(transactionId, customerId, authorizationId, productId,
              Transaction.Status.NEW));
    }

    if (response.code() == 409) {
      return Single.just(
          transactionFactory.create(transactionId, customerId, authorizationId, productId,
              Transaction.Status.FAILED));
    }

    if (response.isSuccessful() && response.body() != null && response.body()
        .isOk()) {
      final GetTransactionRequest.ResponseBody.Transaction transaction = response.body()
          .getData();

      return Single.just(transactionFactory.create(transaction.getId(), String.valueOf(
          transaction.getUser()
              .getId()), transaction.getService()
          .getId(), transaction.getProduct()
          .getId(), Transaction.Status.valueOf(transaction.getStatus())));
    }

    return Single.error(new IllegalStateException(V7.getErrorMessage(response.body())));
  }
}
