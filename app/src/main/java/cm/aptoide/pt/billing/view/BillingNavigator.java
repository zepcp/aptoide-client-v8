package cm.aptoide.pt.billing.view;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.widget.Toast;
import cm.aptoide.pt.BuildConfig;
import cm.aptoide.pt.billing.authorization.PayPalAuthorization;
import cm.aptoide.pt.billing.payment.PayPalResult;
import cm.aptoide.pt.billing.payment.PaymentMethod;
import cm.aptoide.pt.billing.purchase.Purchase;
import cm.aptoide.pt.billing.view.card.CreditCardAuthorizationFragment;
import cm.aptoide.pt.billing.view.login.PaymentLoginFragment;
import cm.aptoide.pt.billing.view.payment.PaymentFragment;
import cm.aptoide.pt.billing.view.payment.PaymentMethodsFragment;
import cm.aptoide.pt.navigator.ActivityNavigator;
import cm.aptoide.pt.navigator.FragmentNavigator;
import com.paypal.android.sdk.payments.PayPalConfiguration;
import com.paypal.android.sdk.payments.PayPalPayment;
import com.paypal.android.sdk.payments.PayPalService;
import com.paypal.android.sdk.payments.PaymentConfirmation;
import java.math.BigDecimal;
import rx.Observable;

public class BillingNavigator {

  private static final String EXTRA_AUTHORIZATION_ID = "AUTHORIZATION_ID";
  private final Context context;
  private final PurchaseBundleMapper bundleMapper;
  private final ActivityNavigator activityNavigator;
  private final FragmentNavigator fragmentNavigator;
  private final String marketName;

  public BillingNavigator(Context context, PurchaseBundleMapper bundleMapper, ActivityNavigator activityNavigator,
      FragmentNavigator fragmentNavigator, String marketName) {
    this.context = context;
    this.bundleMapper = bundleMapper;
    this.activityNavigator = activityNavigator;
    this.fragmentNavigator = fragmentNavigator;
    this.marketName = marketName;
  }

  public void navigateToCustomerAuthenticationView(String merchantName) {
    fragmentNavigator.navigateToWithoutBackSave(
        PaymentLoginFragment.create(getBillingBundle(merchantName, null)), true);
  }

  public void navigateToAuthorizationView(String merchantName, PaymentMethod service) {

    final Bundle bundle = getBillingBundle(merchantName, service.getType());

    switch (service.getType()) {
      case PaymentMethod.PAYPAL:
        fragmentNavigator.navigateToWithoutBackSave(PaymentFragment.create(bundle), true);
        break;
      case PaymentMethod.CREDIT_CARD:
        fragmentNavigator.navigateTo(CreditCardAuthorizationFragment.create(bundle), true);
        break;
      default:
        throw new IllegalArgumentException(service.getType()
            + " does not require authorization. Can not navigate to authorization view.");
    }
  }

  public void popView() {
    fragmentNavigator.popBackStack();
  }

  public void navigateToPayPalForResult(int requestCode, PayPalAuthorization authorization) {

    final Bundle bundle = new Bundle();
    bundle.putParcelable(PayPalService.EXTRA_PAYPAL_CONFIGURATION,
        new PayPalConfiguration().environment(BuildConfig.PAYPAL_ENVIRONMENT)
            .clientId(BuildConfig.PAYPAL_KEY)
            .rememberUser(true)
            .merchantName(marketName));
    bundle.putParcelable(com.paypal.android.sdk.payments.PaymentActivity.EXTRA_PAYMENT,
        new PayPalPayment(new BigDecimal(authorization.getPrice()
            .getAmount()), authorization.getPrice()
            .getCurrency(), authorization.getProductDescription(),
            PayPalPayment.PAYMENT_INTENT_SALE));
    bundle.putLong(EXTRA_AUTHORIZATION_ID, authorization.getId());

    activityNavigator.navigateForResult(com.paypal.android.sdk.payments.PaymentActivity.class,
        requestCode, bundle);
  }

  public Observable<PayPalResult> payPalResults(int requestCode) {
    return activityNavigator.results(requestCode)
        .map(result -> {
          if (result.getResultCode() == Activity.RESULT_OK) {
            final long authorizationId = result.getData()
                .getLongExtra(EXTRA_AUTHORIZATION_ID, -1);
            final PaymentConfirmation confirmation = result.getData()
                .getParcelableExtra(
                    com.paypal.android.sdk.payments.PaymentActivity.EXTRA_RESULT_CONFIRMATION);
            if (confirmation != null && confirmation.getProofOfPayment() != null) {
              return new PayPalResult(false, true, confirmation.getProofOfPayment()
                  .getPaymentId(), -1, authorizationId);
            }
          }
          return new PayPalResult(false, false, null, -1, -1);
        });
  }

  public void popViewWithResult(Purchase purchase) {
    activityNavigator.navigateBackWithResult(Activity.RESULT_OK, bundleMapper.map(purchase));
  }

  public void popViewWithResult(Throwable throwable) {
    activityNavigator.navigateBackWithResult(Activity.RESULT_CANCELED, bundleMapper.map(throwable));
  }

  public void popViewWithResult() {
    activityNavigator.navigateBackWithResult(Activity.RESULT_CANCELED,
        bundleMapper.mapCancellation());
  }

  private Bundle getBillingBundle(String merchantName, String serviceName) {
    final Bundle bundle = new Bundle();
    bundle.putString(BillingActivity.EXTRA_MERCHANT_PACKAGE_NAME, merchantName);
    bundle.putString(BillingActivity.EXTRA_SERVICE_NAME, serviceName);
    return bundle;
  }

  public void navigateToPaymentView(String merchantName) {
    fragmentNavigator.navigateToWithoutBackSave(
        PaymentFragment.create(getBillingBundle(merchantName, null)), true);
  }

  public void navigateToPaymentMethodsView(String merchantName) {
    fragmentNavigator.navigateToWithoutBackSave(
        PaymentMethodsFragment.create(getBillingBundle(merchantName, null)), true);
  }

  public void navigateToManageAuthorizationsView() {
    Toast.makeText(context, "Not implemented!", Toast.LENGTH_SHORT).show();
  }
}