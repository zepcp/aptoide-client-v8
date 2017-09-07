package cm.aptoide.pt.downloadmanager;

public interface DownloadFile {
  String getAltLink();

  void setAltLink(String altLink);

  DownloadStatus getStatus();

  void setStatus(DownloadStatus status);

  String getLink();

  void setLink(String link);

  String getPackageName();

  void setPackageName(String packageName);

  int getDownloadId();

  void setDownloadId(int downloadId);

  DownloadFileType getFileType();

  void setFileType(DownloadFileType fileType);

  int getProgress();

  void setProgress(int progress);

  String getFilePath();

  String getPath();

  void setPath(String path);

  String getFileName();

  void setFileName(String fileName);

  String getHashCode();

  void setHashCode(String hashCode);

  int getVersionCode();
}
