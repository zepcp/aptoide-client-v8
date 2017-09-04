/*
 * modified at 2017
 */

package cm.aptoide.pt.downloadmanager.test

import cm.aptoide.pt.crashreports.CrashLogger
import cm.aptoide.pt.downloadmanager.*
import cm.aptoide.pt.downloadmanager.mock.MockDownloadCreator
import cm.aptoide.pt.utils.FileUtils
import com.liulishuo.filedownloader.FileDownloader
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when` as whenever

class DownloadStateChangeTest {

    private var downloadRepository: DownloadRepository? = null
    private var downloadCreator: MockDownloadCreator? = null

    @Before
    fun preparationBeforeEachMethod() {
        downloadRepository = mock<DownloadRepository>(DownloadRepository::class.java)
        downloadCreator = MockDownloadCreator()
    }

    @Test
    fun fromIdleToStarted() {
        var cacheManager = mock(CacheManager::class.java)
        var fileUtils = FileUtils()
        var analytics = mock(Analytics::class.java)
        var fileDownloader = mock(FileDownloader::class.java)
        var paths = mock(FilePaths::class.java)
        var crashLogger = mock(CrashLogger::class.java)

        var downloadManager = AptoideDownloadManager(downloadRepository, cacheManager, fileUtils, analytics, fileDownloader, paths, crashLogger)
        assertTrue("Download Manager, after creation, should not be downloading", !downloadManager.isDownloading)


        downloadManager.startDownload(downloadCreator?.createDownload())
        
    }

    @Test
    fun formStartedToPaused() {

    }

    @Test
    fun formPausedToStarted() {

    }

    @Test
    fun formStartedToFinished() {

    }

    @Test
    fun fromStartedToErrorDownloading() {

    }

    @Test
    fun fromErrorToRetry() {

    }

    @Test
    fun fromRetryToStarted() {

    }

    @Test
    fun fromStartedToFileMissingError() {

    }
}
