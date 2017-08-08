package cm.aptoide.pt.downloadmanager;

public enum DownloadFileType {
  APK(0), OBB(1), GENERIC(2);

  private final int value;

  DownloadFileType(int value) {
    this.value = value;
  }

  public static DownloadFileType fromValue(int type) {
    for (DownloadFileType downloadFileType : DownloadFileType.values()) {
      if (downloadFileType.getValue() == type) return downloadFileType;
    }
    return null;
  }

  public int getValue() {
    return value;
  }
}
