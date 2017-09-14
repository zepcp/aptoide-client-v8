package cm.aptoide.pt.downloadmanager.test

import cm.aptoide.pt.downloadmanager.DownloadRequestsCreator
import cm.aptoide.pt.downloadmanager.base.Download
import cm.aptoide.pt.downloadmanager.external.DownloadRepository
import org.junit.Test
import org.mockito.Mockito

class ExampleDownloadTest {

    private var downloadRepository: DownloadRepository? = null
    private var downloadRequestsCreator: DownloadRequestsCreator? = null

    @org.junit.Before
    fun preparationBeforeEachMethod() {
        downloadRepository = Mockito.mock<DownloadRepository>(
                DownloadRepository::class.java)
        downloadRequestsCreator = DownloadRequestsCreator()
    }

    @Test
    fun sampleDownloadCreation() {
        val downloadHashCode = "abcd"
        Mockito.`when`(downloadRepository?.get(downloadHashCode)).thenReturn(
                downloadRequestsCreator?.createObservableDownload())
        val testSubscriber = rx.observers.TestSubscriber<Download>()
        val observableDownload = downloadRepository?.get(downloadHashCode)
        observableDownload?.subscribe(testSubscriber)

        testSubscriber.assertNoErrors()
        val resultDownload = testSubscriber.onNextEvents
        org.junit.Assert.assertEquals(downloadHashCode, resultDownload[0].hashCode)
    }
}
