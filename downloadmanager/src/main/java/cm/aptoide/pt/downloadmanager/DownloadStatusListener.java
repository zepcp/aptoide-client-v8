package cm.aptoide.pt.downloadmanager;

import com.liulishuo.filedownloader.BaseDownloadTask;
import com.liulishuo.filedownloader.FileDownloadLargeFileListener;
import rx.subjects.BehaviorSubject;

public class DownloadStatusListener extends FileDownloadLargeFileListener {

  private final String downloadHashCode;
  private final String packageName;
  private final int versionCode;
  private final Analytics analytics;
  private final BehaviorSubject<DownloadProgress> currentDownloadsSubject;

  public DownloadStatusListener(String downloadHashCode, String packageName, int versionCode,
      Analytics analytics, BehaviorSubject<DownloadProgress> currentDownloadsSubject) {
    this.downloadHashCode = downloadHashCode;
    this.packageName = packageName;
    this.versionCode = versionCode;
    this.analytics = analytics;
    this.currentDownloadsSubject = currentDownloadsSubject;
  }

  @Override protected void pending(BaseDownloadTask baseDownloadTask, long bytesTransferredSoFar,
      long totalBytesToTransfer) {
    currentDownloadsSubject.onNext(new DownloadProgress(downloadHashCode,
        (int) baseDownloadTask.getTag(DownloadProgress.APPLICATION_FILE_INDEX),
        bytesTransferredSoFar, totalBytesToTransfer, baseDownloadTask.getSpeed(),
        DownloadStatus.PENDING));
  }

  @Override protected void progress(BaseDownloadTask baseDownloadTask, long bytesTransferredSoFar,
      long totalBytesToTransfer) {
    currentDownloadsSubject.onNext(new DownloadProgress(downloadHashCode,
        (int) baseDownloadTask.getTag(DownloadProgress.APPLICATION_FILE_INDEX),
        bytesTransferredSoFar, totalBytesToTransfer, baseDownloadTask.getSpeed(),
        DownloadStatus.PROGRESS));
  }

  @Override protected void paused(BaseDownloadTask baseDownloadTask, long bytesTransferredSoFar,
      long totalBytesToTransfer) {

    currentDownloadsSubject.onNext(new DownloadProgress(downloadHashCode,
        (int) baseDownloadTask.getTag(DownloadProgress.APPLICATION_FILE_INDEX),
        bytesTransferredSoFar, totalBytesToTransfer, baseDownloadTask.getSpeed(),
        DownloadStatus.PAUSED));
  }

  @Override protected void completed(BaseDownloadTask baseDownloadTask) {
    analytics.onDownloadComplete(downloadHashCode);
    currentDownloadsSubject.onNext(new DownloadProgress(downloadHashCode,
        (int) baseDownloadTask.getTag(DownloadProgress.APPLICATION_FILE_INDEX),
        DownloadStatus.COMPLETED));
  }

  @Override protected void error(BaseDownloadTask baseDownloadTask, Throwable throwable) {
    analytics.onError(packageName, versionCode, throwable);
    currentDownloadsSubject.onNext(new DownloadProgress(downloadHashCode,
        (int) baseDownloadTask.getTag(DownloadProgress.APPLICATION_FILE_INDEX), throwable,
        DownloadStatus.ERROR));
  }

  @Override protected void warn(BaseDownloadTask baseDownloadTask) {
    currentDownloadsSubject.onNext(new DownloadProgress(downloadHashCode,
        (int) baseDownloadTask.getTag(DownloadProgress.APPLICATION_FILE_INDEX),
        DownloadStatus.WARNING));
  }
}
