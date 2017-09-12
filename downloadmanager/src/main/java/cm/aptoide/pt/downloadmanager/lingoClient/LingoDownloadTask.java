package cm.aptoide.pt.downloadmanager.lingoClient;

import cm.aptoide.pt.downloadmanager.base.DownloadTask;
import com.liulishuo.filedownloader.BaseDownloadTask;

class LingoDownloadTask implements DownloadTask<BaseDownloadTask> {

  private final BaseDownloadTask downloadTask;

  LingoDownloadTask(BaseDownloadTask downloadTask) {
    this.downloadTask = downloadTask;
  }

  @Override public int getDownloadId() {
    return downloadTask.getId();
  }

  public BaseDownloadTask innerTask() {
    return downloadTask;
  }

  public void setAutoRetryTimes(int maxRetryAttempts) {
    downloadTask.setAutoRetryTimes(maxRetryAttempts);
  }

  public void addHeader(String key, String value) {
    downloadTask.addHeader(key, value);
  }

  public void setTag(int key, int value) {
    downloadTask.setTag(key, value);
  }

  public void setTag(int key, String value) {
    downloadTask.setTag(key, value);
  }

  public void setCallbackProgressTimes(int maxValue) {
    downloadTask.setCallbackProgressTimes(maxValue);
  }

  public void setPath(String path) {
    downloadTask.setPath(path);
  }
}
