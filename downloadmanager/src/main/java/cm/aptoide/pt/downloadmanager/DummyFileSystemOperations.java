package cm.aptoide.pt.downloadmanager;

import java.io.File;

public class DummyFileSystemOperations implements FileSystemOperations {
  @Override public boolean deleteFile(String filePath) {
    if (filePath != null
        && filePath.trim()
        .length() > 0) {
      File file = new File(filePath);
      if (file.exists()) {
        return file.delete();
      }
    }
    return false;
  }
}
