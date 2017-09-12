package cm.aptoide.pt.filemanager;

import cm.aptoide.pt.downloadmanager.external.FilePaths;

public class AptoideFilePaths implements FilePaths {
  private final String downloadsStoragePath;
  private final String apkPath;
  private final String obbPath;

  public AptoideFilePaths(String downloadsStoragePath, String apkPath, String obbPath) {
    this.downloadsStoragePath = downloadsStoragePath;
    this.apkPath = apkPath;
    this.obbPath = obbPath;
  }

  @Override public String getDownloadsStoragePath() {
    return downloadsStoragePath;
  }

  @Override public String getApkPath() {
    return apkPath;
  }

  @Override public String getObbPath() {
    return obbPath;
  }
}
