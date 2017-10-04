/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 29/09/2016.
 */

package cm.aptoide.pt.install.installer;

import cm.aptoide.pt.downloadmanager.base.DownloadRequest;
import rx.Observable;

/**
 * Created by marcelobenites on 7/25/16.
 */
public interface InstallationProvider {

  Observable<RollbackInstallation> install(DownloadRequest downloadRequest);

  Observable<Boolean> isInstalled(DownloadRequest request);
}
