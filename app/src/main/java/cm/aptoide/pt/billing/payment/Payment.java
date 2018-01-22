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

  public Transaction getTransaction() {
    return transaction;
  }

  public static Payment loaded(Merchant merchant, Product product, Purchase purchase,
      String payload) {
    return new Payment(merchant, null, product, purchase, Status.LOADED, payload, null);
  }

  public static Payment loading() {
    return new Payment(null, null, null, null, Status.LOADING, null, null);
  }

  public static Payment error() {
    return new Payment(null, null, null, null, Status.LOADING_ERROR, null, null);
  }

  public static Payment withCustomer(Customer customer) {
    return new Payment(null, customer, null, null, null, null, null);
  }

  public static Payment withTransaction(Transaction transaction) {
    return new Payment(null, null, null, null, null, null, transaction);
  }

  public static Payment consolidate(Payment oldCustomer, Payment newCustomer) {

    Merchant merchant = oldCustomer.merchant;
    Customer customer = oldCustomer.customer;
    Product product = oldCustomer.product;
    Purchase purchase = oldCustomer.purchase;
    Status status = oldCustomer.status;
    String payload = oldCustomer.payload;
    Transaction transaction = oldCustomer.transaction;

    if (newCustomer.merchant != null) {
      merchant = newCustomer.merchant;
    }

    if (newCustomer.customer != null) {
      customer = newCustomer.customer;
    }

    if (newCustomer.product != null) {
      product = newCustomer.product;
    }

    if (newCustomer.purchase != null) {
      purchase = newCustomer.purchase;
    }

    if (newCustomer.status != null) {
      status = newCustomer.status;
    }

    if (newCustomer.payload != null) {
      payload = newCustomer.payload;
    }

    if (newCustomer.transaction != null) {
      transaction = newCustomer.transaction;
    }

    return new Payment(merchant, customer, product, purchase, status, payload, transaction);
  }

  public static enum Status {
    LOADING, LOADING_ERROR, LOADED, PROCESSING, FAILED, COMPLETED, PENDING_AUTHORIZATION
  }
}
