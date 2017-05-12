package cm.aptoide.pt.v8engine.view.timeline.widget;

import android.view.View;
import cm.aptoide.pt.v8engine.view.timeline.displayable.AggregatedSocialInstallDisplayable;
import cm.aptoide.pt.v8engine.view.timeline.displayable.SocialInstallDisplayable;

/**
 * Created by jdandrade on 11/05/2017.
 */

public class AggregatedSocialInstallWidget extends SocialInstallWidget {
  public AggregatedSocialInstallWidget(View itemView) {
    super(itemView);
  }

  @Override protected void assignViews(View itemView) {
    super.assignViews(itemView);
  }

  @Override public void bindView(SocialInstallDisplayable displayable) {
    super.bindView(displayable);
  }

  @Override String getCardTypeName() {
    return AggregatedSocialInstallDisplayable.CARD_TYPE_NAME;
  }
}
