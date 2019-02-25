package cm.aptoide.pt.editorialList

import cm.aptoide.pt.home.EditorialHomeEvent
import cm.aptoide.pt.presenter.View
import rx.Observable

interface EditorialListView: View {

    fun editorialCardClicked(): Observable<EditorialHomeEvent>

    fun showLoading()

    fun hideLoading()

    fun showGenericError()

    fun showNetworkError()

    fun retryClicked(): Observable<Void>

    fun imageClick(): Observable<Void>

    fun showAvatar()

    fun setDefaultUserImage()

    fun setUserImage(userAvatarUrl: String)

    fun reachesBottom(): Observable<Any>

    fun populateView(curationCards: List<CurationCard>)

    fun showLoadMore()

    fun hideLoadMore()
}