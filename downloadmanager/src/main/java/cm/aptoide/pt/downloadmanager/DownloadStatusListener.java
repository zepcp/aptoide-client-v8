package cm.aptoide.pt.downloadmanager;

import com.liulishuo.filedownloader.BaseDownloadTask;
import com.liulishuo.filedownloader.FileDownloadLargeFileListener;
import rx.subjects.BehaviorSubject;

public class DownloadStatusListener extends FileDownloadLargeFileListener {

  private final String downloadHashCode;
  private final BehaviorSubject<DownloadProgress> currentDownloadsSubject;

  public DownloadStatusListener(String downloadHashCode,
      BehaviorSubject<DownloadProgress> currentDownloadsSubject) {
    this.downloadHashCode = downloadHashCode;
    this.currentDownloadsSubject = currentDownloadsSubject;
  }

  @Override protected void pending(BaseDownloadTask baseDownloadTask, long bytesTransferredSoFar,
      long totalBytesToTransfer) {
    currentDownloadsSubject.onNext(new DownloadProgress(downloadHashCode,
        (int) baseDownloadTask.getTag(DownloadProgress.APPLICATION_FILE_INDEX), bytesTransferredSoFar,
        totalBytesToTransfer, DownloadStatus.PENDING));
  }

  @Override protected void progress(BaseDownloadTask baseDownloadTask, long bytesTransferredSoFar,
      long totalBytesToTransfer) {
    currentDownloadsSubject.onNext(new DownloadProgress(downloadHashCode,
        (int) baseDownloadTask.getTag(DownloadProgress.APPLICATION_FILE_INDEX), bytesTransferredSoFar,
        totalBytesToTransfer, DownloadStatus.PROGRESS));
  }

  @Override protected void paused(BaseDownloadTask baseDownloadTask, long bytesTransferredSoFar,
      long totalBytesToTransfer) {

    currentDownloadsSubject.onNext(new DownloadProgress(downloadHashCode,
        (int) baseDownloadTask.getTag(DownloadProgress.APPLICATION_FILE_INDEX), bytesTransferredSoFar,
        totalBytesToTransfer, DownloadStatus.PAUSED));
  }

  @Override protected void completed(BaseDownloadTask baseDownloadTask) {

    currentDownloadsSubject.onNext(new DownloadProgress(downloadHashCode,
        (int) baseDownloadTask.getTag(DownloadProgress.APPLICATION_FILE_INDEX), DownloadStatus.COMPLETED));
  }

  @Override protected void error(BaseDownloadTask baseDownloadTask, Throwable throwable) {
    currentDownloadsSubject.onNext(new DownloadProgress(downloadHashCode,
        (int) baseDownloadTask.getTag(DownloadProgress.APPLICATION_FILE_INDEX), throwable, DownloadStatus.ERROR));
  }

  @Override protected void warn(BaseDownloadTask baseDownloadTask) {
    currentDownloadsSubject.onNext(new DownloadProgress(downloadHashCode,
        (int) baseDownloadTask.getTag(DownloadProgress.APPLICATION_FILE_INDEX), DownloadStatus.WARNING));
  }
}
