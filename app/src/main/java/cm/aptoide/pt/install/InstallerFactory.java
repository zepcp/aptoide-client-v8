/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 29/09/2016.
 */

package cm.aptoide.pt.install;

import android.content.Context;
import android.support.annotation.NonNull;
import cm.aptoide.pt.AptoideApplication;
import cm.aptoide.pt.BuildConfig;
import cm.aptoide.pt.ads.MinimalAdMapper;
import cm.aptoide.pt.analytics.Analytics;
import cm.aptoide.pt.database.AccessorFactory;
import cm.aptoide.pt.database.accessors.StoredMinimalAdAccessor;
import cm.aptoide.pt.database.realm.StoredMinimalAd;
import cm.aptoide.pt.download.DownloadInstallationProvider;
import cm.aptoide.pt.downloadmanager.base.DownloadManager;
import cm.aptoide.pt.downloadmanager.external.DownloadRepository;
import cm.aptoide.pt.install.installer.DefaultInstaller;
import cm.aptoide.pt.install.installer.RollbackInstaller;
import cm.aptoide.pt.install.rollback.RollbackFactory;
import cm.aptoide.pt.preferences.toolbox.ToolboxManager;
import cm.aptoide.pt.repository.RepositoryFactory;
import cm.aptoide.pt.utils.FileUtils;

/**
 * Created by marcelobenites on 9/29/16.
 */

public class InstallerFactory {

  public static final int DEFAULT = 0;
  public static final int ROLLBACK = 1;
  private final MinimalAdMapper adMapper;
  private final InstallerAnalytics installerAnalytics;
  private final String imagesCachePath;

  public InstallerFactory(MinimalAdMapper adMapper, InstallerAnalytics installerAnalytics,
      String imagesCachePath) {
    this.adMapper = adMapper;
    this.installerAnalytics = installerAnalytics;
    this.imagesCachePath = imagesCachePath;
  }

  public Installer create(Context context, int type) {
    switch (type) {
      case DEFAULT:
        return getDefaultInstaller(context);
      case ROLLBACK:
        return getRollbackInstaller(context);
      default:
        throw new IllegalArgumentException("Installer not supported: " + type);
    }
  }

  @NonNull private DefaultInstaller getDefaultInstaller(Context context) {
    final AptoideApplication applicationContext =
        (AptoideApplication) context.getApplicationContext();

    return new DefaultInstaller(context.getPackageManager(),
        getInstallationProvider(applicationContext.getDownloadManager(),
            context.getApplicationContext()), new FileUtils(), Analytics.getInstance(),
        ToolboxManager.isDebug(applicationContext.getDefaultSharedPreferences())
            || BuildConfig.DEBUG,
        RepositoryFactory.getInstalledRepository(context.getApplicationContext()), 180000,
        applicationContext.getRootAvailabilityManager(),
        applicationContext.getDefaultSharedPreferences(), installerAnalytics,
        BuildConfig.CONTENT_AUTHORITY);
  }

  @NonNull private RollbackInstaller getRollbackInstaller(Context context) {
    return new RollbackInstaller(getDefaultInstaller(context),
        RepositoryFactory.getRollbackRepository(context.getApplicationContext()),
        new RollbackFactory(imagesCachePath), getInstallationProvider(
        ((AptoideApplication) context.getApplicationContext()).getDownloadManager(),
        context.getApplicationContext()));
  }

  @NonNull
  private DownloadInstallationProvider getInstallationProvider(DownloadManager downloadManager,
      Context context) {
    final DownloadRepository downloadRepository = RepositoryFactory.getDownloadRepository(context);

    final InstalledRepository installedRepository =
        RepositoryFactory.getInstalledRepository(context);

    final StoredMinimalAdAccessor minimalAdAccessor = AccessorFactory.getAccessorFor(
        ((AptoideApplication) context.getApplicationContext()
            .getApplicationContext()).getDatabase(), StoredMinimalAd.class);

    return new DownloadInstallationProvider(downloadManager, downloadRepository,
        installedRepository, adMapper, minimalAdAccessor);
  }
}
