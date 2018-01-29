package cm.aptoide.pt.billing.transaction;

public class TransactionFactory {

  public Transaction create(long id, String customerId, long productId,
      Transaction.Status status, long paymentMethodId) {
    return new Transaction(id, status, customerId, productId, paymentMethodId);
  }
}