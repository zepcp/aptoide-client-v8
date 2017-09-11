package cm.aptoide.pt.downloadmanager;

import com.liulishuo.filedownloader.BaseDownloadTask;
import com.liulishuo.filedownloader.FileDownloadLargeFileListener;
import rx.subjects.BehaviorSubject;

public class DownloadStatusListener extends FileDownloadLargeFileListener {

  private final Analytics analytics;
  private final BehaviorSubject<DownloadProgress> currentDownloadsSubject;

  public DownloadStatusListener(Analytics analytics,
      BehaviorSubject<DownloadProgress> currentDownloadsSubject) {
    this.analytics = analytics;
    this.currentDownloadsSubject = currentDownloadsSubject;
  }

  @Override protected void pending(BaseDownloadTask baseDownloadTask, long bytesTransferredSoFar,
      long totalBytesToTransfer) {
    final String hashCode = (String) baseDownloadTask.getTag(DownloadProgress.DOWNLOAD_HASH_CODE);
    final int fileIndex = (int) baseDownloadTask.getTag(DownloadProgress.APPLICATION_FILE_INDEX);
    currentDownloadsSubject.onNext(
        new DownloadProgress(hashCode, fileIndex, bytesTransferredSoFar, totalBytesToTransfer,
            baseDownloadTask.getSpeed(), DownloadStatus.PENDING));
  }

  @Override protected void progress(BaseDownloadTask baseDownloadTask, long bytesTransferredSoFar,
      long totalBytesToTransfer) {
    final String hashCode = (String) baseDownloadTask.getTag(DownloadProgress.DOWNLOAD_HASH_CODE);
    final int fileIndex = (int) baseDownloadTask.getTag(DownloadProgress.APPLICATION_FILE_INDEX);
    currentDownloadsSubject.onNext(
        new DownloadProgress(hashCode, fileIndex, bytesTransferredSoFar, totalBytesToTransfer,
            baseDownloadTask.getSpeed(), DownloadStatus.PROGRESS));
  }

  @Override protected void paused(BaseDownloadTask baseDownloadTask, long bytesTransferredSoFar,
      long totalBytesToTransfer) {
    final String hashCode = (String) baseDownloadTask.getTag(DownloadProgress.DOWNLOAD_HASH_CODE);
    final int fileIndex = (int) baseDownloadTask.getTag(DownloadProgress.APPLICATION_FILE_INDEX);
    currentDownloadsSubject.onNext(
        new DownloadProgress(hashCode, fileIndex, bytesTransferredSoFar, totalBytesToTransfer,
            baseDownloadTask.getSpeed(), DownloadStatus.PAUSED));
  }

  @Override protected void completed(BaseDownloadTask baseDownloadTask) {
    final String hashCode = (String) baseDownloadTask.getTag(DownloadProgress.DOWNLOAD_HASH_CODE);
    final int fileIndex = (int) baseDownloadTask.getTag(DownloadProgress.APPLICATION_FILE_INDEX);
    currentDownloadsSubject.onNext(
        new DownloadProgress(hashCode, fileIndex, DownloadStatus.COMPLETED));
    analytics.onDownloadComplete(hashCode);
  }

  @Override protected void error(BaseDownloadTask baseDownloadTask, Throwable throwable) {
    final String hashCode = (String) baseDownloadTask.getTag(DownloadProgress.DOWNLOAD_HASH_CODE);
    final int fileIndex = (int) baseDownloadTask.getTag(DownloadProgress.APPLICATION_FILE_INDEX);
    final String packageName = (String) baseDownloadTask.getTag(DownloadProgress.PACKAGE_NAME);
    final int versionCode = (int) baseDownloadTask.getTag(DownloadProgress.VERSION_CODE);
    currentDownloadsSubject.onNext(
        new DownloadProgress(hashCode, fileIndex, throwable, DownloadStatus.ERROR));
    analytics.onError(packageName, versionCode, throwable);
  }

  @Override protected void warn(BaseDownloadTask baseDownloadTask) {
    final String hashCode = (String) baseDownloadTask.getTag(DownloadProgress.DOWNLOAD_HASH_CODE);
    final int fileIndex = (int) baseDownloadTask.getTag(DownloadProgress.APPLICATION_FILE_INDEX);
    currentDownloadsSubject.onNext(
        new DownloadProgress(hashCode, fileIndex, DownloadStatus.WARNING));
  }
}
