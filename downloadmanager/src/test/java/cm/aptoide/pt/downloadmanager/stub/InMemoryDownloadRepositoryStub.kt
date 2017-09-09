package cm.aptoide.pt.downloadmanager.stub

import cm.aptoide.pt.downloadmanager.*
import rx.Observable
import rx.subjects.PublishSubject

class InMemoryDownloadRepositoryStub : DownloadRepository {

  private var downloads: MutableMap<String, Download> = mutableMapOf()
  private var downloadPublisher: PublishSubject<Collection<Download>> = PublishSubject.create()

  override fun getAll(): Observable<Collection<Download>> = downloadPublisher.asObservable()

  override fun get(md5: String): Observable<Download> {
    return downloadPublisher.asObservable().flatMapIterable { list -> list }.filter(
        { download -> download.hashCode?.equals(md5, ignoreCase = true) })
  }

  override fun delete(hashCode: String) {
    if (downloads.remove(hashCode) != null) {
      notifyChanges()
    }
  }

  override fun deleteAll() {
    downloads.clear()
    notifyChanges()
  }

  private fun notifyChanges() {
    downloadPublisher.onNext(downloads.values)
  }

  override fun save(download: Download) {
    downloads.put(download.hashCode, download)
    notifyChanges()
  }

  override fun <T : Collection<Download>> save(downloadCollection: T) {
    for (download in downloadCollection) {
      downloads.put(download.hashCode, download)
    }
    notifyChanges()
  }

  override fun <T : MutableList<DownloadFile>> insertNew(hashCode: String?, appName: String?,
                                                         icon: String?, action: Int,
                                                         packageName: String?, versionCode: Int,
                                                         versionName: String?,
                                                         downloadFiles: T): Download {

    val download = createDownload(hashCode, appName, icon, action, packageName, versionCode,
        versionName, downloadFiles)
    save(download)
    return download
  }

  override fun getCurrentDownloads(): Observable<List<Download>> {
    return downloadPublisher.asObservable().flatMapIterable { list -> list }.filter({ download ->
      isDownloading(download)
    }).toList().map { list -> list }
  }

  private fun isDownloading(download: Download): Boolean {
    if (download.overallDownloadStatus == DownloadStatus.IN_QUEUE) return true
    if (download.overallDownloadStatus == DownloadStatus.PROGRESS) return true
    if (download.overallDownloadStatus == DownloadStatus.PAUSED) return true
    if (download.overallDownloadStatus == DownloadStatus.PENDING) return true
    if (download.overallDownloadStatus == DownloadStatus.STARTED) return true
    return false
  }

  private fun createDownload(hashCode: String?, appName: String?, icon: String?, action: Int,
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
