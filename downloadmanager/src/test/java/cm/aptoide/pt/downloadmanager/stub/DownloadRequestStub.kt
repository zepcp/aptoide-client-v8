package cm.aptoide.pt.downloadmanager.stub

import cm.aptoide.pt.downloadmanager.DownloadAction
import cm.aptoide.pt.downloadmanager.base.DownloadFile
import cm.aptoide.pt.downloadmanager.base.DownloadRequest


class DownloadRequestStub(private var appName: String,
                          private var downloadFileParts: MutableList<DownloadFile>,
                          private var appIcon: String, private var versionCode: Int,
                          private var packageName: String,
                          private var currentAction: DownloadAction, private var hashCode: String,
                          private var versionName: String) :
    DownloadRequest {


  override fun getFilesToDownload(): MutableList<DownloadFile> = downloadFileParts

  override fun getVersionCode(): Int = versionCode

  override fun getPackageName(): String = packageName

  override fun getHashCode(): String = hashCode

  override fun getVersionName(): String = versionName

  override fun getApplicationName(): String = appName

  override fun getDownloadAction(): DownloadAction = currentAction

  override fun getApplicationIcon(): String = appIcon

}
