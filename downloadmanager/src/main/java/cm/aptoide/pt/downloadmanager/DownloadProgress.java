package cm.aptoide.pt.downloadmanager;

public class DownloadProgress {

  public static final int APPLICATION_FILE_INDEX = 12;
  private final String applicationHashCode;
  private final int fileIndex;
  private final long bytesTransferredSoFar;
  private final long totalBytesToTransfer;
  private final DownloadStatus status;
  private final Throwable error;

  public DownloadProgress(String applicationHashCode, int fileIndex, long bytesTransferredSoFar,
      long totalBytesToTransfer, DownloadStatus status) {
    this.applicationHashCode = applicationHashCode;
    this.fileIndex = fileIndex;
    this.bytesTransferredSoFar = bytesTransferredSoFar;
    this.totalBytesToTransfer = totalBytesToTransfer;
    this.status = status;
    this.error = null;
  }

  public DownloadProgress(String applicationHashCode, int fileIndex, DownloadStatus status) {
    this.applicationHashCode = applicationHashCode;
    this.fileIndex = fileIndex;
    this.bytesTransferredSoFar = Long.MIN_VALUE;
    this.totalBytesToTransfer = Long.MIN_VALUE;
    this.status = status;
    this.error = null;
  }

  public DownloadProgress(String applicationHashCode, int fileIndex, Throwable error, DownloadStatus status) {
    this.applicationHashCode = applicationHashCode;
    this.fileIndex = fileIndex;
    this.bytesTransferredSoFar = Long.MIN_VALUE;
    this.totalBytesToTransfer = Long.MIN_VALUE;
    this.status = status;
    this.error = error;
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
