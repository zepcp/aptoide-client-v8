package cm.aptoide.pt.downloadmanager;

import android.support.annotation.NonNull;
import java.util.List;
import rx.Observable;
import rx.subjects.BehaviorSubject;

public class SynchronousDownloadManager implements DownloadManager {

  private final BehaviorSubject<DownloadProgress> currentDownloadsSubject;
  private final DownloadOrchestrator downloadOrchestrator;
  private final DownloadRepository downloadRepository;

  public SynchronousDownloadManager(DownloadOrchestrator downloadOrchestrator,
      DownloadRepository downloadRepository) {
    this.downloadOrchestrator = downloadOrchestrator;
    this.downloadRepository = downloadRepository;
    currentDownloadsSubject = BehaviorSubject.create();
  }

  @NonNull @Override
  public Observable<Download> observeDownloadChanges(DownloadRequest downloadRequest) {
    final String downloadHash = downloadRequest.getHashCode();
    if (downloadHash.trim()
        .isEmpty()) {
      throw new IllegalArgumentException(
          String.format("%s has an invalid hash code", DownloadRequest.class.getSimpleName()));
    }
    return currentDownloadsSubject.asObservable()
        .filter(download1 -> downloadHash.equals(download1.getApplicationHashCode()))
        .flatMap(downloadProgress -> getDownloadFromProgress(downloadProgress));
  }

  @Override public Observable<Download> observeAllDownloadChanges() {
    return currentDownloadsSubject.asObservable()
        .flatMap(downloadProgress -> getDownloadFromProgress(downloadProgress));
  }

  @Override public void startDownload(DownloadRequest downloadRequest) {
    validate(downloadRequest);
    final Download download = downloadRepository.insertNew(downloadRequest.getHashCode(),
        downloadRequest.getApplicationName(), downloadRequest.getApplicationIcon(),
        downloadRequest.getDownloadAction()
            .getValue(), downloadRequest.getPackageName(), downloadRequest.getVersionCode(),
        downloadRequest.getVersionName(), downloadRequest.getFilesToDownload());
    downloadOrchestrator.start(downloadRequest, download, currentDownloadsSubject);
  }

  @Override public void removeDownload(DownloadRequest downloadRequest) {
    downloadOrchestrator.remove(downloadRequest);
    downloadRepository.delete(downloadRequest.getHashCode());
  }

  @Override public void pauseDownload(DownloadRequest downloadRequest) {
    downloadOrchestrator.pause(downloadRequest);
  }

  @Override public void removeAllDownloads() {
    downloadOrchestrator.removeAll();
    downloadRepository.deleteAll();
  }

  @Override public void pauseAllDownloads() {
    downloadOrchestrator.pauseAll();
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

  private Observable<Download> getDownloadFromProgress(DownloadProgress downloadProgress) {
    return Observable.just(downloadProgress)
        .flatMap(progress -> {
          if (progress.hasError()) {
            return Observable.error(
                new DownloadErrorWrapper(downloadProgress.getApplicationHashCode(),
                    progress.getError()));
          }
          return downloadRepository.get(downloadProgress.getApplicationHashCode())
              .doOnNext(download -> calculateProgress(download, downloadProgress))
              .doOnNext(download -> download.setDownloadSpeed(
                  downloadProgress.getSpeedInBytesPerSecond() * 1024));
        })
        .doOnNext(download -> updateDownloadInDatabase(download));
  }

  private void updateDownloadInDatabase(Download download) {
    downloadRepository.save(download);
  }

  private void calculateProgress(Download download, DownloadProgress downloadProgress) {

    int progressPercentage = getProgressPercentage(downloadProgress.getTotalBytesToTransfer(),
        downloadProgress.getBytesTransferredSoFar());

    final List<DownloadFile> filesToDownload = download.getFilesToDownload();
    final int applicationFileCount = filesToDownload.size();
    if (applicationFileCount > 1) {
      DownloadFile downloadFile = filesToDownload.get(downloadProgress.getFileIndex());
      downloadFile.setProgress(progressPercentage);
      // update the whole application progress based on each file progress
      int fileProgressSum = 0;
      for (DownloadFile f : filesToDownload) {
        fileProgressSum += f.getProgress();
      }
      download.setOverallProgress(fileProgressSum / applicationFileCount);
    } else {
      download.setOverallProgress(progressPercentage);
    }
  }

  private int getProgressPercentage(long totalBytesToTransfer, long bytesTransferredSoFar) {
    // totalBytesToTransfer -> 100%
    // bytesTransferredSoFar -> X
    // X = ((bytesTransferredSoFar * 100) / totalBytesToTransfer
    int totalKbToTransfer = (int) (totalBytesToTransfer / 1024L);
    int KbTransferredSoFar = (int) (bytesTransferredSoFar / 1024L);
    return ((KbTransferredSoFar * 100) / totalKbToTransfer);
  }
}
