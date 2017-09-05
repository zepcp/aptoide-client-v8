/*
 * modified at 2017
 */

package cm.aptoide.pt.downloadmanager.test

import cm.aptoide.pt.crashreports.CrashLogger
import cm.aptoide.pt.downloadmanager.*
import cm.aptoide.pt.downloadmanager.mock.MockDownloadCreator
import cm.aptoide.pt.utils.FileUtils
import com.liulishuo.filedownloader.FileDownloader
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when` as whenever

class DownloadStateChangeTest {

    private var downloadManager: AptoideDownloadManager? = null
    private var downloadCreator: MockDownloadCreator? = null

    @Before
    fun preparationBeforeEachMethod() {
        downloadCreator = MockDownloadCreator()

        val downloadRepository = mock<DownloadRepository>(DownloadRepository::class.java)
        val cacheManager = mock(CacheManager::class.java)
        val fileUtils = FileUtils()
        val analytics = mock(Analytics::class.java)
        val fileDownloader = mock(FileDownloader::class.java)
        val paths = mock(FilePaths::class.java)
        val crashLogger = mock(CrashLogger::class.java)

        downloadManager = AptoideDownloadManager(downloadRepository, cacheManager, fileUtils, analytics, fileDownloader, paths, crashLogger)
    }

    @Test
    fun fromIdleToStarted() {
        // prepare
        val download = downloadCreator?.createDownload()

        // execute
        val observableDownload = downloadManager?.observeAllDownloadChanges()

        downloadManager?.startDownload(download)

        val listDownloadsTestSubscriber = rx.observers.TestSubscriber<List<Download>>()
        val isDownloadingTestSubscriber = rx.observers.TestSubscriber<Boolean>()

        observableDownload?.subscribe(listDownloadsTestSubscriber)
        downloadManager?.isDownloading?.subscribe(isDownloadingTestSubscriber)

        // assert
        val isDownloadingResult = isDownloadingTestSubscriber.onNextEvents
        assertEquals(1, isDownloadingResult.size)
        assertTrue(isDownloadingResult[0])

        val downloads = listDownloadsTestSubscriber.onNextEvents
        assertEquals(1, downloads[0].size)
        assertEquals("abcd", downloads[0][0].hashCode)
    }

    @Test
    fun formStartedToPaused() {
        // prepare
        val download = downloadCreator?.createDownload()

        // execute
        val observableDownload = downloadManager?.observeAllDownloadChanges()

        downloadManager?.startDownload(download)

        val listDownloadsTestSubscriber = rx.observers.TestSubscriber<List<Download>>()
        val isDownloadingTestSubscriber = rx.observers.TestSubscriber<Boolean>()

        observableDownload?.subscribe(listDownloadsTestSubscriber)
        downloadManager?.isDownloading?.subscribe(isDownloadingTestSubscriber)

        // assert
        val isDownloadingResult = isDownloadingTestSubscriber.onNextEvents
        assertEquals(1, isDownloadingResult.size)
        assertTrue(isDownloadingResult[0])

        val downloads = listDownloadsTestSubscriber.onNextEvents
        assertEquals(1, downloads[0].size)
        assertEquals(DownloadStatus.STARTED, downloads[0][0].overallDownloadStatus)
    }

    @Test
    fun formPausedToStarted() {
        // prepare
        val download = downloadCreator?.createDownload()

        // execute
        val observableDownload = downloadManager?.observeAllDownloadChanges()

        downloadManager?.startDownload(download)

        val listDownloadsTestSubscriber = rx.observers.TestSubscriber<List<Download>>()
        val isDownloadingTestSubscriber = rx.observers.TestSubscriber<Boolean>()

        observableDownload?.subscribe(listDownloadsTestSubscriber)
        downloadManager?.isDownloading?.subscribe(isDownloadingTestSubscriber)

        // assert
        val isDownloadingResult = isDownloadingTestSubscriber.onNextEvents
        assertEquals(1, isDownloadingResult.size)
        assertTrue(isDownloadingResult[0])

        val downloads = listDownloadsTestSubscriber.onNextEvents
        assertEquals(1, downloads[0].size)
        assertEquals(DownloadStatus.STARTED, downloads[0][0].overallDownloadStatus)
    }

    @Test
    fun formStartedToFinished() {
        // prepare
        val download = downloadCreator?.createDownload()

        // execute
        val observableDownload = downloadManager?.observeAllDownloadChanges()

        downloadManager?.startDownload(download)

        val listDownloadsTestSubscriber = rx.observers.TestSubscriber<List<Download>>()
        val isDownloadingTestSubscriber = rx.observers.TestSubscriber<Boolean>()

        observableDownload?.subscribe(listDownloadsTestSubscriber)
        downloadManager?.isDownloading?.subscribe(isDownloadingTestSubscriber)

        // assert
        val isDownloadingResult = isDownloadingTestSubscriber.onNextEvents
        assertEquals(1, isDownloadingResult.size)
        assertTrue(isDownloadingResult[0])

        val downloads = listDownloadsTestSubscriber.onNextEvents
        assertEquals(1, downloads[0].size)
        assertEquals(DownloadStatus.STARTED, downloads[0][0].overallDownloadStatus)

        val downloadsPart2 = listDownloadsTestSubscriber.onNextEvents
        assertEquals(1, downloads[0].size)
        assertEquals(DownloadStatus.STARTED, downloadsPart2[0][0].overallDownloadStatus)
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

    @Test
    fun cancelWholeQueue(){

    }
}
