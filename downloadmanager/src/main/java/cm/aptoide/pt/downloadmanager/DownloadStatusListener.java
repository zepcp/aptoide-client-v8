package cm.aptoide.pt.downloadmanager;

import com.liulishuo.filedownloader.BaseDownloadTask;
import com.liulishuo.filedownloader.FileDownloadLargeFileListener;
import rx.Observable;
import rx.subjects.BehaviorSubject;

public class DownloadStatusListener extends FileDownloadLargeFileListener {

  private final BehaviorSubject<DownloadStatus> downloadStatusBehaviorSubject;

  public DownloadStatusListener() {
    downloadStatusBehaviorSubject = BehaviorSubject.create();
  }

  public Observable<DownloadStatus> observeStatusChanges() {
    return downloadStatusBehaviorSubject.asObservable();
  }

  @Override protected void pending(BaseDownloadTask baseDownloadTask, long bytesTransferredSoFar,
      long totalBytesTransferred) {
    downloadStatusBehaviorSubject.onNext(DownloadStatus.PENDING);
  }

  @Override protected void progress(BaseDownloadTask baseDownloadTask, long bytesTransferredSoFar,
      long totalBytesTransferred) {
    downloadStatusBehaviorSubject.onNext(DownloadStatus.PROGRESS);
  }

  @Override protected void paused(BaseDownloadTask baseDownloadTask, long bytesTransferredSoFar,
      long totalBytesTransferred) {
    downloadStatusBehaviorSubject.onNext(DownloadStatus.PAUSED);
  }

  @Override protected void completed(BaseDownloadTask baseDownloadTask) {
    downloadStatusBehaviorSubject.onNext(DownloadStatus.COMPLETED);
  }

  @Override protected void error(BaseDownloadTask baseDownloadTask, Throwable throwable) {
    downloadStatusBehaviorSubject.onNext(DownloadStatus.ERROR);
  }

  @Override protected void warn(BaseDownloadTask baseDownloadTask) {
    downloadStatusBehaviorSubject.onNext(DownloadStatus.WARNING);
  }
}
