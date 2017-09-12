package cm.aptoide.pt.downloadmanager.base;

public interface DownloadFactory {

  DownloadTask create(String fileDownloadLink, int versionCode, String packageName, int fileIndex,
      String downloadPath, String hashCode);
}
