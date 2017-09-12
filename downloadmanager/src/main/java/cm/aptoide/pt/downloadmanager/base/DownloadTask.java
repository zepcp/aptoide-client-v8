package cm.aptoide.pt.downloadmanager.base;

public interface DownloadTask<T> {

  String VERSION_CODE = "versioncode";
  String PACKAGE = "package";
  String FILE_TYPE = "fileType";
  int PROGRESS_MAX_VALUE = 100;
  int MAX_RETRY_ATTEMPTS = 3;

  int getDownloadId();

  T innerTask();
}
