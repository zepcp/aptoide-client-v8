/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 29/08/2016.
 */

package cm.aptoide.pt.billing.view.payment;

import cm.aptoide.pt.billing.Billing;
import cm.aptoide.pt.billing.BillingAnalytics;
import cm.aptoide.pt.billing.authorization.PayPalAuthorization;
import cm.aptoide.pt.billing.payment.Payment;
import cm.aptoide.pt.billing.view.BillingNavigator;
import cm.aptoide.pt.presenter.Presenter;
import cm.aptoide.pt.presenter.View;
import io.reactivex.exceptions.OnErrorNotImplementedException;
import rx.Scheduler;

public class PaymentPresenter implements Presenter {

  private static final int PAY_APP_REQUEST_CODE = 12;
  private final PaymentView view;
  private final Billing billing;
  private final BillingNavigator navigator;
  private final BillingAnalytics analytics;
  private final String merchantName;
  private final Scheduler viewScheduler;

  public PaymentPresenter(PaymentView view, Billing billing, BillingNavigator navigator,
      BillingAnalytics analytics, String merchantName, Scheduler viewScheduler) {
    this.view = view;
    this.billing = billing;
    this.navigator = navigator;
    this.analytics = analytics;
    this.merchantName = merchantName;
    this.viewScheduler = viewScheduler;
  }

  @Override public void present() {

    onViewCreatedShowPayment();

    handleChangeAuthorizationEvent();

    handleCancelEvent();

    handleBuyEvent();

    handlePayPalResultEvent();
  }

  private void handleChangeAuthorizationEvent() {
    view.getLifecycle()
        .filter(event -> View.LifecycleEvent.CREATE.equals(event))
        .flatMap(__ -> view.changeAuthorizationEvent())
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(__ -> navigator.navigateToManageAuthorizationsView(), throwable -> {
          throw new OnErrorNotImplementedException(throwable);
        });
  }

  private void onViewCreatedShowPayment() {
    view.getLifecycle()
        .filter(event -> event.equals(View.LifecycleEvent.RESUME))
        .doOnNext(__ -> view.showLoading())
        .flatMap(loading -> billing.getPayment()
            .compose(view.bindUntilEvent(View.LifecycleEvent.PAUSE)))
        .observeOn(viewScheduler)
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(payment -> {

          if (payment.getStatus()
              .equals(Payment.Status.LOADING)) {
            view.showLoading();
          }

          if (payment.getStatus()
              .equals(Payment.Status.LOADING_ERROR)) {
            view.hideLoading();
            view.showNetworkError();
          }

          if (payment.getStatus()
              .equals(Payment.Status.LOADED)) {

            if (payment.getCustomer()
                .isAuthenticated()) {

              view.hideLoading();

              if (payment.isProcessing()) {
                view.showLoading();
              }

              if (payment.isCompleted()) {
                analytics.sendPaymentSuccessEvent();
                navigator.popViewWithResult(payment.getPurchase());
              }

              if (payment.isFailed()) {
                view.hideLoading();
                view.showUnknownError();
                analytics.sendPaymentErrorEvent();
              }

              if (payment.isPendingAuthorization()) {
                navigator.navigateToPayPalForResult(PAY_APP_REQUEST_CODE,
                    (PayPalAuthorization) payment.getCustomer()
                        .getSelectedAuthorization());
              }

              if (payment.isRedeemed()) {
                billing.pay();
              }

              view.showMerchant(payment.getMerchant()
                  .getName());
              view.showProduct(payment.getProduct());
              view.showAuthorization(payment.getCustomer()
                  .getSelectedAuthorization());
            } else {
              navigator.navigateToCustomerAuthenticationView(merchantName);
            }
          }
        }, throwable -> {
          throw new OnErrorNotImplementedException(throwable);
        });
  }

  private void handlePayPalResultEvent() {
    view.getLifecycle()
        .filter(event -> event.equals(View.LifecycleEvent.CREATE))
        .flatMap(created -> navigator.payPalResults(PAY_APP_REQUEST_CODE))
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(result -> billing.authorize(result), throwable -> {
          throw new OnErrorNotImplementedException(throwable);
        });
  }

  private void handleCancelEvent() {
    view.getLifecycle()
        .filter(event -> View.LifecycleEvent.RESUME.equals(event))
        .flatMap(__ -> view.cancelEvent()
            .flatMap(serviceId -> billing.getPayment()
                .first())
            .compose(view.bindUntilEvent(View.LifecycleEvent.PAUSE)))
        .observeOn(viewScheduler)
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(payment -> {
          analytics.sendPaymentViewCancelEvent(payment);
          navigator.popViewWithResult();
        }, throwable -> {
          throw new OnErrorNotImplementedException(throwable);
        });
  }

  private void handleBuyEvent() {
    view.getLifecycle()
        .filter(event -> View.LifecycleEvent.CREATE.equals(event))
        .flatMap(__ -> view.buyEvent())
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(__ -> billing.pay(), throwable -> {
          throw new OnErrorNotImplementedException(throwable);
        });
  }
}
