package cm.aptoide.pt.billing.view.payment;

import cm.aptoide.pt.billing.Billing;
import cm.aptoide.pt.billing.view.BillingNavigator;
import cm.aptoide.pt.presenter.Presenter;
import cm.aptoide.pt.presenter.View;
import rx.Scheduler;
import rx.exceptions.OnErrorNotImplementedException;

/**
 * Created by jdandrade on 18/01/2018.
 */

public class SavedPaymentPresenter implements Presenter {

  private final SavedPaymentView view;
  private final Scheduler viewScheduler;
  private final String merchant;
  private final Billing billing;
  private final BillingNavigator navigator;

  public SavedPaymentPresenter(SavedPaymentView view, Billing billing, BillingNavigator navigator,
      Scheduler viewScheduler, String merchant) {
    this.view = view;
    this.billing = billing;
    this.navigator = navigator;
    this.viewScheduler = viewScheduler;
    this.merchant = merchant;
  }

  @Override public void present() {
    view.getLifecycle()
        .filter(lifecycleEvent -> lifecycleEvent.equals(View.LifecycleEvent.CREATE))
        .flatMap(created -> billing.getCustomer())
        .observeOn(viewScheduler)
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(customer -> view.showAuthorizedPaymentMethods(customer.getAuthorizations(),
            customer.getSelectedAuthorization()
                .getId()), throwable -> {
          throw new OnErrorNotImplementedException(throwable);
        });

    view.getLifecycle()
        .filter(lifecycleEvent -> lifecycleEvent.equals(View.LifecycleEvent.CREATE))
        .flatMap(created -> view.onBackPressed())
        .doOnNext(__ -> navigator.popView())
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(__ -> {
        }, throwable -> {
          throw new OnErrorNotImplementedException(throwable);
        });

    view.getLifecycle()
        .filter(lifecycleEvent -> lifecycleEvent.equals(View.LifecycleEvent.CREATE))
        .flatMap(created -> view.addPaymentClicked()
            .doOnNext(__ -> navigator.navigateToPaymentMethodsViewWithBackSave(merchant, false))
            .retry())
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(__ -> {
        }, throwable -> {
          throw new OnErrorNotImplementedException(throwable);
        });

    view.getLifecycle()
        .filter(lifecycleEvent -> lifecycleEvent.equals(View.LifecycleEvent.CREATE))
        .flatMap(created -> view.paymentAuthorizationSelected()
            .doOnNext(billing::selectAuthorization)
            .doOnNext(__ -> navigator.popView())
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
        .subscribe(__ -> view.enterPaymentMethodRemovalMode(), throwable -> {
          throw new OnErrorNotImplementedException(throwable);
        });

    view.getLifecycle()
        .filter(lifecycleEvent -> lifecycleEvent.equals(View.LifecycleEvent.CREATE))
        .flatMap(created -> view.paymentMethodsToRemove()
            // TODO: 23/01/2018 remove saved payment methods from billing
            .doOnNext(view::hidePaymentMethods)
            .retry())
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(__ -> {
        }, throwable -> {
          throw new OnErrorNotImplementedException(throwable);
        });
  }
}