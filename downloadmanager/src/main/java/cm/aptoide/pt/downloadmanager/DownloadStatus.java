package cm.aptoide.pt.downloadmanager;

public enum DownloadStatus {
  INVALID_STATUS(0), COMPLETED(1), BLOCK_COMPLETE(2), CONNECTED(3), PENDING(4), PROGRESS(5), PAUSED(
      6), WARNING(7), STARTED(8), ERROR(9), FILE_MISSING(10), RETRY(11), NOT_DOWNLOADED(
      12), IN_QUEUE(13);

  private final int value;

  DownloadStatus(int value) {
    this.value = value;
  }

  public static DownloadStatus fromValue(int status) {
    for (DownloadStatus downloadStatus : DownloadStatus.values()) {
      if (downloadStatus.getValue() == status) return downloadStatus;
    }
    return null;
  }

  public int getValue() {
    return value;
  }

}
