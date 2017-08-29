package cm.aptoide.pt.downloadmanager;

import java.util.List;
import rx.Observable;

public interface DownloadRepository {
  Observable<List<Download>> getAll();

  Observable<Download> get(long downloadId);

  Observable<Download> get(String md5);

  void delete(String md5);

  void save(Download download);

  void save(List<Download> download);

  Observable<List<Download>> getRunningDownloads();

  Observable<Download> getNextDownloadInQueue();

  @Deprecated Observable<List<Download>> getAsList(String md5);
}
