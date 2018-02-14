package cm.aptoide.pt.billing.view.payment;

import cm.aptoide.pt.billing.Billing;
import cm.aptoide.pt.billing.customer.Customer;
import cm.aptoide.pt.billing.view.BillingNavigator;
import cm.aptoide.pt.presenter.Presenter;
import cm.aptoide.pt.presenter.View;
import rx.Scheduler;
import rx.exceptions.OnErrorNotImplementedException;

public class PaymentMethodsPresenter implements Presenter {

  private final PaymentMethodsView view;
  private final Billing billing;
  private final Scheduler viewScheduler;
  private final BillingNavigator navigator;
  private final String merchantPackageName;
  private boolean changePaymentMethod;

  public PaymentMethodsPresenter(PaymentMethodsView view, Billing billing, Scheduler viewScheduler,
      BillingNavigator navigator, String merchantPackageName, boolean changePaymentMethod) {
    this.view = view;
    this.billing = billing;
    this.viewScheduler = viewScheduler;
    this.navigator = navigator;
    this.merchantPackageName = merchantPackageName;
    this.changePaymentMethod = changePaymentMethod;
  }

  @Override public void present() {

    onCreateShowPaymentMethods();

    handlePaymentMethodSelectedEvent();

    handleCancelEvent();
  }

  private void handleCancelEvent() {
    view.getLifecycle()
        .filter(event -> event.equals(View.LifecycleEvent.RESUME))
        .flatMap(__ -> view.getCancelEvent()
            .compose(view.bindUntilEvent(View.LifecycleEvent.PAUSE)))
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(paymentMethod -> {
          navigator.popViewWithResult();
        }, throwable -> {
          throw new OnErrorNotImplementedException(throwable);
        });
  }

  private void handlePaymentMethodSelectedEvent() {
    view.getLifecycle()
        .filter(event -> event.equals(View.LifecycleEvent.RESUME))
        .flatMap(__ -> view.getSelectedPaymentMethodEvent()
            .doOnNext(ignore -> changePaymentMethod = true))
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(paymentMethod -> billing.selectPaymentMethod(paymentMethod), throwable -> {
          throw new OnErrorNotImplementedException(throwable);
        });
  }

  private void onCreateShowPaymentMethods() {
    view.getLifecycle()
        .filter(event -> event.equals(View.LifecycleEvent.RESUME))
        .flatMap(__ -> billing.getCustomer()
            .compose(view.bindUntilEvent(View.LifecycleEvent.PAUSE)))
        .observeOn(viewScheduler)
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(customer -> {

          if (customer.getStatus()
              .equals(Customer.Status.LOADING)) {
            view.showLoading();
          }

          if (customer.getStatus()
              .equals(Customer.Status.LOADED)) {
            view.hideLoading();
            if (!customer.isAuthenticated()) {
              navigator.navigateToCustomerAuthenticationView(merchantPackageName);
            } else {
              if (customer.isPaymentMethodSelected() && changePaymentMethod) {
                if (customer.isAuthorizationSelected()) {
                  navigator.navigateToPaymentView(merchantPackageName);
                } else {
                  navigator.navigateToAuthorizationView(merchantPackageName,
                      customer.getSelectedPaymentMethod());
                }
              } else {
                if (customer.getPaymentMethods()
                    .isEmpty()) {
                  view.showNoPaymentMethodsAvailableMessage();
                } else {
                  view.showAvailablePaymentMethods(customer.getPaymentMethods());
                }
              }
            }
          }

          if (customer.getStatus()
              .equals(Customer.Status.LOADING_ERROR)) {
            view.showNetworkError();
          }
        }, throwable -> {
          throw new OnErrorNotImplementedException(throwable);
        });
  }
}
