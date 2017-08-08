package cm.aptoide.pt.v8engine.download;

import cm.aptoide.pt.database.realm.FileToDownload;
import cm.aptoide.pt.downloadmanager.DownloadFile;
import cm.aptoide.pt.downloadmanager.DownloadFileType;
import cm.aptoide.pt.downloadmanager.DownloadStatus;

public class DownloadFileWrapper implements DownloadFile {

  private final FileToDownload downloadFile;

  public DownloadFileWrapper(FileToDownload downloadFile) {
    this.downloadFile = downloadFile;
  }

  @Override public String getAltLink() {
    return null;
  }

  @Override public void setAltLink(String altLink) {

  }

  @Override public DownloadStatus getStatus() {
    return null;
  }

  @Override public void setStatus(DownloadStatus status) {

  }

  @Override public String getLink() {
    return null;
  }

  @Override public void setLink(String link) {

  }

  @Override public String getPackageName() {
    return null;
  }

  @Override public void setPackageName(String packageName) {

  }

  @Override public int getDownloadId() {
    return 0;
  }

  @Override public void setDownloadId(int downloadId) {

  }

  @Override public DownloadFileType getFileType() {
    return null;
  }

  @Override public void setFileType(DownloadFileType fileType) {

  }

  @Override public int getProgress() {
    return 0;
  }

  @Override public void setProgress(int progress) {

  }

  @Override public String getFilePath() {
    return null;
  }

  @Override public String getPath() {
    return null;
  }

  @Override public void setPath(String path) {

  }

  @Override public String getFileName() {
    return null;
  }

  @Override public void setFileName(String fileName) {

  }

  @Override public String getMd5() {
    return null;
  }

  @Override public void setMd5(String md5) {

  }
}
