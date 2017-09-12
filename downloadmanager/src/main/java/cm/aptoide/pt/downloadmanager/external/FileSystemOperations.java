package cm.aptoide.pt.downloadmanager.external;

public interface FileSystemOperations {
  boolean deleteFile(String path);

  long cleanCache();
}
