package cm.aptoide.pt.download;

import android.support.annotation.NonNull;
import cm.aptoide.pt.database.accessors.DownloadAccessor;
import cm.aptoide.pt.database.realm.FileToDownload;
import cm.aptoide.pt.downloadmanager.Download;
import cm.aptoide.pt.downloadmanager.DownloadFile;
import cm.aptoide.pt.logger.Logger;
import io.realm.RealmList;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import rx.Observable;

public class DownloadRepository implements cm.aptoide.pt.downloadmanager.DownloadRepository {

  private static final String TAG = DownloadRepository.class.getName();

  private final DownloadAccessor accessor;

  public DownloadRepository(DownloadAccessor downloadAccessor) {
    this.accessor = downloadAccessor;
  }

  public Observable<? extends Collection<Download>> getAll() {
    return accessor.getAll()
        .flatMapIterable(list -> list)
        .map(download -> mapFromDatabase(download))
        .toList();
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

  @Override public <T extends Collection<Download>> void save(T downloads) {
    List<cm.aptoide.pt.database.realm.Download> dbDownloads = new ArrayList<>(downloads.size());
    for (Download d : downloads) {
      dbDownloads.add(mapToDatabase(d));
    }
    accessor.save(dbDownloads);
  }

  @Override public Observable<List<Download>> getCurrentDownloads() {
    return accessor.getCurrentDownloads()
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

  //@Override public Observable<List<Download>> getAllInQueue() {
  //  return accessor.getDownloadsInQueue()
  //      .flatMapIterable(list -> list)
  //      .map(download -> mapFromDatabase(download))
  //      .toList();
  //}

  @Override public <T extends Collection<DownloadFile>> Download insertNew(String downloadHashCode,
      String appName, String icon, int action, String packageName, int versionCode,
      String versionName, T downloadFiles) {
    cm.aptoide.pt.database.realm.Download download = new cm.aptoide.pt.database.realm.Download();
    download.setMd5(downloadHashCode);
    download.setAppName(appName);
    download.setIcon(icon);
    download.setAction(action);
    download.setDownloadError(cm.aptoide.pt.database.realm.Download.NO_ERROR);
    download.setDownloadSpeed(0);
    download.setOverallDownloadStatus(cm.aptoide.pt.database.realm.Download.IN_QUEUE);
    download.setOverallProgress(0);
    download.setPackageName(packageName);
    download.setScheduled(true);
    download.setTimeStamp(System.currentTimeMillis());
    download.setVersionCode(versionCode);
    download.setVersionName(versionName);

    RealmList<FileToDownload> listFilesToDownload = new RealmList<>();
    for (DownloadFile downloadFile : downloadFiles) {
      FileToDownload fileToDownload = getFileToDownload(downloadFile);
      listFilesToDownload.add(fileToDownload);
    }
    download.setFilesToDownload(listFilesToDownload);

    accessor.saveIfNotExisting(download);

    return new DownloadAdapter(download);
  }

  @Deprecated public Observable<Download> get(long downloadId) {
    return accessor.get(downloadId)
        .map(download -> mapFromDatabase(download));
  }

  @NonNull private FileToDownload getFileToDownload(DownloadFile downloadFile) {
    FileToDownload fileToDownload = new FileToDownload();
    fileToDownload.setAltLink(downloadFile.getAltLink());
    fileToDownload.setDownloadId(downloadFile.getDownloadId());
    fileToDownload.setFileName(downloadFile.getFileName());
    fileToDownload.setFileType(downloadFile.getFileType()
        .getValue());
    fileToDownload.setLink(downloadFile.getLink());
    fileToDownload.setMd5(downloadFile.getMd5());
    fileToDownload.setPackageName(downloadFile.getPackageName());
    fileToDownload.setPath(downloadFile.getPath());
    fileToDownload.setProgress(downloadFile.getProgress());
    fileToDownload.setStatus(downloadFile.getStatus()
        .getValue());
    return fileToDownload;
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
