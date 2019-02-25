package cm.aptoide.pt.editorialList

import android.view.View
import cm.aptoide.pt.home.EditorialBundleViewHolder
import cm.aptoide.pt.home.HomeEvent
import rx.subjects.PublishSubject

class LoadingViewHolder(inflate: View, uiEventListener: PublishSubject<HomeEvent>) : EditorialBundleViewHolder(inflate, uiEventListener)