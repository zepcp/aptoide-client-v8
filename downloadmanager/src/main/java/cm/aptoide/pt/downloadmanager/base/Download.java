package cm.aptoide.pt.downloadmanager.base;

import android.support.annotation.NonNull;
import cm.aptoide.pt.downloadmanager.DownloadAction;
import cm.aptoide.pt.downloadmanager.DownloadError;
import cm.aptoide.pt.downloadmanager.DownloadStatus;
import java.util.List;

public interface Download {

  @NonNull DownloadError getDownloadError();

  void setDownloadError(DownloadError downloadError);

  long getTimeStamp();

  void setTimeStamp(long timeStamp);

  String getAppName();

  void setAppName(String appName);

  @NonNull List<DownloadFile> getFilesToDownload();

  void setFilesToDownload(List<DownloadFile> filesToDownload);

  @NonNull DownloadStatus getOverallDownloadStatus();

  void setOverallDownloadStatus(DownloadStatus overallDownloadStatus);

  int getOverallProgress();

  void setOverallProgress(int overallProgress);

  String getIcon();

  void setIcon(String icon);

  int getDownloadSpeed();

  void setDownloadSpeed(int speed);

  int getVersionCode();

  void setVersionCode(int versionCode);

  String getPackageName();

  void setPackageName(String packageName);

  @NonNull DownloadAction getAction();

  void setAction(DownloadAction action);

  boolean isScheduled();

  void setScheduled(boolean scheduled);

  String getHashCode();

  void setHashCode(String hashCode);

  String getVersionName();

  void setVersionName(String versionName);
}
