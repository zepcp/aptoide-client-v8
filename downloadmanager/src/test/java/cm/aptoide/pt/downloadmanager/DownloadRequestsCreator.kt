package cm.aptoide.pt.downloadmanager

import cm.aptoide.pt.downloadmanager.stub.DownloadFileStub
import cm.aptoide.pt.downloadmanager.stub.DownloadRequestStub
import cm.aptoide.pt.downloadmanager.stub.DownloadStub

class DownloadRequestsCreator {
  fun createObservableDownload(): rx.Observable<Download> {
    var download = DownloadStub()
    download.versionCode = 1
    download.versionName = "first"
    download.appName = "mock app"
    download.hashCode = "abcd"
    download.packageName = "com.unit.test.mock"
    return rx.Observable.just(download)
  }

  fun createDownloadRequest(): DownloadRequest {
    val downloadFile = DownloadFileStub("http://www.aptoide.com/robots.txt")
    return DownloadRequestStub(appName = "mock app", versionCode = 1, versionName = "first",
        hashCode = "abcd", currentAction = DownloadAction.DOWNLOAD_ONLY,
        packageName = "com.unit.test.mock", downloadFileParts = mutableListOf(downloadFile),
        appIcon = "none")
  }

  fun createInvalidDownloadRequest(): DownloadRequest {
    return DownloadRequestStub(appName = "mock app", versionCode = 0, versionName = "first",
        hashCode = "", currentAction = DownloadAction.DOWNLOAD_ONLY,
        packageName = "com.unit.test.mock", downloadFileParts = mutableListOf(), appIcon = "none")

  }

  fun createDownload(hashCode: String?, appName: String?, icon: String?, action: Int,
                     packageName: String?, versionCode: Int, versionName: String?,
                     downloadFiles: Collection<DownloadFile>): Download {
    var download = DownloadStub()
    download.action = DownloadAction.fromValue(action)
    download.hashCode = hashCode
    download.appName = appName
    download.icon = icon
    download.packageName = packageName
    download.versionCode = versionCode
    download.versionName = versionName
    download.filesToDownload = downloadFiles.toMutableList()
    return download
  }
}
