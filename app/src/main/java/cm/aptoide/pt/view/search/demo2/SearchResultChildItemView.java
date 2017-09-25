package cm.aptoide.pt.view.search.demo2;

import cm.aptoide.pt.presenter.View;
import com.jakewharton.rxrelay.PublishRelay;
import rx.Observable;

public class SearchResultChildItemView extends ChildView implements SearchResultItemView {

  private final PublishRelay<SearchViewItemEvent> bus;

  public SearchResultChildItemView(PublishRelay<SearchViewItemEvent> bus, View parentView) {
    super(parentView);
    this.bus = bus;
  }

  @Override public Observable<Void> onOpenAppViewClick() {
    return bus.filter(event -> event.equals(SearchViewItemEvent.OPEN_APP_VIEW))
        .map(__ -> null);
  }

  @Override public Observable<Void> onOpenPopupMenuClick() {
    return bus.filter(event -> event.equals(SearchViewItemEvent.OPEN_POPUP_MENU))
        .map(__ -> null);
  }

  @Override public Observable<Void> onOtherVersionsClick() {
    return bus.filter(event -> event.equals(SearchViewItemEvent.OPEN_OTHER_VERSIONS))
        .map(__ -> null);
  }

  @Override public Observable<Void> onOpenStoreClick() {
    return bus.filter(event -> event.equals(SearchViewItemEvent.OPEN_STORE))
        .map(__ -> null);
  }
}
