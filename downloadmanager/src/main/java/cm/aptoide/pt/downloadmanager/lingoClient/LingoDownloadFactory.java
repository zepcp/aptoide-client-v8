package cm.aptoide.pt.downloadmanager.lingoClient;

import cm.aptoide.pt.downloadmanager.DownloadProgress;
import cm.aptoide.pt.downloadmanager.base.DownloadFactory;
import cm.aptoide.pt.downloadmanager.base.DownloadTask;

public class LingoDownloadFactory implements DownloadFactory {

  private final LingoDownloadClient downloadClient;

  public LingoDownloadFactory(LingoDownloadClient downloadClient) {
    this.downloadClient = downloadClient;
  }

  @Override
  public LingoDownloadTask create(String fileDownloadLink, int versionCode, String packageName,
      int fileIndex, String downloadPath, String hashCode) {
    LingoDownloadTask baseDownloadTask = downloadClient.startDownload(fileDownloadLink);
    baseDownloadTask.setAutoRetryTimes(DownloadTask.MAX_RETRY_ATTEMPTS);
    // Aptoide - events 2 : download
    // Get X-Mirror and add to the event
    baseDownloadTask.addHeader(DownloadTask.VERSION_CODE, Integer.toString(versionCode));
    baseDownloadTask.addHeader(DownloadTask.PACKAGE, packageName);
    baseDownloadTask.addHeader(DownloadTask.FILE_TYPE, Integer.toString(fileIndex));
    // end
    baseDownloadTask.setTag(DownloadProgress.APPLICATION_FILE_INDEX, fileIndex);
    baseDownloadTask.setTag(DownloadProgress.DOWNLOAD_HASH_CODE, hashCode);
    baseDownloadTask.setTag(DownloadProgress.VERSION_CODE, versionCode);
    baseDownloadTask.setTag(DownloadProgress.PACKAGE_NAME, packageName);
    baseDownloadTask.setCallbackProgressTimes(DownloadTask.PROGRESS_MAX_VALUE);
    baseDownloadTask.setPath(downloadPath);
    return baseDownloadTask;
  }
}
