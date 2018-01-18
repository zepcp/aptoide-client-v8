package cm.aptoide.pt.billing.view.card;

import cm.aptoide.pt.billing.Billing;
import cm.aptoide.pt.billing.BillingAnalytics;
import cm.aptoide.pt.billing.view.BillingNavigator;
import cm.aptoide.pt.presenter.Presenter;
import cm.aptoide.pt.presenter.View;
import java.io.IOException;
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

    handleErrorDismissEvent();

    handleCancel();
  }

  private void handleSaveCreditCardEvent() {
    view.getLifecycle()
        .filter(event -> event.equals(View.LifecycleEvent.CREATE))
        .flatMap(created -> view.saveCreditCardEvent())
        .observeOn(viewScheduler)
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(creditCard -> billing.authorize(creditCard), throwable -> showError(throwable));
  }

  private void handleCancel() {
    view.getLifecycle()
        .filter(event -> event.equals(View.LifecycleEvent.CREATE))
        .flatMap(created -> view.cancelEvent())
        .observeOn(viewScheduler)
        .doOnNext(__ -> {
          analytics.sendAuthorizationCancelEvent(serviceName);
          navigator.popView();
        })
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(__ -> {
        }, throwable -> showError(throwable));
  }

  private void handleErrorDismissEvent() {
    view.getLifecycle()
        .filter(event -> event.equals(View.LifecycleEvent.CREATE))
        .flatMap(created -> view.errorDismisses())
        .doOnNext(__ -> popViewWithError())
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(__ -> {
        }, throwable -> {
          throw new OnErrorNotImplementedException(throwable);
        });
  }

  private void showError(Throwable throwable) {
    if (throwable instanceof IOException) {
      view.hideLoading();
      view.showNetworkError();
    } else {
      popViewWithError();
    }
  }

  private void popViewWithError() {
    analytics.sendAuthorizationErrorEvent(serviceName);
    navigator.popView();
  }
}
