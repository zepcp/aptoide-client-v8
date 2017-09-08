/*
 * modified at 2017
 */

package cm.aptoide.pt.downloadmanager.test

import cm.aptoide.pt.crashreports.CrashLogger
import cm.aptoide.pt.downloadmanager.*
import cm.aptoide.pt.downloadmanager.stub.DownloadRepositoryStub
import cm.aptoide.pt.utils.FileUtils
import com.liulishuo.filedownloader.FileDownloader
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.mock
import java.lang.IllegalArgumentException
import org.mockito.Mockito.`when` as whenever

class DownloadStateChangeTest {

  private var downloadManager: AptoideDownloadManager? = null
  private var downloadRequestsCreator: DownloadRequestsCreator? = null

  @Before
  fun preparationBeforeEachMethod() {
    downloadRequestsCreator = DownloadRequestsCreator()

    val downloadRepository = DownloadRepositoryStub()
    val cacheManager = mock(CacheManager::class.java)
    val fileUtils = FileUtils()
    val analytics = mock(Analytics::class.java)
    val fileDownloader = mock(FileDownloader::class.java)
    val paths = mock(FilePaths::class.java)
    val crashLogger = mock(CrashLogger::class.java)

    downloadManager = AptoideDownloadManager(downloadRepository, cacheManager, fileUtils, analytics,
        fileDownloader, paths, crashLogger)
  }

  @Test(expected = IllegalArgumentException::class)
  fun startingInvalidRequest() {
    val downloadRequest = downloadRequestsCreator?.createInvalidDownloadRequest()
    downloadManager?.startDownload(downloadRequest)
  }

  @Test
  fun fromIdleToStarted() {
    // prepare
    val downloadRequest = downloadRequestsCreator?.createDownloadRequest()
    val fileToDownload = mock(DownloadFile::class.java)
    downloadRequest?.filesToDownload?.add(fileToDownload)

    // execute
    val observableDownload = downloadManager?.observeAllDownloadChanges()

    downloadManager?.startDownload(downloadRequest)

    val listDownloadsTestSubscriber = rx.observers.TestSubscriber<Download>()
    val isDownloadingTestSubscriber = rx.observers.TestSubscriber<Boolean>()

    observableDownload?.subscribe(listDownloadsTestSubscriber)
    downloadManager?.isDownloading?.subscribe(isDownloadingTestSubscriber)

    // assert
    val isDownloadingResult = isDownloadingTestSubscriber.onNextEvents
    assertEquals(1, isDownloadingResult.size)
    assertTrue(isDownloadingResult[0])

    val downloads = listDownloadsTestSubscriber.onNextEvents
    assertEquals(1, downloads[0])
    assertEquals("abcd", downloads[0].hashCode)
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

  @Test
  fun cancelWholeQueue() {

  }
}
