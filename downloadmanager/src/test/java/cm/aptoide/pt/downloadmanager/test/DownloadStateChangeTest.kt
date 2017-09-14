/*
 * modified at 2017
 */

package cm.aptoide.pt.downloadmanager.test

import cm.aptoide.pt.downloadmanager.DownloadOrchestrator
import cm.aptoide.pt.downloadmanager.DownloadProgress
import cm.aptoide.pt.downloadmanager.DownloadRequestsCreator
import cm.aptoide.pt.downloadmanager.SynchronousDownloadManager
import cm.aptoide.pt.downloadmanager.base.Download
import cm.aptoide.pt.downloadmanager.base.DownloadManager
import cm.aptoide.pt.downloadmanager.base.DownloadRequest
import cm.aptoide.pt.downloadmanager.external.DownloadRepository
import cm.aptoide.pt.downloadmanager.stub.InMemoryDownloadRepositoryStub
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

        downloadOrchestrator = Mockito.mock(DownloadOrchestrator::class.java)
        downloadRepository = InMemoryDownloadRepositoryStub()
        downloadManager = SynchronousDownloadManager(downloadRepository, downloadOrchestrator,
                downloadBehaviourSubject.asObservable())
    }

    @Test(expected = IllegalArgumentException::class)
    fun startingInvalidRequest() {
        downloadManager?.startDownload(invalidDownloadRequest)
    }

    // @Ignore @Test
    fun emittingEventWhenDownloadStarts() {
        val listDownloadsTestSubscriber = rx.observers.TestSubscriber<Download>()
        val observableDownload = downloadManager?.observeAllDownloadChanges()
        observableDownload?.subscribe(listDownloadsTestSubscriber)
    }

    @Test
    fun fromIdleToStarted() {
        downloadManager?.startDownload(validDownloadRequest)
        Mockito.verify(downloadOrchestrator!!, Mockito.times(1)).startAndUpdateDownloadFileIds(
                validDownloadRequest)
    }

    @Test
    fun formStartedToPaused() {
        downloadManager?.startDownload(validDownloadRequest)
        downloadManager?.pauseDownload(validDownloadRequest)
        Mockito.verify(downloadOrchestrator!!, Mockito.times(1)).pause(validDownloadRequest)
    }

    @Test
    fun formPausedToStarted() {
        downloadManager?.startDownload(validDownloadRequest)
        downloadManager?.pauseDownload(validDownloadRequest)
        downloadManager?.startDownload(validDownloadRequest)
        Mockito.verify(downloadOrchestrator!!, Mockito.times(2))
                .startAndUpdateDownloadFileIds(validDownloadRequest)
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
