package cm.aptoide.pt.downloadmanager;

import android.support.annotation.NonNull;
import java.util.List;

public interface DownloadRequest {
  @NonNull String getHashCode();

  @NonNull String getPackageName();

  @NonNull List<DownloadFile> getFilesToDownload();

  @NonNull String getApplicationName();

  @NonNull DownloadAction getDownloadAction();

  @NonNull String getApplicationIcon();

  int getVersionCode();

  @NonNull String getVersionName();
}
