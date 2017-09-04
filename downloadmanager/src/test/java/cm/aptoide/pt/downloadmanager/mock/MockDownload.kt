/*
 * modified at 2017
 */

/*
 * modified at 2017
 */

package cm.aptoide.pt.downloadmanager.mock

import cm.aptoide.pt.downloadmanager.*


class MockDownload : Download {

    private var error: DownloadError? = DownloadError.NO_ERROR
    private var timestamp : Long = System.currentTimeMillis()
    private var applicationName : String? = null
    private var downloadFileParts : MutableList<DownloadFile>? = mutableListOf()
    private var status : DownloadStatus? = null
    private var progressPercentage : Int = 0
    private var applicationIcon : String? = null
    private var speed : Int = 0
    private var versionCode : Int = 0
    private var packageName : String? = null
    private var currentAction : DownloadAction? = null
    private var scheduled: Boolean = false
    private var fileHash: String? = null
    private var versionName: String? = null

    override fun getDownloadError(): DownloadError? {
        return error
    }

    override fun setDownloadError(downloadError: DownloadError?) {
        error = downloadError
    }

    override fun getTimeStamp(): Long {
        return timeStamp
    }

    override fun setTimeStamp(timeStamp: Long) {
        timestamp = timeStamp
    }

    override fun getAppName(): String? {
        return applicationName
    }

    override fun setAppName(appName: String?) {
        applicationName = appName
    }

    override fun getFilesToDownload(): MutableList<DownloadFile>? {
        return downloadFileParts
    }

    override fun setFilesToDownload(filesToDownload: MutableList<DownloadFile>?) {
        downloadFileParts = filesToDownload
    }

    override fun getOverallDownloadStatus(): DownloadStatus? {
        return status
    }

    override fun setOverallDownloadStatus(overallDownloadStatus: DownloadStatus?) {
        status = overallDownloadStatus
    }

    override fun getOverallProgress(): Int {
        return progressPercentage
    }

    override fun setOverallProgress(overallProgress: Int) {
        progressPercentage = overallProgress
    }

    override fun getIcon(): String? {
        return applicationIcon
    }

    override fun setIcon(icon: String?) {
        applicationIcon = icon
    }

    override fun getDownloadSpeed(): Int {
        return speed
    }

    override fun setDownloadSpeed(speed: Int) {
        this.speed = speed
    }

    override fun getVersionCode(): Int {
        return versionCode
    }

    override fun setVersionCode(versionCode: Int) {
        this.versionCode = versionCode
    }

    override fun getPackageName(): String? {
        return packageName
    }

    override fun setPackageName(packageName: String?) {
        this.packageName = packageName
    }

    override fun getAction(): DownloadAction? {
        return currentAction
    }

    override fun setAction(action: DownloadAction?) {
        currentAction = action
    }

    override fun isScheduled(): Boolean {
        return scheduled
    }

    override fun setScheduled(scheduled: Boolean) {
        this.scheduled = scheduled
    }

    override fun getMd5(): String? {
        return fileHash
    }

    override fun setMd5(md5: String?) {
        fileHash = md5
    }

    override fun getVersionName(): String? {
        return versionName
    }

    override fun setVersionName(versionName: String?) {
        this.versionName = versionName
    }
}
