package cm.aptoide.pt.downloadmanager;

import android.support.annotation.NonNull;
import android.support.v4.util.Pair;
import com.liulishuo.filedownloader.BaseDownloadTask;
import com.liulishuo.filedownloader.FileDownloadListener;
import com.liulishuo.filedownloader.FileDownloader;
import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import rx.subjects.BehaviorSubject;

public class DownloadOrchestrator {

  private static final String VERSION_CODE = "versioncode";
  private static final String PACKAGE = "package";
  private static final String FILE_TYPE = "fileType";
  private static final int PROGRESS_MAX_VALUE = 100;

  // private static final short RETRY_TIMES = 3;
  // private static final int APTOIDE_DOWNLOAD_TASK_TAG_KEY = 888;

  private final short maxRetryTimes;
  private final Map<DownloadRequest, Pair<Download, DownloadStatusListener>> downloadsMap;
  private final FileDownloader fileDownloader;
  private final FilePaths filePaths;
  private final FileSystemOperations fsOperations;
  private final Analytics analytics;

  public DownloadOrchestrator(short maxRetryTimes, FileDownloader fileDownloader,
      FilePaths filePaths, FileSystemOperations fsOperations, Analytics analytics) {
    this.maxRetryTimes = maxRetryTimes;
    this.fileDownloader = fileDownloader;
    this.filePaths = filePaths;
    this.fsOperations = fsOperations;
    this.analytics = analytics;
    downloadsMap = new HashMap<>();
  }

  public void start(DownloadRequest downloadRequest, Download download,
      BehaviorSubject<DownloadProgress> currentDownloadsSubject) {

    final DownloadStatusListener listener =
        new DownloadStatusListener(downloadRequest.getHashCode(), downloadRequest.getPackageName(),
            downloadRequest.getVersionCode(), analytics, currentDownloadsSubject);

    downloadsMap.put(downloadRequest, Pair.create(download, listener));

    startInternal(downloadRequest.getFilesToDownload(), downloadRequest.getVersionCode(),
        downloadRequest.getPackageName(), listener);
  }

  private void startInternal(List<DownloadFile> filesToDownload, int versionCode,
      String packageName, FileDownloadListener listener) {

    int fileIndex = 0;
    for (DownloadFile downloadFile : filesToDownload) {
      final String fileDownloadLink = getFileDownloadLink(downloadFile);
      final String downloadPath = getDownloadPath(downloadFile);
      BaseDownloadTask baseDownloadTask =
          getDownloadTask(fileDownloadLink, versionCode, packageName, fileIndex, listener,
              downloadPath);
      downloadFile.setDownloadId(baseDownloadTask.asInQueueTask()
          .enqueue());
      downloadFile.setPath(downloadPath);
      downloadFile.setFileName(downloadFile.getFileName());
      fileIndex++;
    }

    fileDownloader.start(listener, true);
  }

  @NonNull private String getDownloadPath(DownloadFile downloadFile) {
    switch (downloadFile.getFileType()) {
      default:
      case GENERIC:
        return filePaths.getDownloadsStoragePath() + downloadFile.getFileName();

      case APK:
        return filePaths.getApkPath() + downloadFile.getFileName();

      case OBB:
        return filePaths.getObbPath() + downloadFile.getFileName();
    }
  }

  private String getFileDownloadLink(DownloadFile fileToDownload) {
    String fileDownloadLink = fileToDownload.getLink();
    if (fileDownloadLink == null || fileDownloadLink.trim()
        .isEmpty()) {
      throw new IllegalArgumentException("A link to download must be provided");
    }
    final String fileName = fileToDownload.getFileName();
    if (fileName.endsWith(".temp")) {
      fileToDownload.setFileName(fileName.replace(".temp", ""));
    }
    return fileDownloadLink;
  }

  private BaseDownloadTask getDownloadTask(String fileDownloadLink, int versionCode,
      String packageName, int fileIndex, FileDownloadListener listener, String downloadPath) {
    BaseDownloadTask baseDownloadTask = fileDownloader.create(fileDownloadLink);
    baseDownloadTask.setAutoRetryTimes(maxRetryTimes);
    // Aptoide - events 2 : download
    // Get X-Mirror and add to the event
    baseDownloadTask.addHeader(VERSION_CODE, Integer.toString(versionCode));
    baseDownloadTask.addHeader(PACKAGE, packageName);
    baseDownloadTask.addHeader(FILE_TYPE, Integer.toString(fileIndex));
    // end
    baseDownloadTask.setTag(DownloadProgress.APPLICATION_FILE_INDEX, fileIndex);
    baseDownloadTask.setListener(listener);
    baseDownloadTask.setCallbackProgressTimes(PROGRESS_MAX_VALUE);
    baseDownloadTask.setPath(downloadPath);
    return baseDownloadTask;
  }

  public void pause(DownloadRequest downloadRequest) {
    final Pair<Download, DownloadStatusListener> pair = downloadsMap.get(downloadRequest);
    if (pair == null) {
      throw new IllegalArgumentException(
          String.format("Illegal %s passed", DownloadRequest.class.getSimpleName()));
    }
    fileDownloader.pause(pair.second);
  }

  public void removeAll() {
    fileDownloader.clearAllTaskData();

    for (Pair<Download, DownloadStatusListener> pair : downloadsMap.values()) {
      for (DownloadFile file : pair.first.getFilesToDownload()) {
        fsOperations.deleteFile(file.getFilePath() + File.pathSeparator + file.getFileName());
      }
    }

    downloadsMap.clear();
  }

  public void pauseAll() {
    fileDownloader.pauseAll();
  }

  public void remove(DownloadRequest downloadRequest) {
    final Pair<Download, DownloadStatusListener> pair = downloadsMap.get(downloadRequest);
    if (pair == null) {
      throw new IllegalArgumentException(
          String.format("Illegal %s passed", DownloadRequest.class.getSimpleName()));
    }
    fileDownloader.pause(pair.second);
  }
}
