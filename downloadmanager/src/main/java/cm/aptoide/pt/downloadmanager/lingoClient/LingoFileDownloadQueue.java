package cm.aptoide.pt.downloadmanager.lingoClient;

import cm.aptoide.pt.downloadmanager.base.DownloadTask;
import cm.aptoide.pt.downloadmanager.base.FileDownloadQueue;
import com.liulishuo.filedownloader.BaseDownloadTask;
import com.liulishuo.filedownloader.FileDownloadQueueSet;

public class LingoFileDownloadQueue implements FileDownloadQueue<BaseDownloadTask> {
  private final FileDownloadQueueSet downloadQueueSet;

  public LingoFileDownloadQueue(FileDownloadQueueSet downloadQueueSet) {
    this.downloadQueueSet = downloadQueueSet;
  }

  public void enqueue(DownloadTask<BaseDownloadTask> downloadTask) {
    downloadQueueSet.downloadSequentially(downloadTask.innerTask());
  }
}
