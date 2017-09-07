/*
 * modified at 2017
 */

/*
 * modified at 2017
 */

package cm.aptoide.pt.downloadmanager.stub

import cm.aptoide.pt.downloadmanager.*


class DownloadStub : Download {

    private var error: DownloadError = DownloadError.NO_ERROR
    private var timestamp : Long = System.currentTimeMillis()
    private var applicationName : String? = null
    private var downloadFileParts : MutableList<DownloadFile> = mutableListOf()
    private var status : DownloadStatus = DownloadStatus.INVALID_STATUS
    private var progressPercentage : Int = 0
    private var applicationIcon : String? = null
    private var speed : Int = 0
    private var versionCode : Int = 0
    private var packageName : String? = null
    private var currentAction : DownloadAction = DownloadAction.NOTHING
    private var scheduled: Boolean = false
    private var fileHash: String? = null
    private var versionName: String? = null

    override fun getDownloadError(): DownloadError = error

    override fun setDownloadError(downloadError: DownloadError) {
        error = downloadError
    }

    override fun getTimeStamp(): Long = timeStamp

    override fun setTimeStamp(timeStamp: Long) {
        timestamp = timeStamp
    }

    override fun getAppName(): String? = applicationName

    override fun setAppName(appName: String?) {
        applicationName = appName
    }

    override fun getFilesToDownload(): MutableList<DownloadFile> = downloadFileParts

    override fun setFilesToDownload(filesToDownload: MutableList<DownloadFile>) {
        downloadFileParts = filesToDownload
    }

    override fun getOverallDownloadStatus(): DownloadStatus = status

    override fun setOverallDownloadStatus(overallDownloadStatus: DownloadStatus) {
        status = overallDownloadStatus
    }

    override fun getOverallProgress(): Int = progressPercentage

    override fun setOverallProgress(overallProgress: Int) {
        progressPercentage = overallProgress
    }

    override fun getIcon(): String? = applicationIcon

    override fun setIcon(icon: String?) {
        applicationIcon = icon
    }

    override fun getDownloadSpeed(): Int = speed

    override fun setDownloadSpeed(speed: Int) {
        this.speed = speed
    }

    override fun getVersionCode(): Int = versionCode

    override fun setVersionCode(versionCode: Int) {
        this.versionCode = versionCode
    }

    override fun getPackageName(): String? = packageName

    override fun setPackageName(packageName: String?) {
        this.packageName = packageName
    }

    override fun getAction(): DownloadAction = currentAction

    override fun setAction(action: DownloadAction) {
        currentAction = action
    }

    override fun isScheduled(): Boolean = scheduled

    override fun setScheduled(scheduled: Boolean) {
        this.scheduled = scheduled
    }

    override fun getHashCode(): String? = fileHash

    override fun setHashCode(md5: String?) {
        fileHash = md5
    }

    override fun getVersionName(): String? = versionName

    override fun setVersionName(versionName: String?) {
        this.versionName = versionName
    }
}
