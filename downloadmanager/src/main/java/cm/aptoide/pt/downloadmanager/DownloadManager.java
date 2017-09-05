package cm.aptoide.pt.downloadmanager;

import android.support.annotation.NonNull;
import java.util.List;
import rx.Observable;
import rx.Single;

public interface DownloadManager {

  @NonNull Observable<Download> observeDownloadChanges(String downloadHash);

  @NonNull Observable<List<Download>> observeAllDownloadChanges();

  @NonNull Single<Boolean> isDownloading();

  void startDownload(Download download)
      throws IllegalArgumentException, IllegalAccessException;

  void removeDownload(String downloadHash);

  void pauseDownload(String downloadHash);

  void pauseAllDownloads();

  void clearAllDownloads();
}
