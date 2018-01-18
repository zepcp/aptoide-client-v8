/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 12/08/2016.
 */

package cm.aptoide.pt.billing;

import cm.aptoide.pt.billing.authorization.Authorization;
import cm.aptoide.pt.billing.authorization.AuthorizationRepository;
import cm.aptoide.pt.billing.customer.Customer;
import cm.aptoide.pt.billing.customer.User;
import cm.aptoide.pt.billing.payment.Payment;
import cm.aptoide.pt.billing.payment.PaymentMethod;
import cm.aptoide.pt.billing.payment.PaymentServiceAdapter;
import cm.aptoide.pt.billing.product.Product;
import cm.aptoide.pt.billing.purchase.Purchase;
import cm.aptoide.pt.billing.transaction.TransactionRepository;
import io.reactivex.exceptions.OnErrorNotImplementedException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import rx.Completable;
import rx.Observable;
import rx.Single;
import rx.subjects.PublishSubject;

public class Billing {

  private final BillingService billingService;
  private final UserPersistence userPersistence;
  private final PurchaseTokenDecoder tokenDecoder;
  private final String merchantPackageName;
  private final MerchantVersionProvider versionProvider;
  private final PaymentServiceAdapter serviceAdapter;
  private final PublishSubject<Action> actions;
  private final Observable<Customer> customer;
  private final Observable<Payment> payment;
  private boolean setup;

  private Billing(String merchantPackageName, BillingService billingService,
      UserPersistence userPersistence, PurchaseTokenDecoder tokenDecoder,
      MerchantVersionProvider versionProvider, PaymentServiceAdapter serviceAdapter,
      PublishSubject<Action> actions) {
    this.billingService = billingService;
    this.userPersistence = userPersistence;
    this.tokenDecoder = tokenDecoder;
    this.merchantPackageName = merchantPackageName;
    this.versionProvider = versionProvider;
    this.serviceAdapter = serviceAdapter;
    this.actions = actions;
    this.setup = false;
    this.customer = actions.publish(published -> Observable.merge(
        published.ofType(LoadCustomer.class)
            .compose(loadCustomer()), published.ofType(SelectPaymentMethod.class)
            .compose(selectPaymentMethod())))
        .scan(Customer.loading(),
            (oldCustomer, newCustomer) -> Customer.consolidate(oldCustomer, newCustomer))
        .publish()
        .autoConnect();
    this.payment = actions.publish(published -> Observable.merge(
        published.ofType(SelectProduct.class)
            .compose(selectProduct()), published.ofType(CreateTransaction.class)
            .compose(createTransaction()), published.ofType(SelectCustomer.class)
            .compose(selectCustomer())))
        .scan(Payment.loading(),
            (oldPayment, newPayment) -> Payment.consolidate(oldPayment, newPayment))
        .publish()
        .autoConnect();
  }

  public void setup() {

    if (setup) {
      return;
    }

    customer.subscribe(customer -> {
      actions.onNext(new SelectCustomer(customer));
    }, throwable -> {
      throw new OnErrorNotImplementedException(throwable);
    });

    payment.subscribe(__ -> {
    }, throwable -> {
      throw new OnErrorNotImplementedException(throwable);
    });

    userPersistence.getUser()
        .subscribe(user -> actions.onNext(new LoadCustomer(user)), throwable -> {
          throw new OnErrorNotImplementedException(throwable);
        });

    setup = true;
  }

  private Observable.Transformer<SelectPaymentMethod, Customer> selectPaymentMethod() {
    return select -> select.map(data -> Customer.withPaymentMethod(data.getPaymentMethod()));
  }

  public Single<Merchant> getMerchant() {
    return versionProvider.getVersionCode(merchantPackageName)
        .flatMap(versionCode -> billingService.getMerchant(merchantPackageName, versionCode));
  }

  public Observable<Customer> getCustomer() {
    return customer;
  }

  public Observable<Payment> getPayment() {
    return payment;
  }

  private Observable.Transformer<LoadCustomer, Customer> loadCustomer() {
    return load -> load.map(data -> data.getUser())
        .flatMap(user -> Single.zip(billingService.getPaymentMethods(),
            billingService.getAuthorizations(user.getId()),
            (paymentMethods, authorizations) -> Customer.loaded(user.isAuthenticated(),
                paymentMethods, authorizations, getDefaultAuthorization(authorizations), null,
                user.getId()))
            .toObservable()
            .onErrorReturn(throwable -> Customer.error())
            .startWith(Customer.loading()));
  }

  private Observable.Transformer<SelectCustomer, Payment> selectCustomer() {
    return selectCustomer -> selectCustomer.map(data -> Payment.withCustomer(data.getCustomer()));
  }

