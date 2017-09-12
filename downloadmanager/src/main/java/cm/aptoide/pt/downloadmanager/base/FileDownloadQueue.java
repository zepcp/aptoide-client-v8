package cm.aptoide.pt.downloadmanager.base;

public interface FileDownloadQueue<T> {
  void enqueue(DownloadTask<T> downloadTask);
}
