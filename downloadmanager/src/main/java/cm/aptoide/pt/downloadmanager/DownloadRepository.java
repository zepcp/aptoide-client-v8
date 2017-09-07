package cm.aptoide.pt.downloadmanager;

import android.support.annotation.NonNull;
import java.util.Collection;
import java.util.List;
import rx.Observable;

public interface DownloadRepository {
  @NonNull Observable<? extends List<Download>> getAll();

  @NonNull Observable<Download> get(@NonNull String hashCode);

  void delete(@NonNull String hashCode);

  void save(@NonNull Download download);

  <T extends Collection<Download>> void save(@NonNull T download);

  @NonNull Observable<? extends List<Download>> getCurrentDownloads();

  @NonNull <T extends List<DownloadFile>> Download insertNew(String hashCode, String appName,
      String icon, int action, String packageName, int versionCode, String versionName,
      T downloadFiles);

  void deleteAll();
}
