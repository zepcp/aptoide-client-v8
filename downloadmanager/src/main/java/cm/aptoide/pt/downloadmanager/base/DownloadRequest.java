package cm.aptoide.pt.downloadmanager.base;

import android.support.annotation.NonNull;
import cm.aptoide.pt.downloadmanager.DownloadAction;
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
