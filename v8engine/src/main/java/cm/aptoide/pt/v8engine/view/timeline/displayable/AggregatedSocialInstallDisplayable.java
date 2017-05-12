package cm.aptoide.pt.v8engine.view.timeline.displayable;

import cm.aptoide.pt.v8engine.R;

/**
 * Created by jdandrade on 11/05/2017.
 */

public class AggregatedSocialInstallDisplayable extends SocialInstallDisplayable {

  public static final String CARD_TYPE_NAME = "AGGREGATED_SOCIAL_INSTALL";

  @Override public int getViewLayout() {
    return R.layout.displayable_social_timeline_aggregated_social_install;
  }
}
