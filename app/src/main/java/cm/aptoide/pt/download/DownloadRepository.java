package cm.aptoide.pt.download;

import cm.aptoide.pt.database.accessors.DownloadAccessor;
import cm.aptoide.pt.downloadmanager.Download;
import cm.aptoide.pt.logger.Logger;
import java.util.ArrayList;
import java.util.List;
import rx.Observable;
import rx.schedulers.Schedulers;

public class DownloadRepository implements cm.aptoide.pt.downloadmanager.DownloadRepository {

  private static final String TAG = DownloadRepository.class.getName();

  private final DownloadAccessor accessor;

  public DownloadRepository(DownloadAccessor downloadAccessor) {
    this.accessor = downloadAccessor;
  }

  public Observable<List<Download>> getAll() {
    return accessor.getAll()
        .flatMapIterable(list -> list)
        .map(download -> mapFromDatabase(download))
        .toList();
  }

  @Override public Observable<Download> get(long downloadId) {
    return accessor.get(downloadId)
        .map(download -> mapFromDatabase(download));
  }

  public Observable<Download> get(String md5) {
    return accessor.get(md5)
        .map(download -> mapFromDatabase(download));
  }

  @Override public void delete(String md5) {
    accessor.delete(md5);
  }

  public void save(Download entity) {
    accessor.save(mapToDatabase(entity));
  }

  @Override public void save(List<Download> downloads) {
    List<cm.aptoide.pt.database.realm.Download> dbDownloads = new ArrayList<>(downloads.size());
    for (Download d : downloads) {
      dbDownloads.add(mapToDatabase(d));
    }
    accessor.save(dbDownloads);
  }

  @Override public Observable<List<Download>> getRunningDownloads() {
    return accessor.getRunningDownloads()
        .flatMapIterable(list -> list)
        .map(download -> mapFromDatabase(download))
        .toList();
  }

  @Override public Observable<Download> getNextDownloadInQueue() {
    return accessor.getDownloadsInQueue()
        .first()
        .map(downloads -> {
          if (downloads != null && !downloads.isEmpty()) {
            return mapFromDatabase(downloads.get(0));
          }
          return null;
        });
  }

  @Deprecated public Observable<List<Download>> getAsList(String md5) {
    return accessor.getAsList(md5)
        .observeOn(Schedulers.io())
        .map(downloads -> {
          if (downloads.isEmpty()) {
            return null;
          } else {
            return downloads.get(0);
          }
        })
        .map(download -> mapFromDatabase(download))
        .toList();
  }

  private cm.aptoide.pt.database.realm.Download mapToDatabase(Download download) {
    if (DownloadAdapter.class.isAssignableFrom(download.getClass())) {
      return ((DownloadAdapter) download).getDecoratedEntity();
    }
    throw new IllegalStateException(
        String.format("Only classes that extend %s are supported in this method call",
            DownloadAdapter.class.getSimpleName()));
  }

  private Download mapFromDatabase(cm.aptoide.pt.database.realm.Download download) {
    if (download == null) {
      Logger.w(TAG, "Avoid calling this method with a null Download object");
      return null;
    }

    return new DownloadAdapter(download);
  }
}
