package cm.aptoide.pt.downloadmanager;

import android.support.annotation.NonNull;
import java.util.List;
import rx.Observable;
import rx.Single;

public interface DownloadManager {

  @NonNull Observable<Download> observeDownloadChanges(DownloadRequest downloadRequest);

  @NonNull Observable<List<Download>> observeAllDownloadChanges();

  @NonNull Single<Boolean> isDownloading();

  void startDownload(DownloadRequest downloadRequest);

  void removeDownload(DownloadRequest downloadRequest);

  void pauseDownload(DownloadRequest downloadRequest);

  void pauseAllDownloads();

  void clearAllDownloads();
}
