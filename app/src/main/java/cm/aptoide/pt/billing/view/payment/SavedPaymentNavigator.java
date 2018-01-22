package cm.aptoide.pt.billing.view.payment;

import cm.aptoide.pt.navigator.FragmentNavigator;

/**
 * Created by jdandrade on 19/01/2018.
 */

class SavedPaymentNavigator {

  private final FragmentNavigator fragmentNavigator;

  public SavedPaymentNavigator(FragmentNavigator fragmentNavigator) {
    this.fragmentNavigator = fragmentNavigator;
  }

  public void back() {
    fragmentNavigator.popBackStack();
  }

  public void navigateToAddPaymentsView() {
    // TODO: 19/01/2018 navigate to the add payment screen
  }
}
