package cm.aptoide.pt.downloadmanager;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import cm.aptoide.pt.crashreports.CrashLogger;
import cm.aptoide.pt.crashreports.CrashReport;
import cm.aptoide.pt.logger.Logger;
import cm.aptoide.pt.utils.AptoideUtils;
import cm.aptoide.pt.utils.FileUtils;
import com.liulishuo.filedownloader.BaseDownloadTask;
import com.liulishuo.filedownloader.FileDownloadListener;
import com.liulishuo.filedownloader.FileDownloader;
import java.io.File;
import java.util.List;
import rx.Completable;
import rx.Observable;
import rx.Single;
import rx.schedulers.Schedulers;
import rx.subjects.BehaviorSubject;

/**
 * Created by trinkes on 5/13/16.
 */
public class AptoideDownloadManager extends Service implements DownloadManager {

  private static final String TAG = AptoideDownloadManager.class.getSimpleName();

  private static final int RETRY_TIMES = 3;
  private static final int APTOIDE_DOWNLOAD_TASK_TAG_KEY = 888;

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

  @NonNull @Override
  public Observable<Download> observeDownloadChanges(DownloadRequest downloadRequest) {
    final String downloadHash = downloadRequest.getHashCode();
    return currentDownloadsSubject.asObservable()
        .flatMapIterable(list -> list)
        .filter(download1 -> TextUtils.equals(download1.getHashCode(), downloadHash));
  }

  @NonNull @Override public Observable<Download> observeAllDownloadChanges() {
    return null;
  }

  @Override public void startDownload(DownloadRequest downloadRequest) {
    validate(downloadRequest);
    downloadRepository.get(downloadRequest.getHashCode())
        .first()
        .toSingle()
        .subscribe(download -> {
          if (download == null) {
            downloadRepository.insertNew(downloadRequest.getHashCode(),
                downloadRequest.getApplicationName(), downloadRequest.getApplicationIcon(),
                downloadRequest.getDownloadAction()
                    .getValue(), downloadRequest.getPackageName(), downloadRequest.getVersionCode(),
                downloadRequest.getVersionName(), downloadRequest.getFilesToDownload());
          }
        });
  }

