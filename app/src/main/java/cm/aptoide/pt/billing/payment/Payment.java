package cm.aptoide.pt.billing.payment;

import cm.aptoide.pt.billing.Merchant;
import cm.aptoide.pt.billing.customer.Customer;
import cm.aptoide.pt.billing.product.Product;
import cm.aptoide.pt.billing.purchase.Purchase;
import cm.aptoide.pt.billing.transaction.Transaction;

public class Payment {

  private final Merchant merchant;
  private final Customer customer;
  private final Product product;
  private final Purchase purchase;
  private final Status status;
  private final String payload;
  private final Transaction transaction;

  public Payment(Merchant merchant, Customer customer, Product product, Purchase purchase,
      Status status, String payload, Transaction transaction) {
    this.merchant = merchant;
    this.customer = customer;
    this.product = product;
    this.purchase = purchase;
    this.status = status;
    this.payload = payload;
    this.transaction = transaction;
  }

  public static Payment withProduct(Merchant merchant, Product product, String payload) {
    return new Payment(merchant, null, product, null, Status.LOADING, payload, null);
  }

  public static Payment loading() {
    return new Payment(null, null, null, null, Status.LOADING, null, null);
  }

  public static Payment error() {
    return new Payment(null, null, null, null, Status.LOADING_ERROR, null, null);
  }

  public static Payment withCustomer(Customer customer, Transaction transaction,
      Purchase purchase) {
    return new Payment(null, customer, null, purchase, Status.LOADED, null, transaction);
  }

  public static Payment consolidate(Payment oldPayment, Payment newPayment) {

    Merchant merchant = oldPayment.merchant;
    Customer customer = oldPayment.customer;
    Product product = oldPayment.product;
    Purchase purchase = oldPayment.purchase;
    String payload = oldPayment.payload;
    Transaction transaction = oldPayment.transaction;
    Status status = newPayment.status;

    if (newPayment.getStatus()
        .equals(Status.LOADING_ERROR)) {
      return new Payment(merchant, customer, product, purchase, status, payload, transaction);
    }

    if (newPayment.getStatus()
        .equals(Status.LOADING)) {
      if (newPayment.merchant != null) {
        merchant = newPayment.merchant;
      }

      if (newPayment.product != null) {
        product = newPayment.product;
      }

      if (newPayment.payload != null) {
        payload = newPayment.payload;
      }
    }

    if (newPayment.customer != null) {
      customer = newPayment.customer;
    }

    if (newPayment.purchase != null) {
      purchase = newPayment.purchase;
    }

    if (newPayment.transaction != null) {
      transaction = newPayment.transaction;
    }

    return new Payment(merchant, customer, product, purchase, status, payload, transaction);
  }

  public Merchant getMerchant() {
    return merchant;
  }

  public Customer getCustomer() {
    return customer;
  }

  public Product getProduct() {
    return product;
  }

  public Purchase getPurchase() {
    return purchase;
  }

  public Status getStatus() {
    return status;
  }

  public String getPayload() {
    return payload;
  }

  public boolean isProcessing() {
    return transaction != null && transaction.isProcessing();
  }

  public boolean isCompleted() {
    return transaction != null
        && transaction.isCompleted()
        && purchase != null
        && purchase.isCompleted();
  }

  public boolean isFailed() {
    return (transaction != null && transaction.isFailed()) || (purchase != null
        && purchase.isFailed());
  }

  public boolean isPendingAuthorization() {
    return customer != null
        && customer.getSelectedAuthorization() != null
        && customer.getSelectedAuthorization()
        .isPending();
  }

  public boolean isRedeemed() {
    return customer != null
        && customer.getSelectedAuthorization() != null
        && customer.getSelectedAuthorization()
        .isRedeemed();
  }

  public enum Status {
    LOADING, LOADING_ERROR, LOADED
  }
}
