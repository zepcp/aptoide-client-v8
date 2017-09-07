/*
 * Copyright (c) 2016.
 * Modified on 02/09/2016.
 */

package cm.aptoide.pt.downloadmanager;

import android.support.annotation.NonNull;
import android.text.TextUtils;
import cm.aptoide.pt.crashreports.CrashLogger;
import cm.aptoide.pt.logger.Logger;
import com.liulishuo.filedownloader.BaseDownloadTask;
import com.liulishuo.filedownloader.FileDownloadLargeFileListener;
import com.liulishuo.filedownloader.FileDownloader;
import com.liulishuo.filedownloader.exception.FileDownloadHttpException;
import com.liulishuo.filedownloader.exception.FileDownloadOutOfSpaceException;
import rx.Observable;
import rx.schedulers.Schedulers;

/**
 * Created by trinkes on 5/13/16.
 */
class AptoideDownloadTask extends FileDownloadLargeFileListener {

  private static final int FILE_NOT_FOUND_HTTP_ERROR = 404;
  private static final String TAG = AptoideDownloadTask.class.getSimpleName();
  private final Download download;
  private final DownloadRepository downloadRepository;
  private final AptoideDownloadManager downloadManager;
  private final FilePaths filePaths;
  private final CrashLogger crashLogger;
  private Analytics analytics;
  private FileDownloader fileDownloader;

  AptoideDownloadTask(DownloadRepository downloadRepository, Download download, Analytics analytics,
      AptoideDownloadManager downloadManager, FilePaths filePaths, FileDownloader fileDownloader,
      CrashLogger crashLogger) {
    this.analytics = analytics;
    this.download = download;
    this.downloadRepository = downloadRepository;
    this.downloadManager = downloadManager;
    this.filePaths = filePaths;
    this.fileDownloader = fileDownloader;
    this.crashLogger = crashLogger;
  }

  @Deprecated @NonNull private Download updateProgress() {
    if (download.getOverallProgress() >= Constants.PROGRESS_MAX_VALUE
        || download.getOverallDownloadStatus() != DownloadStatus.PROGRESS) {
      return download;
    }

    int progress = 0;
    for (final DownloadFile fileToDownload : download.getFilesToDownload()) {
      progress += fileToDownload.getProgress();
    }
    download.setOverallProgress((int) Math.floor((float) progress / download.getFilesToDownload()
        .size()));
    downloadManager.saveDownloadInDb(download);
    Logger.d(TAG,
        "Download: " + download.getHashCode() + " Progress: " + download.getOverallProgress());
    return download;
  }

  @Override protected void pending(BaseDownloadTask task, long soFarBytes, long totalBytes) {
    downloadManager.setDownloadStatus(DownloadStatus.PENDING, download, task);
  }

  @Override protected void pending(BaseDownloadTask task, int soFarBytes, int totalBytes) {
    pending(task, (long) soFarBytes, (long) totalBytes);
    downloadManager.setDownloadStatus(DownloadStatus.PENDING, download, task);
  }

  @Override protected void progress(BaseDownloadTask task, long soFarBytes, long totalBytes) {
    for (DownloadFile fileToDownload : download.getFilesToDownload()) {
      if (fileToDownload.getDownloadId() == task.getId()) {
        //sometimes to totalBytes = 0, i believe that's when a 301(Moved Permanently) http error occurs
        if (totalBytes > 0) {
          fileToDownload.setProgress(
              (int) Math.floor((float) soFarBytes / totalBytes * Constants.PROGRESS_MAX_VALUE));
        } else {
          fileToDownload.setProgress(0);
        }
      }
    }
    this.download.setDownloadSpeed(task.getSpeed() * 1024);
    if (download.getOverallDownloadStatus() != DownloadStatus.PROGRESS) {
      downloadManager.setDownloadStatus(DownloadStatus.PROGRESS, download, task);
    }
  }

  @Override protected void progress(BaseDownloadTask task, int soFarBytes, int totalBytes) {
    progress(task, (long) soFarBytes, (long) totalBytes);
  }

  @Override protected void paused(BaseDownloadTask task, long soFarBytes, long totalBytes) {
    downloadManager.setDownloadStatus(DownloadStatus.PAUSED, download, task);
    downloadManager.currentDownloadFinished();
  }

