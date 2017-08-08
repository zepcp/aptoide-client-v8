package cm.aptoide.pt.downloadmanager;

import java.util.List;

public interface Download {

  DownloadError getDownloadError();

  void setDownloadError(DownloadError downloadError);

  long getTimeStamp();

  void setTimeStamp(long timeStamp);

  String getAppName();

  void setAppName(String appName);

  List<DownloadFile> getFilesToDownload();

  void setFilesToDownload(List<DownloadFile> filesToDownload);

  DownloadStatus getOverallDownloadStatus();

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

  DownloadAction getAction();

  void setAction(DownloadAction action);

  boolean isScheduled();

  void setScheduled(boolean scheduled);

  String getMd5();

  void setMd5(String md5);

  String getVersionName();

  void setVersionName(String versionName);

  void save(DownloadRepository downloadRepository);
}
