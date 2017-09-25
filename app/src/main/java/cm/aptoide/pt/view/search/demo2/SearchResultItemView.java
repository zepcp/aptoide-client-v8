package cm.aptoide.pt.view.search.demo2;

import cm.aptoide.pt.presenter.View;
import rx.Observable;

public interface SearchResultItemView extends View {

  Observable<Void> onOpenAppViewClick();

  Observable<Void> onOpenPopupMenuClick();

  Observable<Void> onOtherVersionsClick();

  Observable<Void> onOpenStoreClick();

  final class StoreData {
    private final String storeName;
    private final String theme;

    StoreData(String storeName, String theme) {
      this.storeName = storeName;
      this.theme = theme;
    }

    public String getStoreName() {
      return storeName;
    }

    public String getTheme() {
      return theme;
    }
  }

  final class OtherVersionsData {
    private final String appName;
    private final String appIcon;
    private final String packageName;

    OtherVersionsData(String appName, String appIcon, String packageName) {
      this.appName = appName;
      this.appIcon = appIcon;
      this.packageName = packageName;
    }

    public String getAppName() {
      return appName;
    }

    public String getAppIcon() {
      return appIcon;
    }

    public String getPackageName() {
      return packageName;
    }
  }
}
