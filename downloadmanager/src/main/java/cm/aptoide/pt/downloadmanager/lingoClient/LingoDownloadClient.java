package cm.aptoide.pt.downloadmanager.lingoClient;

import cm.aptoide.pt.downloadmanager.base.DownloadClient;
import com.liulishuo.filedownloader.FileDownloader;

public class LingoDownloadClient implements DownloadClient {

  private final FileDownloader fileDownloader;

  public LingoDownloadClient(FileDownloader fileDownloader) {
    this.fileDownloader = fileDownloader;
  }

  @Override public LingoDownloadTask startDownload(String string) {
    return new LingoDownloadTask(fileDownloader.create(string));
  }

  @Override public void pauseAllDownloads() {
    fileDownloader.pauseAll();
  }

  @Override public void pauseDownload(int downloadId) {
    fileDownloader.pause(downloadId);
  }

  @Override public void clearAllDownloads() {
    fileDownloader.clearAllTaskData();
  }
}
