package cm.aptoide.pt.billing.customer;

import cm.aptoide.pt.billing.BillingService;
import cm.aptoide.pt.billing.authorization.Authorization;
import cm.aptoide.pt.billing.authorization.CreditCardAuthorization;
import cm.aptoide.pt.billing.authorization.PayPalAuthorization;
import cm.aptoide.pt.billing.payment.PaymentMethod;
import cm.aptoide.pt.billing.payment.PaymentServiceAdapter;
import io.reactivex.exceptions.OnErrorNotImplementedException;
import java.util.List;
import rx.Observable;
import rx.Single;
import rx.subjects.PublishSubject;

public class CustomerManager {

  private final PublishSubject<Action> actions;
  private final UserPersistence userPersistence;
  private final Observable<Customer> customerObservable;
  private final BillingService billingService;
  private final AuthorizationPersistence authorizationPersistence;
  private final Authorization payPalAuthorization;
  private final PaymentServiceAdapter serviceAdapter;
  private boolean setup;

  public CustomerManager(PublishSubject<Action> actions, UserPersistence userPersistence,
      BillingService billingService, AuthorizationPersistence authorizationPersistence,
      PayPalAuthorization payPalAuthorization, PaymentServiceAdapter serviceAdapter) {
    this.actions = actions;
    this.userPersistence = userPersistence;
    this.billingService = billingService;
    this.authorizationPersistence = authorizationPersistence;
    this.payPalAuthorization = payPalAuthorization;
    this.serviceAdapter = serviceAdapter;
    this.customerObservable = this.actions.publish(published -> Observable.merge(
        published.ofType(LoadCustomer.class)
            .compose(loadCustomer()), published.ofType(SelectAuthorization.class)
            .compose(selectAuthorization()), published.ofType(SelectPaymentMethod.class)
            .compose(selectPaymentMethod()), published.ofType(ClearPaymentMethod.class)
            .compose(clearPaymentMethod()), published.ofType(AuthorizePaymentMethod.class)
            .compose(authorizePaymentMethod()), published.ofType(SyncAuthorizations.class)
            .compose(syncAuthorizations())))
        .scan(Customer.loading(),
            (oldCustomer, newCustomer) -> Customer.consolidate(oldCustomer, newCustomer))
        .replay(1)
        .autoConnect();
  }

  public synchronized void setup() {

    if (setup) {
      return;
    }

    customerObservable.subscribe();

    customerObservable.filter(customer -> customer.getStatus()
        .equals(Customer.Status.LOADED))
        .subscribe(customer -> actions.onNext(new SyncAuthorizations(customer)), throwable -> {
          throw new OnErrorNotImplementedException(throwable);
        });

    userPersistence.getUser()
        .subscribe(user -> actions.onNext(new LoadCustomer(user)), throwable -> {
          throw new OnErrorNotImplementedException(throwable);
        });

    setup = true;
  }

  public Observable<Customer> getCustomer() {
    return customerObservable;
  }

  public <T> void authorize(T data) {
    actions.onNext(new AuthorizePaymentMethod<T>(data));
  }

  public void selectPaymentMethod(PaymentMethod paymentMethod) {
    actions.onNext(new SelectPaymentMethod(paymentMethod));
  }

  public void selectAuthorization(Authorization authorization) {
    actions.onNext(new SelectAuthorization(authorization));
  }

  private Observable.Transformer<SelectAuthorization, Customer> selectAuthorization() {
    return select -> select.map(data -> data.getAuthorization())
        .flatMap(authorization -> getCustomer().first()
            .flatMapIterable(customer -> customer.getPaymentMethods())
            .filter(paymentMethod -> paymentMethod.getId() == authorization.getPaymentMethodId())
            .first()
            .map(paymentMethod -> {
              if (authorization.getType()
                  .equals(Authorization.PAYPAL_SDK)) {
                return Customer.withAuthorization(payPalAuthorization);
              }
              return Customer.withAuthorization(paymentMethod, authorization);
            }));
  }

  public void clearPaymentMethodSelection() {
    actions.onNext(new ClearPaymentMethod());
  }

  private Observable.Transformer<SelectPaymentMethod, Customer> selectPaymentMethod() {
    return select -> select.map(data -> data.getPaymentMethod())
        .map(paymentMethod -> {
          if (paymentMethod.getType()
              .equals(PaymentMethod.PAYPAL)) {
            return Customer.withAuthorization(paymentMethod, payPalAuthorization);
          }
          return Customer.withPaymentMethod(paymentMethod);
        });
  }

  private Observable.Transformer<ClearPaymentMethod, Customer> clearPaymentMethod() {
    return select -> select.map(data -> Customer.withoutPaymentMethod());
  }

