package cm.aptoide.pt.downloadmanager;

public enum DownloadError {
  NO_ERROR(0), GENERIC_ERROR(1), NOT_ENOUGH_SPACE_ERROR(2);

  private final int value;

  DownloadError(int value) {
    this.value = value;
  }

  public int getValue() {
    return value;
  }

  public static DownloadError fromValue(int errorState) {
    for (DownloadError downloadAction : DownloadError.values()) {
      if (downloadAction.getValue() == errorState) return downloadAction;
    }
    return null;
  }
}