  @Override public void removeDownload(DownloadRequest downloadRequest) {
    validate(downloadRequest);
    pauseDownload(downloadRequest);
    final String downloadHash = downloadRequest.getHashCode();
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

  @Override public void pauseDownload(DownloadRequest downloadRequest) {
    validate(downloadRequest);
    final String downloadHash = downloadRequest.getHashCode();
    internalPause(downloadHash).subscribe();
  }

  @Override public void removeAllDownloads() {

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

  public Single<Boolean> isDownloading() {
    return currentDownloadsSubject.first()
        .toSingle()
        .map(list -> list != null && list.size() > 0);
  }

  private void validate(DownloadRequest download) throws IllegalArgumentException {
    if (download.getHashCode() == null || download.getHashCode()
        .trim()
        .equals("")) {
      throw new IllegalArgumentException(
          String.format("%s does not have an hash code", DownloadRequest.class.getSimpleName()));
    }

    if (download.getPackageName() == null || download.getPackageName()
        .trim()
        .equals("")) {
      throw new IllegalArgumentException(
          String.format("%s does not have a package name", DownloadRequest.class.getSimpleName()));
    }

    final List<DownloadFile> filesToDownload = download.getFilesToDownload();
    if (filesToDownload.isEmpty()) {
      throw new IllegalArgumentException(
          String.format("%s does not have files", DownloadRequest.class.getSimpleName()));
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

  void startDownload(Download download) {
    final List<DownloadFile> filesToDownload = download.getFilesToDownload();
    if (filesToDownload != null) {
      DownloadFile fileToDownload;
      FileDownloadListener listener = null; // FIXME
      for (int i = 0; i < filesToDownload.size(); i++) {

        fileToDownload = filesToDownload.get(i);

        if (TextUtils.isEmpty(fileToDownload.getLink())) {
          throw new IllegalArgumentException("A link to download must be provided");
        }
        BaseDownloadTask baseDownloadTask = fileDownloader.create(fileToDownload.getLink())
            .setAutoRetryTimes(RETRY_TIMES);
        /*
         * Aptoide - events 2 : download
         * Get X-Mirror and add to the event
         */
        baseDownloadTask.addHeader(Constants.VERSION_CODE,
            String.valueOf(download.getVersionCode()));
        baseDownloadTask.addHeader(Constants.PACKAGE, download.getPackageName());
        baseDownloadTask.addHeader(Constants.FILE_TYPE, String.valueOf(i));
        /*
         * end
         */

        baseDownloadTask.setTag(APTOIDE_DOWNLOAD_TASK_TAG_KEY, this);
        if (fileToDownload.getFileName()
            .endsWith(".temp")) {
          fileToDownload.setFileName(fileToDownload.getFileName()
              .replace(".temp", ""));
        }

        fileToDownload.setDownloadId(baseDownloadTask.setListener(listener)
            .setCallbackProgressTimes(Constants.PROGRESS_MAX_VALUE)
            .setPath(filePaths.getDownloadsStoragePath() + fileToDownload.getFileName())
            .asInQueueTask()
            .enqueue());
        fileToDownload.setPath(filePaths.getDownloadsStoragePath());
        fileToDownload.setFileName(fileToDownload.getFileName() + ".temp");
      }

      fileDownloader.start(listener, true);
    }
    saveDownloadInDb(download);
  }

  void saveDownloadInDb(Download download) {
    Completable.fromAction(() -> downloadRepository.save(download))
        .subscribeOn(Schedulers.io())
        .subscribe(() -> {
        }, err -> CrashReport.getInstance()
            .log(err));
  }

  void setDownloadStatus(DownloadStatus status, Download download,
      @Nullable BaseDownloadTask task) {
    if (task != null) {
      for (final DownloadFile fileToDownload : download.getFilesToDownload()) {
        if (fileToDownload.getDownloadId() == task.getId()) {
          fileToDownload.setStatus(status);
        }
      }
    }

    download.setOverallDownloadStatus(status);
    saveDownloadInDb(download);
  }

  @Deprecated synchronized void currentDownloadFinished() {
    Download download = null; // FIXME
    if (download != null) {
      final AptoideDownloadTask downloadTask =
          new AptoideDownloadTask(downloadRepository, download, analytics, this, filePaths,
              fileDownloader, crashLogger);
      //startDownload(downloadTask);
      Logger.d(TAG, "Download with hash " + download.getHashCode() + " started");
    } else {
      cacheHelper.cleanCache()
          .subscribe(cleanedSize -> Logger.d(TAG,
              "cleaned size: " + AptoideUtils.StringU.formatBytes(cleanedSize, false)),
              err -> crashLogger.log(err));
    }
  }

  Observable<Boolean> checkMd5AndMoveFileToRightPlace(Download download) {
    return Observable.fromCallable(() -> {
      for (final DownloadFile fileToDownload : download.getFilesToDownload()) {
        fileToDownload.setFileName(fileToDownload.getFileName()
            .replace(".temp", ""));
        if (!TextUtils.isEmpty(fileToDownload.getHashCode())) {
          if (!TextUtils.equals(AptoideUtils.AlgorithmU.computeMd5(
              new File(filePaths.getDownloadsStoragePath() + fileToDownload.getFileName())),
              fileToDownload.getHashCode())) {
            return false;
          }
        }
        String newFilePath = getFilePathFromFileType(fileToDownload);
        fileUtils.copyFile(filePaths.getDownloadsStoragePath(), newFilePath,
            fileToDownload.getFileName());
        fileToDownload.setPath(newFilePath);
      }
      return true;
    });
  }

  @NonNull private String getFilePathFromFileType(DownloadFile fileToDownload) {
    String path;
    switch (fileToDownload.getFileType()) {
      case APK:
        path = filePaths.getApkPath();
        break;
      case OBB:
        path = filePaths.getObbPath() + fileToDownload.getPackageName() + "/";
        break;
      case GENERIC:
      default:
        path = filePaths.getDownloadsStoragePath();
        break;
    }
    return path;
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
