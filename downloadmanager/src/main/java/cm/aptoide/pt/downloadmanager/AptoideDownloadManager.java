package cm.aptoide.pt.downloadmanager;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import cm.aptoide.pt.crashreports.CrashLogger;
import cm.aptoide.pt.logger.Logger;
import cm.aptoide.pt.utils.AptoideUtils;
import cm.aptoide.pt.utils.FileUtils;
import com.liulishuo.filedownloader.FileDownloader;
import java.util.List;
import rx.Completable;
import rx.Observable;
import rx.Single;
import rx.subjects.BehaviorSubject;

/**
 * Created by trinkes on 5/13/16.
 */
public class AptoideDownloadManager extends Service implements DownloadManager {

  private static final String TAG = AptoideDownloadManager.class.getSimpleName();

  private final CrashLogger crashLogger;
  private final FilePaths filePaths;
  private final DownloadRepository downloadRepository;
  private final CacheManager cacheHelper;
  private final FileUtils fileUtils;
  private final Analytics analytics;
  private final FileDownloader fileDownloader;
  private final BehaviorSubject<List<Download>> currentDownloadsSubject;

  public AptoideDownloadManager(DownloadRepository downloadRepository, CacheManager cacheHelper,
      FileUtils fileUtils, Analytics analytics, FileDownloader fileDownloader, FilePaths filePaths,
      CrashLogger crashLogger) {
    this.fileDownloader = fileDownloader;
    this.analytics = analytics;
    this.cacheHelper = cacheHelper;
    this.fileUtils = fileUtils;
    this.filePaths = filePaths;
    this.downloadRepository = downloadRepository;
    this.crashLogger = crashLogger;
    currentDownloadsSubject = BehaviorSubject.create();
  }

  @Override public void onCreate() {
    super.onCreate();
  }

  @Nullable @Override public IBinder onBind(Intent intent) {
    return null;
  }

  private void startNewDownload(Download download) {
    download.setOverallDownloadStatus(DownloadStatus.IN_QUEUE);
    download.setTimeStamp(System.currentTimeMillis());
    downloadRepository.save(download);
    downloadRepository.getCurrentDownloads()
        .first()
        .toSingle()
        .subscribe(downloads -> currentDownloadsSubject.onNext(downloads));
  }

  @NonNull @Override public Observable<Download> observeDownloadChanges(String downloadHash) {
    return currentDownloadsSubject.asObservable()
        .flatMapIterable(list -> list)
        .filter(download -> TextUtils.equals(download.getHashCode(), downloadHash));
  }

  @Override public Observable<List<Download>> observeAllDownloadChanges() {
    return currentDownloadsSubject.asObservable();
  }

  /**
   * check if there is any download in progress
   *
   * @return true if there is at least 1 download in progress, false otherwise
   */
  @Override public Single<Boolean> isDownloading() {
    return currentDownloadsSubject
        .first()
        .toSingle()
        .map(list -> list != null && list.size() > 0);
  }

  @Override public void startDownload(Download download) throws IllegalArgumentException {
    validate(download);
    downloadRepository.saveIfNotExisting(download);

    getDownloadStatus(download.getHashCode()).first()
        .subscribe(status -> {
          if (status != DownloadStatus.COMPLETED) {
            downloadRepository.save(download);
            startNewDownload(download);
          }
        });
  }

  @Override public void removeDownload(String downloadHash) {
    pauseDownload(downloadHash);
    downloadRepository.get(downloadHash)
        .first(download -> download.getOverallDownloadStatus() == DownloadStatus.PAUSED)
        .subscribe(download -> {
          deleteDownloadedFiles(download);
          deleteDownloadFromDb(download.getHashCode());
        }, throwable -> {
          if (throwable instanceof NullPointerException) {
            crashLogger.log(new DownloadNotFoundException(downloadHash));
          }
          crashLogger.log(throwable);
        });
  }

  @Override public void pauseDownload(String downloadHash) {
    internalPause(downloadHash).subscribe();
  }

