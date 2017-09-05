package cm.aptoide.pt.downloadmanager;

public enum DownloadAction {
  INSTALL(0), UPDATE(1), DOWNGRADE(2), NOTHING(3);

  private final int value;

  DownloadAction(int value) {
    this.value = value;
  }

  public static DownloadAction fromValue(int action) {
    for (DownloadAction downloadAction : DownloadAction.values()) {
      if (downloadAction.getValue() == action) return downloadAction;
    }
    return NOTHING;
  }

  public int getValue() {
    return value;
  }
}
