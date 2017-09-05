package cm.aptoide.pt.downloadmanager;

import java.util.List;
import rx.Observable;

public interface DownloadRepository {
  Observable<List<Download>> getAll();

  Observable<Download> get(long downloadId);

  Observable<Download> get(String md5);

  void delete(String md5);

  void save(Download download);

  void saveIfNotExisting(Download download);

  void save(List<Download> download);

  Observable<List<Download>> getCurrentDownloads();

  Observable<List<Download>> getAllInQueue();

  Observable<Download> getNextDownloadInQueue();
}