  private Observable.Transformer<SelectProduct, Payment> selectProduct() {
    return selectProduct -> selectProduct.flatMap(data -> getMerchant().flatMap(
        merchant -> billingService.getProduct(data.getSku(), merchantPackageName)
            .flatMap(product -> billingService.getPurchase(product.getId())
                .map(purchase -> Payment.loaded(merchant, product, purchase, data.getPayload()))))
        .toObservable()
        .onErrorReturn(throwable -> Payment.error())
        .startWith(Payment.loading()));
  }

  private Observable.Transformer<CreateTransaction, Payment> createTransaction() {
    return createTransaction -> createTransaction.flatMap(__ -> payment.first()
        .flatMap(payment -> serviceAdapter.createTransaction(payment.getCustomer()
            .getSelectedPaymentMethod()
            .getType(), payment.getCustomer()
            .getSelectedPaymentMethod()
            .getId(), payment.getPayload(), payment.getCustomer()
            .getId(), payment.getProduct()
            .getId())
            .toObservable()
            .map(transaction -> Payment.withTransaction(transaction))
            .onErrorReturn(throwable -> Payment.error())
            .startWith(Payment.loading())));
  }

  private Authorization getDefaultAuthorization(List<Authorization> authorizations) {
    for (Authorization authorization : authorizations) {
      if (authorization.isDefault()) {
        return authorization;
      }
    }
    return null;
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

  public <T> void authorize(T data) {
    actions.onNext(new AuthorizePayment<T>(data));
  }

  public void pay() {
    actions.onNext(new CreateTransaction());
  }

  public void selectProduct(String sku, String payload) {
    actions.onNext(new SelectProduct(sku, payload));
  }

  public void selectPaymentMethod(PaymentMethod paymentMethod) {
    actions.onNext(new SelectPaymentMethod(paymentMethod));
  }

  private static class Action {
  }

  private static class LoadCustomer extends Action {

    private final User user;

    private LoadCustomer(User user) {
      this.user = user;
    }

    public User getUser() {
      return user;
    }
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

  private static class AuthorizePayment<T> extends Action {

    private final T data;

    public AuthorizePayment(T data) {
      this.data = data;
    }

    public T getData() {
      return data;
    }
  }

  public static class SelectPaymentMethod extends Action {

    private final PaymentMethod paymentMethod;

    public SelectPaymentMethod(PaymentMethod paymentMethod) {
      this.paymentMethod = paymentMethod;
    }

    public PaymentMethod getPaymentMethod() {
      return paymentMethod;
    }
  }

  public static class SelectCustomer extends Action {

    private final Customer customer;

    public SelectCustomer(Customer customer) {
      this.customer = customer;
    }

    public Customer getCustomer() {
      return customer;
    }
  }

  public static class CreateTransaction extends Action {

  }

  public static class Builder {

    private TransactionRepository transactionRepository;
    private BillingService billingService;
    private AuthorizationRepository authorizationRepository;
    private UserPersistence userPersistence;
    private PurchaseTokenDecoder tokenDecoder;
    private String merchantPackageName;
    private MerchantVersionProvider versionProvider;
    private Map<String, PaymentService> services;

    public Builder() {
      this.services = new HashMap<>();
    }

    public Builder setTransactionRepository(TransactionRepository transactionRepository) {
      this.transactionRepository = transactionRepository;
      return this;
    }

    public Builder setBillingService(BillingService billingService) {
      this.billingService = billingService;
      return this;
    }

    public Builder setAuthorizationRepository(AuthorizationRepository authorizationRepository) {
      this.authorizationRepository = authorizationRepository;
      return this;
    }

    public Builder setUserPersistence(UserPersistence userPersistence) {
      this.userPersistence = userPersistence;
      return this;
    }

    public Builder setPurchaseTokenDecoder(PurchaseTokenDecoder tokenDecoder) {
      this.tokenDecoder = tokenDecoder;
      return this;
    }

    public Builder setMerchantPackageName(String merchantPackageName) {
      this.merchantPackageName = merchantPackageName;
      return this;
    }

    public Builder setMerchantVersionProvider(MerchantVersionProvider versionProvider) {
      this.versionProvider = versionProvider;
      return this;
    }

    public Builder registerPaymentService(String type, PaymentService service) {
      services.put(type, service);
      return this;
    }

    public Billing build() {

      if (services.isEmpty()) {
        throw new IllegalStateException("Register at least 1 payment service");
      }

      return new Billing(merchantPackageName, billingService, userPersistence, tokenDecoder,
          versionProvider,
          new PaymentServiceAdapter(services, transactionRepository, authorizationRepository),
          PublishSubject.create());
    }
  }
}