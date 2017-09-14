package cm.aptoide.pt.downloadmanager.lingoClient;

import android.content.Context;
import cm.aptoide.pt.downloadmanager.base.DownloadClient;
import com.liulishuo.filedownloader.FileDownloader;

public class LingoDownloadClient implements DownloadClient {

  private boolean isInitialized = false;
  private final FileDownloader fileDownloader;

  public LingoDownloadClient(FileDownloader fileDownloader) {
    this.fileDownloader = fileDownloader;
  }

  @Override public LingoDownloadTask startDownload(String string) {
    if(!isInitialized){
      throw new IllegalStateException("Initialize this client first.");
    }
    return new LingoDownloadTask(fileDownloader.create(string));
  }

  public void initialize(Context context) {
    fileDownloader.setup(context);
    isInitialized = true;
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