  /**
   * Pause all the downloads
   */
  @Override public void pauseAllDownloads() {
    downloadRepository.getCurrentDownloads()
        .first()
        .toSingle()
        .subscribe(downloads -> Completable.fromAction(() -> {
          fileDownloader.pauseAll();
          for (int i = 0; i < downloads.size(); i++) {
            downloads.get(i)
                .setOverallDownloadStatus(DownloadStatus.PAUSED);
          }
          downloadRepository.save(downloads);
        }));
  }

  public void clearAllDownloads() {
    getDownloads().first()
        .flatMapIterable(downloads -> downloads)
        .filter(download -> getStateIfFileExists(download) == DownloadStatus.FILE_MISSING)
        .subscribe(download -> downloadRepository.delete(download.getHashCode()));
  }

  private void validate(Download download) throws IllegalArgumentException {
    if (TextUtils.isEmpty(download.getPackageName())) {
      throw new IllegalArgumentException("Download does not have a package name");
    }

    if (TextUtils.isEmpty(download.getHashCode())) {
      throw new IllegalArgumentException("Download does not have an hash code");
    }

    final List<DownloadFile> filesToDownload = download.getFilesToDownload();
    if (filesToDownload.isEmpty()) {
      throw new IllegalArgumentException("Download does not have files");
    }
  }

  private DownloadStatus getStateIfFileExists(Download downloadToCheck) {
    DownloadStatus downloadStatus = DownloadStatus.COMPLETED;
    if (downloadToCheck.getOverallDownloadStatus() == DownloadStatus.PROGRESS) {
      downloadStatus = DownloadStatus.PROGRESS;
    } else {
      for (final DownloadFile fileToDownload : downloadToCheck.getFilesToDownload()) {
        if (!FileUtils.fileExists(fileToDownload.getFilePath())) {
          downloadStatus = DownloadStatus.FILE_MISSING;
          break;
        }
      }
    }
    return downloadStatus;
  }

  public Observable<Download> getCurrentDownload() {
    return getDownloads().flatMapIterable(downloads -> downloads)
        .filter(downloads -> downloads.getOverallDownloadStatus() == DownloadStatus.PROGRESS);
  }

  public Observable<List<Download>> getDownloads() {
    return downloadRepository.getAll();
  }

  @Deprecated synchronized void currentDownloadFinished() {
    startNextDownload();
  }

  @Deprecated private synchronized void startNextDownload() {
    getNextDownload().first()
        .subscribe(download -> {
          if (download != null) {
            final AptoideDownloadTask downloadTask =
                new AptoideDownloadTask(downloadRepository, download, fileUtils, analytics, this,
                    filePaths, fileDownloader, crashLogger);
            downloadTask.startDownload();
            Logger.d(TAG, "Download with hash " + download.getHashCode() + " started");
          } else {
            cacheHelper.cleanCache()
                .subscribe(cleanedSize -> Logger.d(TAG,
                    "cleaned size: " + AptoideUtils.StringU.formatBytes(cleanedSize, false)),
                    err -> crashLogger.log(err));
          }
        }, err -> crashLogger.log(err));
  }

  private Observable<Download> getNextDownload() {
    return downloadRepository.getNextDownloadInQueue();
  }

  void deleteDownloadedFiles(Download download) {
    for (DownloadFile fileToDownload : download.getFilesToDownload()) {
      fileDownloader.clear(fileToDownload.getDownloadId(), fileToDownload.getFilePath());
      FileUtils.removeFile(fileToDownload.getFilePath());
      FileUtils.removeFile(
          filePaths.getDownloadsStoragePath() + fileToDownload.getFileName() + ".temp");
    }
  }

  @NonNull private Observable<Download> internalPause(String downloadHash) {
    return downloadRepository.get(downloadHash)
        .first()
        .map(download -> {
          download.setOverallDownloadStatus(DownloadStatus.PAUSED);
          downloadRepository.save(download);
          for (int i = download.getFilesToDownload()
              .size() - 1; i >= 0; i--) {
            fileDownloader.pause(download.getFilesToDownload()
                .get(i)
                .getDownloadId());
          }
          return download;
        });
  }

  private void deleteDownloadFromDb(String downloadHash) {
    downloadRepository.delete(downloadHash);
  }
}
