package cm.aptoide.pt.downloadmanager.exception;

public class DownloadErrorWrapper extends Throwable {
  private final String downloadHashCode;
  private final Throwable originalError;

  public DownloadErrorWrapper(String downloadHashCode, Throwable originalError) {
    this.downloadHashCode = downloadHashCode;
    this.originalError = originalError;
  }

  public String getDownloadHashCode() {
    return downloadHashCode;
  }

  public Throwable getOriginalError() {
    return originalError;
  }
}
