package cm.aptoide.pt.downloadmanager.base;

public interface DownloadClient {

  DownloadTask startDownload(String string);

  void pauseAllDownloads();

  void pauseDownload(int downloadId);

  void clearAllDownloads();
}
