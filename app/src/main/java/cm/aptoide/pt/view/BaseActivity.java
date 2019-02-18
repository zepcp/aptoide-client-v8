package cm.aptoide.pt.view;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import cm.aptoide.pt.AptoideApplication;
import cm.aptoide.pt.FlavourActivityModule;
import cm.aptoide.pt.notification.NotificationSyncScheduler;
import cm.aptoide.pt.presenter.View;
import com.trello.rxlifecycle.components.support.RxAppCompatActivity;
import javax.inject.Inject;
import javax.inject.Named;

public abstract class BaseActivity extends RxAppCompatActivity {

  @Inject @Named("aptoide-theme") String theme;
  private ActivityComponent activityComponent;
  private boolean firstCreated;

  @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    firstCreated = savedInstanceState == null;
    getActivityComponent().inject(this);
    ThemeUtils.setStatusBarThemeColor(this, theme);
    ThemeUtils.setAptoideTheme(this, theme);
  }

  @Override protected void onDestroy() {
    activityComponent = null;
    super.onDestroy();
  }

  public ActivityComponent getActivityComponent() {
    if (activityComponent == null) {
      AptoideApplication aptoideApplication = ((AptoideApplication) getApplication());
      activityComponent = aptoideApplication.getApplicationComponent()
          .plus(getActivityModule(this, getIntent(),
              aptoideApplication.getNotificationSyncScheduler(), (View) this, firstCreated),
              new FlavourActivityModule());
    }
    return activityComponent;
  }

  private ActivityModule getActivityModule(BaseActivity activity, Intent intent,
      NotificationSyncScheduler notificationSyncScheduler, View view, boolean firstCreated) {
    return new ActivityModule(activity, intent, notificationSyncScheduler, view, firstCreated);
  }
}
