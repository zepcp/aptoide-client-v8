/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 25/07/2016.
 */

package cm.aptoide.pt.download;

import android.support.annotation.NonNull;
import cm.aptoide.pt.ads.MinimalAdMapper;
import cm.aptoide.pt.database.accessors.StoredMinimalAdAccessor;
import cm.aptoide.pt.database.realm.Installed;
import cm.aptoide.pt.database.realm.StoredMinimalAd;
import cm.aptoide.pt.dataprovider.ads.AdNetworkUtils;
import cm.aptoide.pt.downloadmanager.DownloadStatus;
import cm.aptoide.pt.downloadmanager.base.Download;
import cm.aptoide.pt.downloadmanager.base.DownloadManager;
import cm.aptoide.pt.downloadmanager.base.DownloadRequest;
import cm.aptoide.pt.downloadmanager.external.DownloadRepository;
import cm.aptoide.pt.install.InstalledRepository;
import cm.aptoide.pt.install.exception.InstallationException;
import cm.aptoide.pt.install.installer.InstallationProvider;
import cm.aptoide.pt.install.installer.RollbackInstallation;
import cm.aptoide.pt.install.rollback.DownloadInstallationAdapter;
import java.util.concurrent.TimeUnit;
import rx.Observable;
import rx.functions.Action1;

/**
 * Created by marcelobenites on 7/25/16.
 */
public class DownloadInstallationProvider implements InstallationProvider {

  private final DownloadManager downloadManager;
  private final DownloadRepository downloadRepository;
  private final MinimalAdMapper adMapper;
  private final InstalledRepository installedRepository;
  private final StoredMinimalAdAccessor storedMinimalAdAccessor;

  public DownloadInstallationProvider(DownloadManager downloadManager,
      DownloadRepository downloadRepository, InstalledRepository installedRepository,
      MinimalAdMapper adMapper, StoredMinimalAdAccessor storeMinimalAdAccessor) {
    this.downloadManager = downloadManager;
    this.downloadRepository = downloadRepository;
    this.adMapper = adMapper;
    this.storedMinimalAdAccessor = storeMinimalAdAccessor;
    this.installedRepository = installedRepository;
  }

  @Override public Observable<RollbackInstallation> install(DownloadRequest request) {
    downloadManager.startDownload(request);
    return downloadManager.observeDownloadChanges(request)
        .timeout(2, TimeUnit.SECONDS,
            Observable.error(new InstallationException("Installation file not available.")))
        .filter(download -> download.getOverallDownloadStatus() == DownloadStatus.COMPLETED)
        .flatMap(download -> installedRepository.get(download.getPackageName(),
            download.getVersionCode())
            .map(installed -> {
              if (installed == null) {
                installed = convertDownloadToInstalled(download);
              }

              return new DownloadInstallationAdapter(download, downloadRepository,
                  installedRepository, installed);
            })
            .flatMap(adapter -> storedMinimalAdAccessor.get(adapter.getPackageName())
                .doOnNext(handleCpd())
                .map(__ -> adapter)));

    /*
    return downloadManager.getDownload(md5)
        .first()
        .flatMap(download -> {
          if (download.getOverallDownloadStatus() == DownloadStatus.COMPLETED) {
            return installedRepository.get(download.getPackageName(), download.getVersionCode())
                .map(installed -> {
                  if (installed == null) {
                    installed = convertDownloadToInstalled(download);
                  }
                  return new DownloadInstallationAdapter(download, downloadRepository,
                      installedRepository, installed);
                })
                .doOnNext(downloadInstallationAdapter -> {
                  storedMinimalAdAccessor.get(downloadInstallationAdapter.getPackageName())
                      .doOnNext(handleCpd())
                      .subscribeOn(Schedulers.io())
                      .subscribe(storedMinimalAd -> {
                      }, Throwable::printStackTrace);
                });
          }
          return Observable.error(new InstallationException("Installation file not available."));
        });
    */
  }

  @Override public Observable<Boolean> isInstalled(DownloadRequest request) {
    return downloadManager.observeDownloadChanges(request)
        .filter(download -> download.getOverallDownloadStatus() == DownloadStatus.COMPLETED)
        .first()
        .map(download -> installedRepository.contains(download.getPackageName()));
  }

  @NonNull private Installed convertDownloadToInstalled(Download download) {
    Installed installed = new Installed();
    installed.setPackageAndVersionCode(download.getPackageName() + download.getVersionCode());
    installed.setVersionCode(download.getVersionCode());
    installed.setVersionName(download.getVersionName());
    installed.setStatus(Installed.STATUS_UNINSTALLED);
    installed.setType(Installed.TYPE_UNKNOWN);
    installed.setPackageName(download.getPackageName());
    return installed;
  }

  @NonNull private Action1<StoredMinimalAd> handleCpd() {
    return storedMinimalAd -> {
      if (storedMinimalAd != null && storedMinimalAd.getCpdUrl() != null) {
        AdNetworkUtils.knockCpd(adMapper.map(storedMinimalAd));
        storedMinimalAd.setCpdUrl(null);
        storedMinimalAdAccessor.insert(storedMinimalAd);
      }
    };
  }
}
