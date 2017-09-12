package cm.aptoide.pt.downloadmanager.external;

/**
 * Created by trinkes on 04/01/2017.
 */

public interface Analytics {

  void onError(String packageName, int versionCode, Throwable throwable);

  void onDownloadComplete(String applicationHashCode);
}
