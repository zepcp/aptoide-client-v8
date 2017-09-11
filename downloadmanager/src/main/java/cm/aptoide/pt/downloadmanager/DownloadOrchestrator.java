package cm.aptoide.pt.downloadmanager;

import android.support.annotation.NonNull;
import com.liulishuo.filedownloader.BaseDownloadTask;
import com.liulishuo.filedownloader.FileDownloadQueueSet;
import com.liulishuo.filedownloader.FileDownloader;
import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class DownloadOrchestrator {

  private static final String VERSION_CODE = "versioncode";
  private static final String PACKAGE = "package";
  private static final String FILE_TYPE = "fileType";
  private static final int PROGRESS_MAX_VALUE = 100;

  // private static final short RETRY_TIMES = 3;
  // private static final int APTOIDE_DOWNLOAD_TASK_TAG_KEY = 888;

  private final int maxRetryTimes;

  private final FileDownloadQueueSet downloadQueue;
  private final Map<DownloadRequest, List<DownloadFile>> downloadsMap;

  private final FileDownloader fileDownloader;
  private final FilePaths filePaths;
  private final FileSystemOperations fsOperations;

  public DownloadOrchestrator(int maxRetryTimes, FileDownloader fileDownloader, FilePaths filePaths,
      FileSystemOperations fsOperations, FileDownloadQueueSet downloadQueue) {
    this.maxRetryTimes = maxRetryTimes;
    this.fileDownloader = fileDownloader;
    this.filePaths = filePaths;
    this.fsOperations = fsOperations;
    this.downloadQueue = downloadQueue;
    downloadsMap = new ConcurrentHashMap<>();
  }

  public void startAndUpdateDownloadFileIds(DownloadRequest downloadRequest) {
    int fileIndex = 0;

    for (DownloadFile downloadFile : downloadRequest.getFilesToDownload()) {
      final String fileDownloadLink = getFileDownloadLink(downloadFile);
      final String downloadPath = getDownloadPath(downloadFile);
      BaseDownloadTask downloadTask =
          getDownloadTask(fileDownloadLink, downloadRequest.getVersionCode(),
              downloadRequest.getPackageName(), fileIndex, downloadPath,
              downloadRequest.getHashCode());

      downloadFile.setPath(downloadPath);
      downloadFile.setFileName(downloadFile.getFileName());
      downloadFile.setDownloadId(downloadTask.getId());
      fileIndex++;
      downloadQueue.downloadSequentially(downloadTask);
    }
    downloadsMap.put(downloadRequest, downloadRequest.getFilesToDownload());
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
      String packageName, int fileIndex, String downloadPath, String downloadHashCode) {
    BaseDownloadTask baseDownloadTask = fileDownloader.create(fileDownloadLink);
    baseDownloadTask.setAutoRetryTimes(maxRetryTimes);
    // Aptoide - events 2 : download
    // Get X-Mirror and add to the event
    baseDownloadTask.addHeader(VERSION_CODE, Integer.toString(versionCode));
    baseDownloadTask.addHeader(PACKAGE, packageName);
    baseDownloadTask.addHeader(FILE_TYPE, Integer.toString(fileIndex));
    // end
    baseDownloadTask.setTag(DownloadProgress.APPLICATION_FILE_INDEX, fileIndex);
    baseDownloadTask.setTag(DownloadProgress.DOWNLOAD_HASH_CODE, downloadHashCode);
    baseDownloadTask.setTag(DownloadProgress.VERSION_CODE, versionCode);
    baseDownloadTask.setTag(DownloadProgress.PACKAGE_NAME, packageName);
    baseDownloadTask.setCallbackProgressTimes(PROGRESS_MAX_VALUE);
    baseDownloadTask.setPath(downloadPath);
    return baseDownloadTask;
  }

  public void pause(DownloadRequest downloadRequest) {
    List<DownloadFile> downloadTaskIds = downloadsMap.get(downloadRequest);
    if (downloadTaskIds == null || downloadTaskIds.isEmpty()) {
      throw new IllegalArgumentException(
          String.format("Illegal %s passed", DownloadRequest.class.getSimpleName()));
    }
    for (DownloadFile downloadFile : downloadTaskIds) {
      fileDownloader.pause(downloadFile.getDownloadId());
    }
  }

  public void removeAll() {
    fileDownloader.clearAllTaskData();

    for (List<DownloadFile> fileList : downloadsMap.values()) {
      for (DownloadFile file : fileList) {
        fsOperations.deleteFile(file.getFilePath() + File.pathSeparator + file.getFileName());
      }
    }

    downloadsMap.clear();
  }

  public void pauseAll() {
    fileDownloader.pauseAll();
  }

  public void remove(DownloadRequest downloadRequest) {
    List<DownloadFile> downloadFiles = downloadsMap.get(downloadRequest);
    if (downloadFiles == null || downloadFiles.isEmpty()) {
      throw new IllegalArgumentException(
          String.format("Illegal %s passed", DownloadRequest.class.getSimpleName()));
    }

    for (DownloadFile downloadFile : downloadFiles) {
      fileDownloader.pause(downloadFile.getDownloadId());
    }
  }
}
