package cm.aptoide.pt.downloadmanager;

import java.util.List;
import rx.Completable;
import rx.Observable;

public interface DownloadManager {
  Observable<Download> startDownload(Download download) throws IllegalArgumentException;

  Observable<Download> getDownload(String md5);

  Observable<Download> getCurrentDownload();

  Observable<List<Download>> getDownloads();

  Observable<List<Download>> getCurrentDownloads();

  Completable pauseAllDownloads();

  boolean isDownloading();

  Completable removeDownload(String md5);

  Completable pauseDownloadSync(String md5);

  Completable pauseDownload(String md5);

  Observable<Void> invalidateDatabase();
}
