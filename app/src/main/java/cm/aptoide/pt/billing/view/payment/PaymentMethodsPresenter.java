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

  public PaymentMethodsPresenter(PaymentMethodsView view, Billing billing, Scheduler viewScheduler,
      BillingNavigator navigator, String merchantPackageName) {
    this.view = view;
    this.billing = billing;
    this.viewScheduler = viewScheduler;
    this.navigator = navigator;
    this.merchantPackageName = merchantPackageName;
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
            .compose(view.bindUntilEvent(View.LifecycleEvent.PAUSE)))
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(paymentMethod -> {
          billing.selectPaymentMethod(paymentMethod);
          navigator.navigateToAuthorizationView(merchantPackageName, paymentMethod);
        }, throwable -> {
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

          if (customer.getStatus().equals(Customer.Status.LOADING)) {
            view.showLoading();
          }

          if (customer.getStatus().equals(Customer.Status.LOADED)) {
            view.hideLoading();
          }

          if (customer.getStatus().equals(Customer.Status.LOADING_ERROR)) {
            view.showNetworkError();
          }

          if (!customer.isAuthenticated()) {
            navigator.navigateToCustomerAuthenticationView(merchantPackageName);
          } else {
            if (customer.getAuthorizations()
                .isEmpty()) {
              if (customer.getPaymentMethods()
                  .isEmpty()) {
                view.showNoPaymentMethodsAvailableMessage();
              } else {
                view.showAvailablePaymentMethods(customer.getPaymentMethods());
              }
            } else {
              navigator.navigateToPaymentView(merchantPackageName);
            }
          }
        }, throwable -> {
          throw new OnErrorNotImplementedException(throwable);
        });
  }
}
