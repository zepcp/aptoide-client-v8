package cm.aptoide.pt.billing.view.card;

import cm.aptoide.pt.billing.Billing;
import cm.aptoide.pt.billing.BillingAnalytics;
import cm.aptoide.pt.billing.view.BillingNavigator;
import cm.aptoide.pt.presenter.Presenter;
import cm.aptoide.pt.presenter.View;
import rx.Scheduler;
import rx.exceptions.OnErrorNotImplementedException;

public class CreditCardAuthorizationPresenter implements Presenter {

  private final CreditCardAuthorizationView view;
  private final Billing billing;
  private final BillingNavigator navigator;
  private final BillingAnalytics analytics;
  private final String serviceName;
  private final Scheduler viewScheduler;

  public CreditCardAuthorizationPresenter(CreditCardAuthorizationView view, Billing billing,
      BillingNavigator navigator, BillingAnalytics analytics, String serviceName,
      Scheduler viewScheduler) {
    this.view = view;
    this.billing = billing;
    this.navigator = navigator;
    this.analytics = analytics;
    this.serviceName = serviceName;
    this.viewScheduler = viewScheduler;
  }

  @Override public void present() {

    handleSaveCreditCardEvent();

    handleCancel();

    handleRegistrationResult();
  }

  private void handleRegistrationResult() {
    view.getLifecycle()
        .filter(event -> event.equals(View.LifecycleEvent.RESUME))
        .flatMap(__ -> billing.getCustomer()
            .compose(view.bindUntilEvent(View.LifecycleEvent.PAUSE)))
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .observeOn(viewScheduler)
        .subscribe(customer -> {

          if (customer.isAuthenticated() && customer.isPaymentMethodSelected()) {

            if (customer.isAuthorizationSelected()) {
              if (customer.getSelectedAuthorization()
                  .isFailed()) {
                analytics.sendAuthorizationErrorEvent(serviceName);
              }

              if (customer.getSelectedAuthorization()
                  .isActive()) {
                navigator.popView();
              }
            }
          } else {
            navigator.popView();
          }
        }, throwable -> {
          throw new OnErrorNotImplementedException(throwable);
        });
  }

  private void handleSaveCreditCardEvent() {
    view.getLifecycle()
        .filter(event -> event.equals(View.LifecycleEvent.CREATE))
        .flatMap(created -> view.saveCreditCardEvent())
        .observeOn(viewScheduler)
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(creditCard -> billing.authorize(creditCard), throwable -> {
          throw new OnErrorNotImplementedException(throwable);
        });
  }

  private void handleCancel() {
    view.getLifecycle()
        .filter(event -> event.equals(View.LifecycleEvent.CREATE))
        .flatMap(created -> view.cancelEvent())
        .observeOn(viewScheduler)
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(__ -> {
          billing.clearPaymentMethodSelection();
          analytics.sendAuthorizationCancelEvent(serviceName);
        }, throwable -> {
          throw new OnErrorNotImplementedException(throwable);
        });
  }
}