  private Observable.Transformer<LoadCustomer, Customer> loadCustomer() {
    return load -> load.map(data -> data.getUser())
        .flatMap(user -> {
          if (user.isAuthenticated()) {
            return Single.zip(billingService.getPaymentMethods(),
                billingService.getAuthorizations(user.getId()),
                (paymentMethods, authorizations) -> {

                  final Authorization defaultAuthorization =
                      getDefaultAuthorization(authorizations);

                  final PaymentMethod defaultPaymentMethod =
                      getDefaultPaymentMethod(paymentMethods, defaultAuthorization);

                  return Customer.authenticated(paymentMethods, authorizations,
                      defaultAuthorization, defaultPaymentMethod, user.getId());
                })
                .toObservable()
                .startWith(Customer.loading());
          }
          return Observable.just(Customer.notAuthenticated());
        })
        .onErrorReturn(throwable -> Customer.error());
  }

  private Observable.Transformer<SyncAuthorizations, Customer> syncAuthorizations() {
    return sync -> sync.map(data -> data.getCustomer())
        .switchMap(customer -> {
          if (customer.isAuthenticated()) {
            return authorizationPersistence.getAuthorizations(customer.getId())
                .flatMapObservable(authorizations -> Observable.from(authorizations))
                .publish(authorizations -> Observable.merge(
                    authorizations.ofType(CreditCardAuthorization.class)
                        .flatMapSingle(
                            authorization -> billingService.updateCreditCardAuthorization(
                                authorization.getCustomerId(), authorization.getId(),
                                authorization.getPayload(), authorization.getPaymentMethodId())),
                    authorizations.ofType(PayPalAuthorization.class)
                        .flatMapSingle(authorization -> billingService.updatePayPalAuthorization(
                            authorization.getCustomerId(), authorization.getPayKey(),
                            authorization.getPaymentMethodId(), authorization.getId()))))
                .flatMapSingle(authorization -> authorizationPersistence.removeAuthorization(
                    authorization.getId())
                    .andThen(Single.just(authorization)))
                .filter(authorization -> customer.getSelectedPaymentMethod()
                    .getId() == authorization.getPaymentMethodId())
                .map(
                    authorization -> Customer.withAuthorization(customer.getSelectedPaymentMethod(),
                        authorization))
                .onErrorReturn(throwable -> Customer.error());
          }
          return Observable.empty();
        });
  }

  private PaymentMethod getDefaultPaymentMethod(List<PaymentMethod> paymentMethods,
      Authorization defaultAuthorization) {

    for (PaymentMethod paymentMethod : paymentMethods) {

      if (defaultAuthorization != null
          && paymentMethod.getId() == defaultAuthorization.getPaymentMethodId()) {
        return paymentMethod;
      }

      if (paymentMethod.isDefault()) {
        return paymentMethod;
      }
    }
    return null;
  }

  private Authorization getDefaultAuthorization(List<Authorization> authorizations) {
    if (authorizations.isEmpty()) {
      return null;
    }

    for (Authorization authorization : authorizations) {
      if (authorization.isDefault()) {
        return authorization;
      }
    }

    return authorizations.get(0);
  }

  private Observable.Transformer<AuthorizePaymentMethod, Customer> authorizePaymentMethod() {
    return authorize -> authorize.flatMap(data -> customerObservable.first()
        .flatMap(customer -> {
          if (customer.isAuthenticated() && customer.isPaymentMethodSelected()) {
            return serviceAdapter.authorize(customer.getSelectedPaymentMethod()
                .getType(), data.getData(), customer.getId(), customer.getSelectedPaymentMethod()
                .getId())
                .map(
                    authorization -> Customer.withAuthorization(customer.getSelectedPaymentMethod(),
                        authorization))
                .toObservable()
                .startWith(Customer.loading());
          }
          return Observable.just(Customer.error());
        })
        .onErrorReturn(throwable -> Customer.error()));
  }

  private static class SelectPaymentMethod extends Action {

    private final PaymentMethod paymentMethod;

    public SelectPaymentMethod(PaymentMethod paymentMethod) {
      this.paymentMethod = paymentMethod;
    }

    public PaymentMethod getPaymentMethod() {
      return paymentMethod;
    }
  }

  private static class SelectAuthorization extends Action {

    private final Authorization authorization;

    public SelectAuthorization(Authorization authorization) {
      this.authorization = authorization;
    }

    public Authorization getAuthorization() {
      return authorization;
    }
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

  private static class ClearPaymentMethod extends Action {
  }

  private static class AuthorizePaymentMethod<T> extends Action {

    private final T data;

    public AuthorizePaymentMethod(T data) {
      this.data = data;
    }

    public T getData() {
      return data;
    }
  }

  private static class SyncAuthorizations extends Action {

    private final Customer customer;

    public SyncAuthorizations(Customer customer) {
      this.customer = customer;
    }

    public Customer getCustomer() {
      return customer;
    }
  }
}
