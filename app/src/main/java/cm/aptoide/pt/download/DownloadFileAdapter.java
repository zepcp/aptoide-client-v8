package cm.aptoide.pt.download;

import cm.aptoide.pt.database.realm.FileToDownload;
import cm.aptoide.pt.downloadmanager.DownloadFileType;
import cm.aptoide.pt.downloadmanager.DownloadStatus;
import cm.aptoide.pt.downloadmanager.base.DownloadFile;
import java.io.File;

public class DownloadFileAdapter implements DownloadFile {

  private final FileToDownload downloadFile;

  public DownloadFileAdapter(FileToDownload downloadFile) {
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

  @Override public void setFilePath(String filePath) {
    final int endIndex = filePath.lastIndexOf(File.pathSeparatorChar);
    if (endIndex > 0) {
      downloadFile.setPath(filePath.substring(0, endIndex));
      downloadFile.setFileName(filePath.substring(endIndex));
    }
    downloadFile.setFileName(filePath);
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

  @Override public String getHashCode() {
    return downloadFile.getMd5();
  }

  @Override public void setHashCode(String hashCode) {
    downloadFile.setMd5(hashCode);
  }

  @Override public int getVersionCode() {
    return downloadFile.getVersionCode();
  }

  public FileToDownload getDecoratedEntity() {
    return downloadFile;
  }
}
