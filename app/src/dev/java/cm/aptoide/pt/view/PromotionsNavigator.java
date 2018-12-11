package cm.aptoide.pt.view;

import cm.aptoide.pt.navigator.FragmentNavigator;
import cm.aptoide.pt.promotions.ClaimPromotionDialogFragment;

public class PromotionsNavigator {
  private final FragmentNavigator fragmentNavigator;

  public PromotionsNavigator(FragmentNavigator fragmentNavigator) {
    this.fragmentNavigator = fragmentNavigator;
  }

  public void navigateToClaim() {
    fragmentNavigator.navigateToDialogFragment(new ClaimPromotionDialogFragment(), "tag");
  }
}
