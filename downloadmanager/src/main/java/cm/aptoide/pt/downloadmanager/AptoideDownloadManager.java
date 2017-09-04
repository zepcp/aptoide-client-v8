package cm.aptoide.pt.downloadmanager;

import android.support.annotation.NonNull;
import cm.aptoide.pt.crashreports.CrashLogger;
import cm.aptoide.pt.logger.Logger;
import cm.aptoide.pt.utils.AptoideUtils;
import cm.aptoide.pt.utils.FileUtils;
import com.liulishuo.filedownloader.FileDownloader;
import java.util.List;
import rx.Completable;
import rx.Observable;

/**
 * Created by trinkes on 5/13/16.
 */
public class AptoideDownloadManager implements DownloadManager {
  private static final String TAG = AptoideDownloadManager.class.getSimpleName();

  private final CrashLogger crashLogger;
  private final FilePaths filePaths;
  private boolean isDownloading = false;
  private boolean isPausing = false;
  private DownloadRepository downloadRepository;
  private CacheManager cacheHelper;
  private FileUtils fileUtils;
  private Analytics analytics;
  private FileDownloader fileDownloader;

  public AptoideDownloadManager(DownloadRepository downloadRepository, CacheManager cacheHelper,
      FileUtils fileUtils, Analytics analytics, FileDownloader fileDownloader,
      FilePaths filePaths, CrashLogger crashLogger) {
    this.fileDownloader = fileDownloader;
    this.analytics = analytics;
    this.cacheHelper = cacheHelper;
    this.fileUtils = fileUtils;
    this.filePaths = filePaths;
    this.downloadRepository = downloadRepository;
    this.crashLogger = crashLogger;
  }

  /**
   * @param download info about the download to be made.
   *
   * @return Observable to be subscribed if download updates needed or null if download is done
   * already
   *
   * @throws IllegalArgumentException if the appToDownload object is not filled correctly, this
   * exception will be thrown with the cause in the detail
   * message.
   */
  @Override public Observable<Download> startDownload(Download download) throws IllegalArgumentException {
    return getDownloadStatus(download.getMd5()).first()
        .flatMap(status -> {
          if (status == DownloadStatus.COMPLETED) {
            return Observable.just(download);
          }

          return Completable.fromAction(() -> startNewDownload(download))
              .toObservable()
              .flatMap(__ -> getDownload(download.getMd5()));
        });
  }

  private void startNewDownload(Download download) {
    download.setOverallDownloadStatus(DownloadStatus.IN_QUEUE);
    //commented to prevent the ui glitch with "0" value
    // (trusting in progress value from outside can be dangerous)
    //		download.setOverallProgress(0);
    download.setTimeStamp(System.currentTimeMillis());
    downloadRepository.save(download);

    startNextDownload();
  }

  /**
   * Observe changes to a download. This observable never completes it will emmit items whenever
   * the download state changes.
   *
   * @return observable for download state changes.
   */
  @Override public Observable<Download> getDownload(String md5) {
    return downloadRepository.get(md5)
        .flatMap(download -> {
          if (isInvalid(download)) {
            return Observable.error(new DownloadNotFoundException(md5));
          } else {
            return Observable.just(download);
          }
        })
        .takeUntil(storedDownload -> storedDownload.getOverallDownloadStatus()
            == DownloadStatus.COMPLETED);
  }

  /*
  public Observable<Download> getAsListDownload(String md5) {
    return downloadRepository.get(md5)
        .map(download -> {
          if (isInvalid(download)) {
            return null;
          }
          return download;
        })
        .distinctUntilChanged();
  }
  */

