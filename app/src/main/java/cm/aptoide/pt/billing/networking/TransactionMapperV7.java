/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 16/12/2016.
 */

package cm.aptoide.pt.billing.networking;

import cm.aptoide.pt.billing.transaction.Transaction;
import cm.aptoide.pt.billing.transaction.TransactionFactory;
import cm.aptoide.pt.dataprovider.ws.v7.billing.GetTransactionRequest;

public class TransactionMapperV7 {

  private final TransactionFactory transactionFactory;

  public TransactionMapperV7(TransactionFactory transactionFactory) {
    this.transactionFactory = transactionFactory;
  }

  public Transaction map(GetTransactionRequest.ResponseBody.Transaction response) {
    return transactionFactory.create(response.getId(),
        String.valueOf(response.getUser()
            .getId()), response.getProduct()
            .getId(), Transaction.Status.valueOf(response.getStatus()), response.getService()
            .getId());
  }
}
