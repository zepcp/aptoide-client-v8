package cm.aptoide.pt.billing.transaction;

public class TransactionFactory {

  public Transaction create(long id, String customerId, long authorizationId, long productId,
      Transaction.Status status) {
    return new Transaction(id, status, customerId, productId, authorizationId);
  }
}