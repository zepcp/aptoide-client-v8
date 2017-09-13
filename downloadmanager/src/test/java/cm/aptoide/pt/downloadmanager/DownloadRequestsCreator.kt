package cm.aptoide.pt.downloadmanager

import cm.aptoide.pt.downloadmanager.base.Download
import cm.aptoide.pt.downloadmanager.base.DownloadRequest
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
        val downloadFile = DownloadFileStub(appPackageName = "com.whatsapp",
                fileHash = "d9466f81de6b77711ad5584176f84187",
                downloadLink = "http://pool.apk.aptoide.com/mark8/com-whatsapp-451996-32005517-d9466f81de6b77711ad5584176f84187.apk")

        return DownloadRequestStub(appName = "com.whatsapp", versionCode = 1, versionName = "first",
                hashCode = "d9466f81de6b77711ad5584176f84187", currentAction = DownloadAction.DOWNLOAD_ONLY,
                packageName = "com.whatsapp", downloadFileParts = mutableListOf(downloadFile),
                appIcon = "none")
    }

    fun createInvalidDownloadRequest(): DownloadRequest {
        return DownloadRequestStub(appName = "mock app", versionCode = 0, versionName = "first",
                hashCode = "", currentAction = DownloadAction.DOWNLOAD_ONLY,
                packageName = "com.unit.test.mock", downloadFileParts = mutableListOf(), appIcon = "none")

    }
}
