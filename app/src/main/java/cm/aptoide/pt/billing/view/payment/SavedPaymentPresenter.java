package cm.aptoide.pt.billing.view.payment;

import cm.aptoide.pt.billing.Billing;
import cm.aptoide.pt.presenter.Presenter;
import cm.aptoide.pt.presenter.View;
import java.util.Collections;
import rx.Scheduler;
import rx.exceptions.OnErrorNotImplementedException;

/**
 * Created by jdandrade on 18/01/2018.
 */

class SavedPaymentPresenter implements Presenter {

  private final SavedPaymentView view;
  private final Billing billing;
  private final Scheduler viewScheduler;
  private final SavedPaymentNavigator navigator;

  SavedPaymentPresenter(SavedPaymentView view, Billing billing, SavedPaymentNavigator navigator,
      Scheduler viewScheduler) {
    this.view = view;
    this.billing = billing;
    this.navigator = navigator;
    this.viewScheduler = viewScheduler;
  }

  @Override public void present() {
    view.getLifecycle()
        .filter(lifecycleEvent -> lifecycleEvent.equals(View.LifecycleEvent.CREATE))
        //.flatMapSingle(created -> billing.getCustomer()
        //    .map(customer -> customer.getAuthorizations())
        //    .toSingle())
        .observeOn(viewScheduler)
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(authorizedPayments -> view.showPaymentMethods(Collections.emptyList()),
            throwable -> {
              throw new OnErrorNotImplementedException(throwable);
            });

    view.getLifecycle()
        .filter(lifecycleEvent -> lifecycleEvent.equals(View.LifecycleEvent.CREATE))
        .flatMap(created -> view.onBackPressed())
        .doOnNext(__ -> navigator.back())
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(__ -> {
        }, throwable -> {
          throw new OnErrorNotImplementedException(throwable);
        });

    view.getLifecycle()
        .filter(lifecycleEvent -> lifecycleEvent.equals(View.LifecycleEvent.CREATE))
        .flatMap(created -> view.addPaymentClicked()
            .doOnNext(__ -> navigator.navigateToAddPaymentsView())
            .retry())
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(__ -> {
        }, throwable -> {
          throw new OnErrorNotImplementedException(throwable);
        });

    view.getLifecycle()
        .filter(lifecycleEvent -> lifecycleEvent.equals(View.LifecycleEvent.CREATE))
        .flatMap(created -> view.paymentAuthorizationSelected()
            .doOnNext(authorization -> view.setPaymentMethodSelected(authorization))
            .retry())
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(__ -> {
        }, throwable -> {
          throw new OnErrorNotImplementedException(throwable);
        });

    view.getLifecycle()
        .filter(lifecycleEvent -> lifecycleEvent.equals(View.LifecycleEvent.CREATE))
        .flatMap(created -> view.actionDeleteMenuClicked()
            .retry())
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(__ -> view.showPaymentMethodRemoval(), throwable -> {
          throw new OnErrorNotImplementedException(throwable);
        });
  }
}
