/*
 * modified at 2017
 */

package cm.aptoide.pt.downloadmanager.test

import cm.aptoide.pt.downloadmanager.*
import cm.aptoide.pt.downloadmanager.stub.InMemoryDownloadRepositoryStub
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito
import rx.subjects.BehaviorSubject
import java.lang.IllegalArgumentException
import org.mockito.Mockito.`when` as whenever

class DownloadStateChangeTest {

  private var downloadManager: DownloadManager? = null
  private var downloadOrchestrator: DownloadOrchestrator? = null
  private var validDownloadRequest: DownloadRequest? = null
  private var invalidDownloadRequest: DownloadRequest? = null
  private var downloadRepository: DownloadRepository? = null

  @Before
  fun preparationBeforeEachMethod() {
    validDownloadRequest = DownloadRequestsCreator().createDownloadRequest()
    invalidDownloadRequest = DownloadRequestsCreator().createInvalidDownloadRequest()

    val downloadBehaviourSubject: BehaviorSubject<DownloadProgress> = BehaviorSubject.create()

    //    val analytics = AnalyticsStub()
    //    val downloadListener = DownloadStatusListener(analytics, downloadBehaviourSubject)
    //    val downloadQueue = FileDownloadQueueSet(downloadListener)

    //    val paths = FilePathStub("downloads")
    //    val fsOperations = FileSystemOperationsStub(paths)
    //    val fileDownloader = Mockito.mock(FileDownloader::class.java)
    //    val downloadOrchestrator = DownloadOrchestrator(3, fileDownloader, paths, fsOperations,
    //        downloadQueue, ConcurrentHashMap())

    downloadOrchestrator = Mockito.mock(DownloadOrchestrator::class.java)
    whenever(downloadOrchestrator?.startAndUpdateDownloadFileIds(Mockito.any())).then { _ ->
      {
        downloadBehaviourSubject.onNext(
            DownloadProgress(validDownloadRequest!!.hashCode, 0, DownloadStatus.STARTED))

//        downloadBehaviourSubject.onNext(
//            DownloadProgress(validDownloadRequest!!.hashCode, 0, 1L, 10L, 100,
//                DownloadStatus.PROGRESS))
      }
    }

    downloadRepository = InMemoryDownloadRepositoryStub()

    downloadManager = SynchronousDownloadManager(downloadRepository, downloadOrchestrator,
        downloadBehaviourSubject.asObservable())
  }

  @Test(expected = IllegalArgumentException::class)
  fun startingInvalidRequest() {
    downloadManager?.startDownload(invalidDownloadRequest)
  }

  @Test
  fun fromIdleToStarted() {

    val listDownloadsTestSubscriber = rx.observers.TestSubscriber<Download>()
    val observableDownload = downloadManager?.observeAllDownloadChanges()
    observableDownload?.subscribe(listDownloadsTestSubscriber)

    downloadManager?.startDownload(validDownloadRequest)

    // assert
    Mockito.verify(downloadOrchestrator!!, Mockito.times(1)).startAndUpdateDownloadFileIds(
        Mockito.any())

    //assertTrue(listDownloadsTestSubscriber.awaitValueCount(1, 10, TimeUnit.SECONDS))
    var downloads = listDownloadsTestSubscriber.onNextEvents
    assertEquals(1, downloads[0])
    assertEquals(validDownloadRequest!!.hashCode, downloads[0].hashCode)
    assertEquals(DownloadStatus.STARTED, downloads[0].overallDownloadStatus)

    //assertTrue(listDownloadsTestSubscriber.awaitValueCount(1, 10, TimeUnit.SECONDS))
    downloads = listDownloadsTestSubscriber.onNextEvents
    assertEquals(1, downloads[0])
    assertEquals(validDownloadRequest!!.hashCode, downloads[0].hashCode)
    assertEquals(DownloadStatus.PROGRESS, downloads[0].overallDownloadStatus)

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