  private boolean isInvalid(Download download) {
    return download == null || (download.getOverallDownloadStatus() == DownloadStatus.COMPLETED
        && getStateIfFileExists(download) == DownloadStatus.FILE_MISSING);
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

  @Override public Observable<Download> getCurrentDownload() {
    return getDownloads().flatMapIterable(downloads -> downloads)
        .filter(downloads -> downloads.getOverallDownloadStatus() == DownloadStatus.PROGRESS);
  }

  @Override public Observable<List<Download>> getDownloads() {
    return downloadRepository.getAll();
  }

  @Override public Observable<List<Download>> getCurrentDownloads() {
    return downloadRepository.getRunningDownloads();
  }

  /**
   * Pause all the downloads
   */
  @Override public Completable pauseAllDownloads() {
    return downloadRepository.getRunningDownloads()
        .first()
        .toSingle()
        .doOnUnsubscribe(() -> isPausing = false)
        .flatMapCompletable(downloads -> Completable.fromAction(() -> {
          fileDownloader.pauseAll();
          isPausing = true;
          for (int i = 0; i < downloads.size(); i++) {
            downloads.get(i)
                .setOverallDownloadStatus(DownloadStatus.PAUSED);
          }
          downloadRepository.save(downloads);
        }));
  }

  private Observable<DownloadStatus> getDownloadStatus(String md5) {
    return getDownload(md5).map(download -> {
      if (download != null) {
        if (download.getOverallDownloadStatus() == DownloadStatus.COMPLETED) {
          return getStateIfFileExists(download);
        }
        return download.getOverallDownloadStatus();
      } else {
        return DownloadStatus.NOT_DOWNLOADED;
      }
    });
  }

  void currentDownloadFinished() {
    startNextDownload();
  }

  private synchronized void startNextDownload() {
    if (!isDownloading && !isPausing) {
      isDownloading = true;
      getNextDownload().first()
          .subscribe(download -> {
            if (download != null) {
              final DownloadTask downloadTask =
                  new DownloadTask(downloadRepository, download, fileUtils, analytics, this,
                      filePaths, fileDownloader, crashLogger);
              downloadTask.startDownload();
              Logger.d(TAG, "Download with md5 " + download.getMd5() + " started");
            } else {
              isDownloading = false;
              cacheHelper.cleanCache()
                  .subscribe(cleanedSize -> Logger.d(TAG,
                      "cleaned size: " + AptoideUtils.StringU.formatBytes(cleanedSize, false)),
                      err -> crashLogger.log(err));
            }
          }, err -> crashLogger.log(err));
    }
  }

  private Observable<Download> getNextDownload() {
    return downloadRepository.getNextDownloadInQueue();
  }

  /**
   * check if there is any download in progress
   *
   * @return true if there is at least 1 download in progress, false otherwise
   */
  @Override public boolean isDownloading() {
    return isDownloading;
  }

  void setDownloading(boolean downloading) {
    isDownloading = downloading;
  }

  @Override public Completable removeDownload(String md5) {
    return pauseDownload(md5).andThen(downloadRepository.get(md5)
        .first(download -> download.getOverallDownloadStatus() == DownloadStatus.PAUSED))
        .doOnNext(download -> {
          deleteDownloadedFiles(download);
          deleteDownloadFromDb(download.getMd5());
        })
        .onErrorResumeNext(throwable -> {
          if (throwable instanceof NullPointerException) {
            return Observable.error(new DownloadNotFoundException(md5));
          }
          return Observable.error(throwable);
        })
        .toCompletable();
  }

  void deleteDownloadedFiles(Download download) {
    for (DownloadFile fileToDownload : download.getFilesToDownload()) {
      fileDownloader.clear(fileToDownload.getDownloadId(), fileToDownload.getFilePath());
      FileUtils.removeFile(fileToDownload.getFilePath());
      FileUtils.removeFile(filePaths.getDownloadsStoragePath() + fileToDownload.getFileName() + ".temp");
    }
  }

  @Override public Completable pauseDownloadSync(String md5) {
    return internalPause(md5).toCompletable();
  }

  @NonNull private Observable<Download> internalPause(String md5) {
    return downloadRepository.get(md5)
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

  @Override public Completable pauseDownload(String md5) {
    return internalPause(md5).toCompletable();
  }

  private void deleteDownloadFromDb(String md5) {
    downloadRepository.delete(md5);
  }

  @Override public Observable<Void> invalidateDatabase() {
    return getDownloads().first()
        .flatMapIterable(downloads -> downloads)
        .filter(download -> getStateIfFileExists(download) == DownloadStatus.FILE_MISSING)
        .map(download -> {
          downloadRepository.delete(download.getMd5());
          return null;
        })
        .toList()
        .flatMap(success -> Observable.just(null));
  }
}
