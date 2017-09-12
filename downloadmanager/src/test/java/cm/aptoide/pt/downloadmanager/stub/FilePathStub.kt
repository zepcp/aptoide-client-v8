package cm.aptoide.pt.downloadmanager.stub

import cm.aptoide.pt.downloadmanager.external.FilePaths


class FilePathStub(private val base: String) : FilePaths {

  override fun getDownloadsStoragePath(): String = base + "generic/"

  override fun getApkPath(): String = base + "apk/"

  override fun getObbPath(): String = base + "obb/"
}
