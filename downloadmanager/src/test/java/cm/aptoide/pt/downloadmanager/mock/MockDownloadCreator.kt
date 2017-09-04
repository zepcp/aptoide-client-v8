/*
 * modified at 2017
 */

/*
 * modified at 2017
 */

package cm.aptoide.pt.downloadmanager.mock

import cm.aptoide.pt.downloadmanager.Download

class MockDownloadCreator {
    fun createObservableDownload(): rx.Observable<Download> {
        var download = MockDownload()
        download.versionCode = 1
        download.versionName = "first"
        download.appName = "mock app"
        download.md5 = "abcd"
        download.packageName = "com.unit.test.mock"
        return rx.Observable.just(download)
    }

    fun createDownload(): Download {
        var download = MockDownload()
        download.versionCode = 1
        download.versionName = "first"
        download.appName = "mock app"
        download.md5 = "abcd"
        download.packageName = "com.unit.test.mock"
        return download
    }
}
