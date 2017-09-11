/*
 * modified at 2017
 */

package cm.aptoide.pt.downloadmanager.test

import android.app.Application
import android.support.v4.content.PermissionChecker
import cm.aptoide.pt.downloadmanager.*
import cm.aptoide.pt.downloadmanager.stub.AnalyticsStub
import cm.aptoide.pt.downloadmanager.stub.FilePathStub
import cm.aptoide.pt.downloadmanager.stub.FileSystemOperationsStub
import cm.aptoide.pt.downloadmanager.stub.InMemoryDownloadRepositoryStub
import com.liulishuo.filedownloader.FileDownloader
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment.application
import org.robolectric.annotation.Config
import java.lang.IllegalArgumentException
import java.util.concurrent.TimeUnit
import org.mockito.Mockito.`when` as whenever


@RunWith(RobolectricTestRunner::class)
@Config(constants = BuildConfig::class)
class DownloadStateChangeTest {

  private var downloadManager: DownloadManager? = null
  private var downloadRequestsCreator: DownloadRequestsCreator? = null
  private var spiedApplication: Application? = null

  @Before
  fun preparationBeforeEachMethod() {
    downloadRequestsCreator = DownloadRequestsCreator()

    spiedApplication = Mockito.spy(application)
    whenever(spiedApplication!!.checkPermission(Mockito.anyString(), Mockito.anyInt(),
        Mockito.anyInt())).thenReturn(PermissionChecker.PERMISSION_GRANTED)

    val downloadRepository = InMemoryDownloadRepositoryStub()
    val analytics = AnalyticsStub()
    val paths = FilePathStub(spiedApplication!!.cacheDir.absolutePath)
    val fsOperations = FileSystemOperationsStub(paths)

    FileDownloader.setup(spiedApplication)
    val fileDownloader = FileDownloader.getImpl()

    downloadManager = SynchronousDownloadManager(downloadRepository, analytics, fileDownloader,
        paths, fsOperations)
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

    // execute
    val listDownloadsTestSubscriber = rx.observers.TestSubscriber<Download>()
    val observableDownload = downloadManager?.observeAllDownloadChanges()
    observableDownload?.subscribe(listDownloadsTestSubscriber)

    downloadManager?.startDownload(downloadRequest)

    // assert
    assertTrue(listDownloadsTestSubscriber.awaitValueCount(1, 10, TimeUnit.SECONDS))
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
