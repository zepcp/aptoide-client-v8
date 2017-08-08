package cm.aptoide.pt.v8engine.download;

import cm.aptoide.pt.database.realm.FileToDownload;
import cm.aptoide.pt.downloadmanager.Download;
import cm.aptoide.pt.downloadmanager.DownloadFile;
import cm.aptoide.pt.downloadmanager.DownloadStatus;
import io.realm.RealmList;
import java.util.LinkedList;
import java.util.List;

class DownloadWrapper implements Download {
  private final cm.aptoide.pt.database.realm.Download download;

  DownloadWrapper(cm.aptoide.pt.database.realm.Download download) {
    this.download = download;
  }

  @Override public DownloadStatus getDownloadError() {
    return download.getDownloadError();
  }

  @Override public void setDownloadError(DownloadStatus downloadError) {
    download.setDownloadError(downloadError);
  }

  @Override public long getTimeStamp() {
    return download.getTimeStamp();
  }

  @Override public void setTimeStamp(long timeStamp) {
    download.setTimeStamp(timeStamp);
  }

  @Override public String getAppName() {
    return download.getAppName();
  }

  @Override public void setAppName(String appName) {
    download.setAppName(appName);
  }

  @Override public List<DownloadFile> getFilesToDownload() {
    List<DownloadFile> files = new LinkedList<>();
    for (FileToDownload file : download.getFilesToDownload()) {
      files.add(new DownloadFileWrapper(file));
    }
    return files;
  }

  @Override public void setFilesToDownload(List<DownloadFile> filesToDownload) {
    RealmList<FileToDownload> dbFiles = new RealmList<>();

    for (DownloadFile downloadFile : filesToDownload) {
      dbFiles.add(getDbDownload(downloadFile));
    }

    download.setFilesToDownload(dbFiles);
  }

  @Override public DownloadStatus getOverallDownloadStatus() {
    return download.getOverallDownloadStatus();
  }

  @Override public void setOverallDownloadStatus(DownloadStatus overallDownloadStatus) {
    download.setOverallDownloadStatus(overallDownloadStatus);
  }

  @Override public int getOverallProgress() {
    return download.getOverallProgress();
  }

  @Override public void setOverallProgress(int overallProgress) {
    download.setOverallProgress(overallProgress);
  }

  @Override public String getIcon() {
    return download.getIcon();
  }

  @Override public void setIcon(String icon) {
    download.setIcon(icon);
  }

  @Override public int getDownloadSpeed() {
    return download.getDownloadSpeed();
  }

  @Override public void setDownloadSpeed(int speed) {
    download.setDownloadSpeed(speed);
  }

  @Override public int getVersionCode() {
    return download.getVersionCode();
  }

  @Override public void setVersionCode(int versionCode) {
    download.setVersionCode(versionCode);
  }

  @Override public String getPackageName() {
    return download.getPackageName();
  }

  @Override public void setPackageName(String packageName) {
    download.setPackageName(packageName);
  }

  @Override public int getAction() {
    return download.getAction();
  }

  @Override public void setAction(int action) {
    download.setAction(action);
  }

  @Override public boolean isScheduled() {
    return download.isScheduled();
  }

  @Override public void setScheduled(boolean scheduled) {
    download.setScheduled(scheduled);
  }

  @Override public String getMd5() {
    return download.getMd5();
  }

  @Override public void setMd5(String md5) {
    download.setMd5(md5);
  }

  @Override public String getVersionName() {
    return download.getVersionName();
  }

  @Override public void setVersionName(String versionName) {
    download.setVersionName(versionName);
  }
}
