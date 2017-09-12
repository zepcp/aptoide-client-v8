package cm.aptoide.pt.downloadmanager.base;

import android.support.annotation.NonNull;
import rx.Observable;

public interface DownloadManager {

  @NonNull Observable<Download> observeDownloadChanges(DownloadRequest downloadRequest);

  @NonNull Observable<Download> observeAllDownloadChanges();

  void startDownload(DownloadRequest downloadRequest);

  void removeDownload(DownloadRequest downloadRequest);

  void pauseDownload(DownloadRequest downloadRequest);

  void removeAllDownloads();

  void pauseAllDownloads();
}
