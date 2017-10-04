package cm.aptoide.pt.install;

import android.content.Context;
import cm.aptoide.pt.downloadmanager.base.DownloadRequest;
import cm.aptoide.pt.install.installer.InstallationState;
import rx.Completable;
import rx.Observable;

/**
 * Created by trinkes on 9/8/16.
 */
public interface Installer {

  Completable install(Context context, DownloadRequest request, boolean forceDefaultInstall);

  Completable update(Context context, DownloadRequest request, boolean forceDefaultInstall);

  Completable downgrade(Context context, DownloadRequest request, boolean forceDefaultInstall);

  Completable uninstall(Context context, String packageName, String versionName);

  Observable<InstallationState> getState(String packageName, int versionCode);
}
