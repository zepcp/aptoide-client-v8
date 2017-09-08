package cm.aptoide.pt.downloadmanager;

public interface FileSystemOperations {
  boolean deleteFile(String path);

  long cleanCache();
}
