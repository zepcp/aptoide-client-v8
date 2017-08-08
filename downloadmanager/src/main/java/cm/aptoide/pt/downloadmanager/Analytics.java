package cm.aptoide.pt.downloadmanager;

/**
 * Created by trinkes on 04/01/2017.
 */

public interface Analytics {
  void onError(Download download, Throwable throwable);
}
