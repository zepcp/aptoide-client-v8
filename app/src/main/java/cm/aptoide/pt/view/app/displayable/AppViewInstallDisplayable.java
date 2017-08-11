/*
 * Copyright (c) 2016.
 * Modified on 25/08/2016.
 */

package cm.aptoide.pt.view.app.displayable;

import android.widget.Button;
import cm.aptoide.pt.Install;
import cm.aptoide.pt.InstallManager;
import cm.aptoide.pt.R;
import cm.aptoide.pt.analytics.DownloadCompleteAnalytics;
import cm.aptoide.pt.app.AppViewAnalytics;
import cm.aptoide.pt.database.realm.MinimalAd;
import cm.aptoide.pt.dataprovider.model.v7.GetApp;
import cm.aptoide.pt.dataprovider.model.v7.GetAppMeta;
import cm.aptoide.pt.download.DownloadFactory;
import cm.aptoide.pt.timeline.TimelineAnalytics;
import cm.aptoide.pt.view.app.AppViewFragment;
import rx.Observable;
import rx.subjects.PublishSubject;

/**
 * Created on 06/05/16.
 */
public class AppViewInstallDisplayable extends AppViewDisplayable {

  private final Observable<Void> installAppRelay;
  private boolean shouldInstall;
  private MinimalAd minimalAd;
  private AppViewFragment appViewFragment;
  private int versionCode;
  private InstallManager installManager;
  private String md5;
  private String packageName;
  private Button installButton;
  private DownloadFactory downloadFactory;
  private TimelineAnalytics timelineAnalytics;
  private DownloadCompleteAnalytics analytics;

  public AppViewInstallDisplayable() {
    super();
    installAppRelay = PublishSubject.empty();
  }

  public AppViewInstallDisplayable(InstallManager installManager, GetApp getApp,
      MinimalAd minimalAd, boolean shouldInstall, TimelineAnalytics timelineAnalytics,
      AppViewAnalytics appViewAnalytics, PublishSubject installAppRelay,
      DownloadFactory downloadFactory, AppViewFragment appViewFragment,
      DownloadCompleteAnalytics analytics) {
    super(getApp, appViewAnalytics);
    this.installManager = installManager;
    this.md5 = getApp.getNodes()
        .getMeta()
        .getData()
        .getFile()
        .getMd5sum();
    this.packageName = getApp.getNodes()
        .getMeta()
        .getData()
        .getPackageName();
    this.versionCode = getApp.getNodes()
        .getMeta()
        .getData()
        .getFile()
        .getVercode();
    this.minimalAd = minimalAd;
    this.shouldInstall = shouldInstall;
    this.downloadFactory = downloadFactory;
    this.installAppRelay = installAppRelay;
    this.timelineAnalytics = timelineAnalytics;
    this.appViewFragment = appViewFragment;
    this.analytics = analytics;
  }

  public static AppViewInstallDisplayable newInstance(GetApp getApp, InstallManager installManager,
      MinimalAd minimalAd, boolean shouldInstall, DownloadFactory downloadFactory,
      TimelineAnalytics timelineAnalytics, AppViewAnalytics appViewAnalytics,
      PublishSubject installAppRelay, AppViewFragment appViewFragment,
      DownloadCompleteAnalytics analytics) {
    return new AppViewInstallDisplayable(installManager, getApp, minimalAd, shouldInstall,
        timelineAnalytics, appViewAnalytics, installAppRelay, downloadFactory, appViewFragment,
        analytics);
  }

  public boolean isShouldInstall() {
    return shouldInstall;
  }

  public void setShouldInstall(boolean shouldInstall) {
    this.shouldInstall = shouldInstall;
  }

  public MinimalAd getMinimalAd() {
    return minimalAd;
  }

  public AppViewFragment getAppViewFragment() {
    return appViewFragment;
  }

  public void startInstallationProcess() {
    if (installButton != null) {
      installButton.performClick();
    }
  }

  public void setInstallButton(Button installButton) {
    this.installButton = installButton;
  }

  @Override protected Configs getConfig() {
    return new Configs(1, true);
  }

  @Override public int getViewLayout() {
    return R.layout.displayable_app_view_install;
  }

  public Observable<Install> getInstallState() {
    return installManager.getInstall(md5, packageName, versionCode);
  }

  public DownloadFactory getDownloadFactory() {
    return downloadFactory;
  }

  public TimelineAnalytics getTimelineAnalytics() {
    return timelineAnalytics;
  }

  public Observable<Void> getInstallAppRelay() {
    return installAppRelay;
  }

  public void installAppClicked() {
    GetAppMeta.App app = getPojo().getNodes()
        .getMeta()
        .getData();
    analytics.installClicked(app.getMd5(), app.getPackageName(), app.getFile()
        .getMalware()
        .getRank()
        .name());
  }
}
