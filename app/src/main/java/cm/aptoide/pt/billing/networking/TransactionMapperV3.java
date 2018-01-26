package cm.aptoide.pt.billing.networking;

import cm.aptoide.pt.billing.transaction.Transaction;
import cm.aptoide.pt.billing.transaction.TransactionFactory;
import cm.aptoide.pt.dataprovider.model.v3.ErrorResponse;
import cm.aptoide.pt.dataprovider.model.v3.TransactionResponse;
import java.util.List;

public class TransactionMapperV3 {

  private final TransactionFactory transactionFactory;

  public TransactionMapperV3(TransactionFactory transactionFactory) {
    this.transactionFactory = transactionFactory;
  }

  public Transaction map(String customerId, long transactionId,
      TransactionResponse transactionResponse, long productId) {

    final long serviceId = 1;
    if (transactionResponse.hasErrors()) {
      return getErrorTransaction(transactionResponse.getErrors(), customerId, transactionId,
          serviceId, productId);
    }

    final Transaction.Status status;
    switch (transactionResponse.getTransactionStatus()) {
      case "COMPLETED":
        status = Transaction.Status.COMPLETED;
        break;
      case "PENDING_USER_AUTHORIZATION":
      case "CREATED":
        status = Transaction.Status.PENDING_SERVICE_AUTHORIZATION;
        break;
      case "PROCESSING":
      case "PENDING":
        status = Transaction.Status.PROCESSING;
        break;
      case "FAILED":
      case "CANCELED":
      default:
        status = Transaction.Status.FAILED;
    }

    return transactionFactory.create(transactionId, customerId, serviceId, productId, status);
  }

  private Transaction getErrorTransaction(List<ErrorResponse> errors, String customerId,
      long transactionId, long serviceId, long productId) {

    Transaction transaction = transactionFactory.create(transactionId, customerId, serviceId,
        productId,
            Transaction.Status.FAILED);

    if (errors == null || errors.isEmpty()) {
      return transaction;
    }

    final ErrorResponse error = errors.get(0);

    if ("PRODUCT-204".equals(error.code)
        || "PRODUCT-209".equals(error.code)
        || "PRODUCT-214".equals(error.code)) {
      transaction = transactionFactory.create(transactionId, customerId, serviceId, productId,
          Transaction.Status.PENDING_SERVICE_AUTHORIZATION);
    }

    if ("PRODUCT-200".equals(error.code)) {
      transaction = transactionFactory.create(transactionId, customerId, serviceId, productId,
          Transaction.Status.COMPLETED);
    }

    if ("PRODUCT-216".equals(error.code) || "SYS-1".equals(error.code)) {
      transaction = transactionFactory.create(transactionId, customerId, serviceId, productId,
          Transaction.Status.PROCESSING);
    }

    return transaction;
  }
}
