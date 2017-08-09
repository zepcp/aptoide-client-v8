package cm.aptoide.pt.v8engine.download;

import cm.aptoide.pt.database.realm.FileToDownload;
import cm.aptoide.pt.downloadmanager.DownloadFile;
import cm.aptoide.pt.downloadmanager.DownloadFileType;
import cm.aptoide.pt.downloadmanager.DownloadStatus;

public class DownloadFileDecorator implements DownloadFile {

  private final FileToDownload downloadFile;

  public DownloadFileDecorator(FileToDownload downloadFile) {
    this.downloadFile = downloadFile;
  }

  @Override public String getAltLink() {
    return downloadFile.getAltLink();
  }

  @Override public void setAltLink(String altLink) {
    downloadFile.setAltLink(altLink);
  }

  @Override public DownloadStatus getStatus() {
    return DownloadStatus.fromValue(downloadFile.getStatus());
  }

  @Override public void setStatus(DownloadStatus status) {
    downloadFile.setStatus(status.getValue());
  }

  @Override public String getLink() {
    return downloadFile.getLink();
  }

  @Override public void setLink(String link) {
    downloadFile.setLink(link);
  }

  @Override public String getPackageName() {
    return downloadFile.getPackageName();
  }

  @Override public void setPackageName(String packageName) {
    downloadFile.setPackageName(packageName);
  }

  @Override public int getDownloadId() {
    return downloadFile.getDownloadId();
  }

  @Override public void setDownloadId(int downloadId) {
    downloadFile.setDownloadId(downloadId);
  }

  @Override public DownloadFileType getFileType() {
    return DownloadFileType.fromValue(downloadFile.getFileType());
  }

  @Override public void setFileType(DownloadFileType fileType) {
    downloadFile.setFileType(fileType.getValue());
  }

  @Override public int getProgress() {
    return downloadFile.getProgress();
  }

  @Override public void setProgress(int progress) {
    downloadFile.setProgress(progress);
  }

  @Override public String getFilePath() {
    return downloadFile.getFilePath();
  }

  @Override public String getPath() {
    return downloadFile.getPath();
  }

  @Override public void setPath(String path) {
    downloadFile.setPath(path);
  }

  @Override public String getFileName() {
    return downloadFile.getFileName();
  }

  @Override public void setFileName(String fileName) {
    downloadFile.setFileName(fileName);
  }

  @Override public String getMd5() {
    return downloadFile.getMd5();
  }

  @Override public void setMd5(String md5) {
    downloadFile.setMd5(md5);
  }

  @Override public int getVersionCode() {
    return downloadFile.getVersionCode();
  }

  public FileToDownload getDecoratedEntity() {
    return downloadFile;
  }
}
