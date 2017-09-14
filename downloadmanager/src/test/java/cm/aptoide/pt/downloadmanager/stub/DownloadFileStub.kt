package cm.aptoide.pt.downloadmanager.stub

import cm.aptoide.pt.downloadmanager.DownloadFileType
import cm.aptoide.pt.downloadmanager.DownloadStatus
import cm.aptoide.pt.downloadmanager.base.DownloadFile


class DownloadFileStub(private var downloadLink: String, private var appPackageName: String,
                       private var fileHash: String) :
        DownloadFile {

    private var alternativeLink: String = ""
    private var status: DownloadStatus = DownloadStatus.INVALID_STATUS
    private var dwnldId: Int = 0
    private var fileKind: DownloadFileType = DownloadFileType.GENERIC
    private var pth: String = ""
    private var fileSimpleName: String = ""
    private var vrsnCode: Int = 0
    private var downloadProgress: Int = 0
    private var filePth: String = ""

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
    override fun setProgress(progress: Int) {
        downloadProgress = progress
    }

    override fun getPath(): String = pth
    override fun setPath(path: String) {
        pth = path
    }

    override fun getFilePath(): String = filePth
    override fun setFilePath(filePath: String) {
        filePth = filePath
    }

    override fun getFileName(): String = fileSimpleName
    override fun setFileName(fileName: String) {
        fileSimpleName = fileName
    }

    override fun getHashCode(): String = fileHash
    override fun setHashCode(hashCode: String) {
        fileHash = hashCode
    }

    override fun getVersionCode(): Int = vrsnCode
}
