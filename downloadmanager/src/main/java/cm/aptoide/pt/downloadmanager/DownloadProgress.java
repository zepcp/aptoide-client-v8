package cm.aptoide.pt.downloadmanager;

public class DownloadProgress {

  public static final int DOWNLOAD_HASH_CODE = 12;
  public static final int APPLICATION_FILE_INDEX = 13;
  public static final int PACKAGE_NAME = 14;
  public static final int VERSION_CODE = 15;
  private final String applicationHashCode;
  private final int fileIndex;
  private final long bytesTransferredSoFar;
  private final long totalBytesToTransfer;
  private final int speedInBytesPerSecond;
  private final DownloadStatus status;
  private final Throwable error;

  public DownloadProgress(String applicationHashCode, int fileIndex, long bytesTransferredSoFar,
      long totalBytesToTransfer, int speedInBytesPerSecond, DownloadStatus status) {
    this.applicationHashCode = applicationHashCode;
    this.fileIndex = fileIndex;
    this.bytesTransferredSoFar = bytesTransferredSoFar;
    this.totalBytesToTransfer = totalBytesToTransfer;
    this.speedInBytesPerSecond = speedInBytesPerSecond;
    this.status = status;
    this.error = null;
  }

  public DownloadProgress(String applicationHashCode, int fileIndex, DownloadStatus status) {
    this.applicationHashCode = applicationHashCode;
    this.fileIndex = fileIndex;
    this.bytesTransferredSoFar = Long.MIN_VALUE;
    this.totalBytesToTransfer = Long.MIN_VALUE;
    this.speedInBytesPerSecond = 0;
    this.status = status;
    this.error = null;
  }

  public DownloadProgress(String applicationHashCode, int fileIndex, Throwable error, DownloadStatus status) {
    this.applicationHashCode = applicationHashCode;
    this.fileIndex = fileIndex;
    this.bytesTransferredSoFar = Long.MIN_VALUE;
    this.totalBytesToTransfer = Long.MIN_VALUE;
    this.speedInBytesPerSecond = 0;
    this.status = status;
    this.error = error;
  }

  public int getSpeedInBytesPerSecond() {
    return speedInBytesPerSecond;
  }

  public int getFileIndex() {
    return fileIndex;
  }

  public boolean hasError() {
    return error != null;
  }

  public Throwable getError() {
    return error;
  }

  public String getApplicationHashCode() {
    return applicationHashCode;
  }

  public long getBytesTransferredSoFar() {
    return bytesTransferredSoFar;
  }

  public long getTotalBytesToTransfer() {
    return totalBytesToTransfer;
  }

  public DownloadStatus getStatus() {
    return status;
  }
}