  @Override protected void paused(BaseDownloadTask task, int soFarBytes, int totalBytes) {
    paused(task, (long) soFarBytes, (long) totalBytes);
  }

  @Override protected void blockComplete(BaseDownloadTask task) {

  }

  @Override protected void completed(BaseDownloadTask task) {
    Observable.from(download.getFilesToDownload())
        .filter(file -> file.getDownloadId() == task.getId())
        .flatMap(file -> {
          file.setStatus(DownloadStatus.COMPLETED);
          for (final DownloadFile fileToDownload : download.getFilesToDownload()) {
            if (fileToDownload.getStatus() != DownloadStatus.COMPLETED) {
              file.setProgress(Constants.PROGRESS_MAX_VALUE);
              return Observable.just(null);
            }
          }
          return downloadManager.checkMd5AndMoveFileToRightPlace(download)
              .doOnNext(fileWasMoved -> {
                if (fileWasMoved) {
                  Logger.d(TAG, "Expected file hash and downloaded file hash match");
                } else {
                  Logger.e(TAG, "Expected file hash and downloaded file hash do not match");
                }
              })
              .doOnNext(fileWasMoved -> {
                if (fileWasMoved) {
                  file.setProgress(Constants.PROGRESS_MAX_VALUE);
                } else {
                  downloadManager.deleteDownloadedFiles(download);
                  download.setDownloadError(DownloadError.GENERIC_ERROR);
                  downloadManager.setDownloadStatus(DownloadStatus.ERROR, download, task);
                }
              });
        })
        .doOnNext(__ -> downloadManager.saveDownloadInDb(download))
        .subscribeOn(Schedulers.io())
        .subscribe(__ -> {
        }, throwable -> {
          downloadManager.setDownloadStatus(DownloadStatus.ERROR, download, null);
          crashLogger.log(throwable);
        });
    download.setDownloadSpeed(task.getSpeed() * 1024);
  }

  @Override protected void error(BaseDownloadTask task, Throwable e) {
    stopDownloadQueue(download);
    if (e instanceof FileDownloadHttpException
        && ((FileDownloadHttpException) e).getCode() == FILE_NOT_FOUND_HTTP_ERROR) {
      Logger.d(TAG, "File not found on link: " + task.getUrl());
      for (final DownloadFile fileToDownload : download.getFilesToDownload()) {
        if (TextUtils.equals(fileToDownload.getLink(), task.getUrl()) && !TextUtils.isEmpty(
            fileToDownload.getAltLink())) {
          fileToDownload.setLink(fileToDownload.getAltLink());
          fileToDownload.setAltLink(null);
          downloadRepository.save(download);
          downloadManager.startDownload(download);
          return;
        }
      }
    } else {
      Logger.d(TAG, "Error on download: " + download.getHashCode());
      // Apparently throwable e can be null.
      if (e != null) {
        e.printStackTrace();
      }
      if (analytics != null) {
        analytics.onError(download, e);
      }
    }
    if (e instanceof FileDownloadOutOfSpaceException) {
      download.setDownloadError(DownloadError.NOT_ENOUGH_SPACE_ERROR);
    } else {
      download.setDownloadError(DownloadError.GENERIC_ERROR);
    }
    downloadManager.setDownloadStatus(DownloadStatus.ERROR, download, task);
    downloadManager.currentDownloadFinished();
  }

  @Override protected void warn(BaseDownloadTask task) {
    downloadManager.setDownloadStatus(DownloadStatus.WARNING, download, task);
  }

  /**
   * this method will pause all downloads listed on {@link Download} without change
   * download state, the listener is removed in order to keep the download state, this means that
   * the "virtual" pause will not affect the download state
   */
  private void stopDownloadQueue(Download download) {
    //this try catch sucks
    try {
      for (int i = download.getFilesToDownload()
          .size() - 1; i >= 0; i--) {
        DownloadFile fileToDownload = download.getFilesToDownload()
            .get(i);
        fileDownloader.getStatus(fileToDownload.getDownloadId(), fileToDownload.getPath());
        int taskId = fileDownloader.replaceListener(fileToDownload.getDownloadId(), null);
        if (taskId != 0) {
          fileDownloader.pause(taskId);
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
