/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 25/07/2016.
 */

package cm.aptoide.pt.install.rollback;

import cm.aptoide.pt.database.realm.Installed;
import cm.aptoide.pt.downloadmanager.Download;
import cm.aptoide.pt.downloadmanager.DownloadFile;
import cm.aptoide.pt.downloadmanager.DownloadRepository;
import cm.aptoide.pt.install.InstalledRepository;
import cm.aptoide.pt.install.installer.RollbackInstallation;
import java.io.File;
import java.util.List;

/**
 * Created by marcelobenites on 7/22/16.
 */
public class DownloadInstallationAdapter implements RollbackInstallation {

  private final Download download;
  private DownloadRepository downloadRepository;
  private InstalledRepository ongoingInstallProvider;
  private Installed installed;

  public DownloadInstallationAdapter(Download download, DownloadRepository downloadRepository,
      InstalledRepository installedRepository, Installed installed) {
    this.download = download;
    this.downloadRepository = downloadRepository;
    this.ongoingInstallProvider = installedRepository;
    this.installed = installed;
  }

  @Override public String getId() {
    return download.getHashCode();
  }

  @Override public String getPackageName() {
    return download.getFilesToDownload()
        .get(0)
        .getPackageName();
  }

  @Override public int getVersionCode() {
    return download.getFilesToDownload()
        .get(0)
        .getVersionCode();
  }

  @Override public String getVersionName() {
    return download.getVersionName();
  }

  @Override public File getFile() {
    return new File(download.getFilesToDownload()
        .get(0)
        .getFilePath());
  }

  @Override public void save() {
    ongoingInstallProvider.save(installed);
  }

  @Override public int getStatus() {
    return installed.getStatus();
  }

  @Override public void setStatus(int status) {
    installed.setStatus(status);
  }

  @Override public int getType() {
    return installed.getType();
  }

  @Override public void setType(int type) {
    installed.setType(type);
  }

  @Override public String getAppName() {
    return download.getAppName();
  }

  @Override public String getIcon() {
    return download.getIcon();
  }

  @Override public List<DownloadFile> getFiles() {
    return download.getFilesToDownload();
  }

  @Override public void saveFileChanges() {
    downloadRepository.save(download);
  }
}
