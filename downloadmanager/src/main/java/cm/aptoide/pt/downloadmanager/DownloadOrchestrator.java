package cm.aptoide.pt.downloadmanager;

import android.support.annotation.NonNull;
import cm.aptoide.pt.downloadmanager.base.DownloadClient;
import cm.aptoide.pt.downloadmanager.base.DownloadFactory;
import cm.aptoide.pt.downloadmanager.base.DownloadFile;
import cm.aptoide.pt.downloadmanager.base.DownloadRequest;
import cm.aptoide.pt.downloadmanager.base.DownloadTask;
import cm.aptoide.pt.downloadmanager.base.FileDownloadQueue;
import cm.aptoide.pt.downloadmanager.external.FilePaths;
import cm.aptoide.pt.downloadmanager.external.FileSystemOperations;
import java.io.File;
import java.util.List;
import java.util.Map;

public class DownloadOrchestrator {

  private final FileDownloadQueue downloadQueue;
  private final Map<DownloadRequest, List<DownloadFile>> downloadsMap;
  private final DownloadFactory downloadFactory;
  private final DownloadClient downloadClient;
  private final FilePaths filePaths;
  private final FileSystemOperations fsOperations;

  public DownloadOrchestrator(DownloadClient downloadClient, FilePaths filePaths,
      FileSystemOperations fsOperations, FileDownloadQueue downloadQueue,
      Map<DownloadRequest, List<DownloadFile>> downloadsMap, DownloadFactory downloadFactory) {
    this.downloadClient = downloadClient;
    this.filePaths = filePaths;
    this.fsOperations = fsOperations;
    this.downloadQueue = downloadQueue;
    this.downloadsMap = downloadsMap;
    this.downloadFactory = downloadFactory;
  }

  public void startAndUpdateDownloadFileIds(DownloadRequest downloadRequest) {
    int fileIndex = 0;

    for (DownloadFile downloadFile : downloadRequest.getFilesToDownload()) {
      final String fileDownloadLink = getFileDownloadLink(downloadFile);
      final String downloadPath = getDownloadPath(downloadFile);

      DownloadTask downloadTask =
          downloadFactory.create(fileDownloadLink, downloadRequest.getVersionCode(),
              downloadRequest.getPackageName(), fileIndex, downloadPath,
              downloadRequest.getHashCode());

      downloadFile.setPath(downloadPath);
      downloadFile.setFileName(downloadFile.getFileName());
      downloadFile.setDownloadId(downloadTask.getDownloadId());
      fileIndex++;
      downloadQueue.enqueue(downloadTask);
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

  public void pause(DownloadRequest downloadRequest) {
    List<DownloadFile> downloadTaskIds = downloadsMap.get(downloadRequest);
    if (downloadTaskIds == null || downloadTaskIds.isEmpty()) {
      throw new IllegalArgumentException(
          String.format("Illegal %s passed", DownloadRequest.class.getSimpleName()));
    }
    for (DownloadFile downloadFile : downloadTaskIds) {
      downloadClient.pauseDownload(downloadFile.getDownloadId());
    }
  }

  public void removeAll() {
    downloadClient.clearAllDownloads();

    for (List<DownloadFile> fileList : downloadsMap.values()) {
      for (DownloadFile file : fileList) {
        fsOperations.deleteFile(file.getFilePath() + File.pathSeparator + file.getFileName());
      }
    }

    downloadsMap.clear();
  }

  public void pauseAll() {
    downloadClient.pauseAllDownloads();
  }

  public void remove(DownloadRequest downloadRequest) {
    List<DownloadFile> downloadFiles = downloadsMap.get(downloadRequest);
    if (downloadFiles == null || downloadFiles.isEmpty()) {
      throw new IllegalArgumentException(
          String.format("Illegal %s passed", DownloadRequest.class.getSimpleName()));
    }

    for (DownloadFile downloadFile : downloadFiles) {
      downloadClient.pauseDownload(downloadFile.getDownloadId());
    }
  }
}
