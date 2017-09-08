package cm.aptoide.pt.downloadmanager.stub

import cm.aptoide.pt.downloadmanager.DownloadFile
import cm.aptoide.pt.downloadmanager.DownloadFileType
import cm.aptoide.pt.downloadmanager.DownloadStatus


class DownloadFileStub : DownloadFile {

  constructor(downloadLink: String) {
    this.status = DownloadStatus.INVALID_STATUS
    this.fileKind = DownloadFileType.GENERIC
    this.downloadLink = downloadLink
  }

  private var alternativeLink: String = ""
  private var downloadLink: String = ""
  private var status: DownloadStatus
  private var appPackageName: String = ""
  private var dwnldId: Int = 0
  private var fileKind: DownloadFileType

  override fun getAltLink(): String = alternativeLink;

  override fun setAltLink(altLink: String) {
    alternativeLink = altLink
  }

  override fun getStatus(): DownloadStatus = status

  override fun setStatus(status: DownloadStatus) {
    this.status = status
  }

  override fun getLink(): String = downloadLink

  override fun setLink(link: String) {
    downloadLink = link
  }

  override fun getPackageName(): String = appPackageName


  override fun setPackageName(packageName: String) {
    appPackageName = packageName
  }

  override fun getDownloadId(): Int = dwnldId

  override fun setDownloadId(downloadId: Int) {
    dwnldId = downloadId
  }

  override fun getFileType(): DownloadFileType = fileKind

  override fun setFileType(fileType: DownloadFileType) {
    fileKind = fileType
  }

  override fun getProgress(): Int = downloadProgress

  private var downloadProgress: Int = 0

  override fun setProgress(progress: Int) {
    downloadProgress = progress
  }

  override fun getPath(): String = pth

  private var pth: String = ""

  override fun setPath(path: String) {
    pth = path
  }

  override fun getFilePath(): String = filePth

  private var filePth: String = ""

  override fun setFilePath(filePath: String) {
    filePth = filePath
  }

  private var fileSimpleName: String = ""

  override fun getFileName(): String = fileSimpleName

  override fun setFileName(fileName: String) {
    fileSimpleName = fileName
  }

  override fun getHashCode(): String = fileHash

  private var fileHash: String = ""

  override fun setHashCode(hashCode: String) {
    fileHash = hashCode
  }

  private var vrsnCode: Int = 0

  override fun getVersionCode(): Int = vrsnCode

}
