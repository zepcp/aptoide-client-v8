/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 12/08/2016.
 */

package cm.aptoide.pt.billing;

import cm.aptoide.pt.billing.customer.Customer;
import cm.aptoide.pt.billing.customer.CustomerManager;
import cm.aptoide.pt.billing.payment.PayPalResult;
import cm.aptoide.pt.billing.payment.Payment;
import cm.aptoide.pt.billing.payment.PaymentMethod;
import cm.aptoide.pt.billing.payment.PaymentServiceAdapter;
import cm.aptoide.pt.billing.product.Product;
import cm.aptoide.pt.billing.purchase.Purchase;
import cm.aptoide.pt.billing.transaction.TransactionFactory;
import io.reactivex.exceptions.OnErrorNotImplementedException;
import java.util.List;
import rx.Completable;
import rx.Observable;
import rx.Single;
import rx.subjects.PublishSubject;

public class Billing {

  private final BillingService billingService;
  private final PurchaseTokenDecoder tokenDecoder;
  private final String merchantPackageName;
  private final MerchantVersionProvider versionProvider;
  private final PaymentServiceAdapter serviceAdapter;
  private final PublishSubject<Action> actions;
  private final CustomerManager customerManager;
  private final Observable<Payment> paymentObservable;
  private final TransactionFactory transactionFactory;
  private boolean setup;

  Billing(String merchantPackageName, BillingService billingService,
      PurchaseTokenDecoder tokenDecoder, MerchantVersionProvider versionProvider,
      PaymentServiceAdapter serviceAdapter, PublishSubject<Action> actions,
      CustomerManager customerManager, TransactionFactory transactionFactory) {
    this.billingService = billingService;
    this.tokenDecoder = tokenDecoder;
    this.merchantPackageName = merchantPackageName;
    this.versionProvider = versionProvider;
    this.serviceAdapter = serviceAdapter;
    this.actions = actions;
    this.customerManager = customerManager;
    this.transactionFactory = transactionFactory;
    this.setup = false;
    this.paymentObservable = actions.publish(published -> Observable.merge(
        published.ofType(SelectProduct.class)
            .compose(selectProduct()), published.ofType(Pay.class)
            .compose(startPayment()), published.ofType(LoadPurchase.class)
            .compose(loadPurchase())))
        .scan(Payment.loading(),
            (oldPayment, newPayment) -> Payment.consolidate(oldPayment, newPayment))
        .replay(1)
        .autoConnect();
  }

  public synchronized void setup() {

    if (setup) {
      return;
    }

    customerManager.setup();

    Observable.combineLatest(customerManager.getCustomer(),
        paymentObservable.distinct(payment -> payment.getProduct()),
        (customer, payment) -> new LoadPurchase(payment.getProduct(), customer))
        .subscribe(loadPurchase -> {
          actions.onNext(loadPurchase);
        }, throwable -> {
          throw new OnErrorNotImplementedException(throwable);
        });

    setup = true;
  }

  public Observable<Payment> getPayment() {
    return paymentObservable;
  }

  public Observable<Customer> getCustomer() {
    return customerManager.getCustomer();
  }

  public Single<Merchant> getMerchant() {
    return versionProvider.getVersionCode(merchantPackageName)
        .flatMap(versionCode -> billingService.getMerchant(merchantPackageName, versionCode));
  }

  public <T> void authorize(T data) {
    customerManager.authorize(data);
  }

  public void selectPaymentMethod(PaymentMethod paymentMethod) {
    customerManager.selectPaymentMethod(paymentMethod);
  }

  public void clearPaymentMethodSelection() {
    customerManager.clearPaymentMethodSelection();
  }

  public Single<List<Product>> getProducts(List<String> skus) {
    return billingService.getProducts(merchantPackageName, skus);
  }

  public Single<List<Purchase>> getPurchases() {
    return billingService.getPurchases(merchantPackageName);
  }

  public Completable consumePurchase(String purchaseToken) {
    return billingService.deletePurchase(tokenDecoder.decode(purchaseToken));
  }

  public void pay() {
    actions.onNext(new Pay());
  }

  public void selectProduct(String sku, String payload) {
    actions.onNext(new SelectProduct(sku, payload));
  }

  private Observable.Transformer<LoadPurchase, Payment> loadPurchase() {
    return loadPurchase -> loadPurchase.flatMap(data -> {

      if (data.getCustomer()
          .getStatus()
          .equals(Customer.Status.LOADED) && data.getProduct() != null) {

        if (data.getCustomer()
            .isAuthenticated()) {

          return Single.zip(billingService.getPurchase(data.getProduct()
                  .getId()), billingService.getTransaction(data.getCustomer()
                  .getId(), data.getProduct()
                  .getId()),
              (purchase, transaction) -> Payment.withCustomer(data.getCustomer(), transaction,
                  purchase))
              .toObservable()
              .onErrorReturn(throwable -> Payment.error())
              .startWith(Payment.loading());
        }

        return Observable.just(Payment.withCustomer(data.getCustomer(), null, null));
      }

      return Observable.empty();
    });
  }

  private Observable.Transformer<SelectProduct, Payment> selectProduct() {
    return selectProduct -> selectProduct.flatMap(data -> Single.zip(getMerchant(),
        billingService.getProduct(data.getSku(), merchantPackageName),
        (merchant, product) -> Payment.withProduct(merchant, product, data.getPayload()))
        .toObservable()
        .onErrorReturn(throwable -> Payment.error())
        .startWith(Payment.loading()));
  }

  private Observable.Transformer<Pay, Payment> startPayment() {
    return createTransaction -> createTransaction.flatMap(__ -> paymentObservable.first()
        .flatMap(payment -> {

          if (payment.getStatus()
              .equals(Payment.Status.LOADED)) {

            if (payment.getCustomer()
                .getSelectedAuthorization()
                .isNew()) {
              customerManager.authorize(new PayPalResult(true, false, null, payment.getProduct()
                  .getId(), payment.getCustomer()
                  .getSelectedAuthorization()
                  .getId()));
              return Observable.empty();
            }

            if (payment.getCustomer()
                .getSelectedAuthorization()
                .isActive()) {
              return serviceAdapter.pay(payment.getCustomer()
                  .getSelectedPaymentMethod()
                  .getType(), payment.getCustomer()
                  .getSelectedAuthorization()
                  .getId(), payment.getCustomer()
                  .getId(), payment.getProduct()
                  .getId(), payment.getPayload())
                  .flatMap(transaction -> {
                    if (transaction.isCompleted()) {
                      return billingService.getPurchase(payment.getProduct()
                          .getId())
                          .map(purchase -> Payment.withCustomer(payment.getCustomer(), transaction,
                              purchase));
                    }
                    return Single.just(
                        Payment.withCustomer(payment.getCustomer(), transaction, null));
                  })
                  .toObservable()
                  .onErrorReturn(throwable -> Payment.error())
                  .startWith(Payment.loading());
            }
          }

          return Observable.just(Payment.error());
        }));
  }

  private static class Action {
  }

  private static class Pay extends Action {

  }

  private static class SelectProduct extends Action {

    private final String sku;
    private final String payload;

    private SelectProduct(String sku, String payload) {
      this.sku = sku;
      this.payload = payload;
    }

    public String getSku() {
      return sku;
    }

    public String getPayload() {
      return payload;
    }
  }

  private static class LoadPurchase extends Action {

    private final Product product;
    private final Customer customer;

    public LoadPurchase(Product product, Customer customer) {
      this.product = product;
      this.customer = customer;
    }

    public Customer getCustomer() {
      return customer;
    }

    public Product getProduct() {
      return product;
    }
  }
}