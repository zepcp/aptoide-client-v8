package cm.aptoide.pt.downloadmanager;

import java.util.List;

public interface DownloadRequest {
  String getHashCode();

  String getPackageName();

  List<DownloadFile> getFilesToDownload();

  String getApplicationName();

  DownloadAction getDownloadAction();

  String getApplicationIcon();

  int getVersionCode();

  String getVersionName();
}
