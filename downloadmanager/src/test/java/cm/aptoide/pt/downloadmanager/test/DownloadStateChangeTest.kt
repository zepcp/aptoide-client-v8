/*
 * modified at 2017
 */

package cm.aptoide.pt.downloadmanager.test

import cm.aptoide.pt.downloadmanager.*
import cm.aptoide.pt.downloadmanager.stub.AnalyticsStub
import cm.aptoide.pt.downloadmanager.stub.FilePathStub
import cm.aptoide.pt.downloadmanager.stub.FileSystemOperationsStub
import cm.aptoide.pt.downloadmanager.stub.InMemoryDownloadRepositoryStub
import com.liulishuo.filedownloader.FileDownloader
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.mock
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment.application
import org.robolectric.annotation.Config
import java.lang.IllegalArgumentException
import org.mockito.Mockito.`when` as whenever

@RunWith(RobolectricTestRunner::class)
@Config(constants = BuildConfig::class)
class DownloadStateChangeTest {

  private var downloadManager: DownloadManager? = null
  private var downloadRequestsCreator: DownloadRequestsCreator? = null

  @Before
  fun preparationBeforeEachMethod() {
    downloadRequestsCreator = DownloadRequestsCreator()

    val downloadRepository = InMemoryDownloadRepositoryStub()
    val analytics = AnalyticsStub()
    val paths = FilePathStub(application.cacheDir.absolutePath)
    val fsOperations = FileSystemOperationsStub(paths)
    val fileDownloader = getFileDownloader()

    val downloadOrchestrator = DownloadOrchestrator(3, fileDownloader, paths, fsOperations,
        analytics)

    downloadManager = SynchronousDownloadManager(downloadOrchestrator, downloadRepository)
  }

  private fun getFileDownloader(): FileDownloader {
    FileDownloader.setup(application)
    return FileDownloader.getImpl()
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

    observableDownload?.subscribe(listDownloadsTestSubscriber)

    // assert
    val downloads = listDownloadsTestSubscriber.onNextEvents
    assertEquals(1, downloads[0])
    assertEquals("d9466f81de6b77711ad5584176f84187", downloads[0].hashCode)
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
