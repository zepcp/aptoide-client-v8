package cm.aptoide.pt.view.search.demo2;

import cm.aptoide.pt.dataprovider.model.v7.search.SearchApp;
import cm.aptoide.pt.presenter.View;
import com.jakewharton.rxrelay.PublishRelay;

public class ViewItemFactory {

  private final PublishRelay<SearchViewItemEvent> bus;
  private final View parentView;

  public ViewItemFactory(PublishRelay<SearchViewItemEvent> bus, View parentView) {
    this.bus = bus;
    this.parentView = parentView;
  }

  public <T> ChildView create(T item) {
    Class clazz = item.getClass();
    if (SearchApp.class.isAssignableFrom(clazz)) {
      return new SearchResultChildItemView(bus, parentView);
    }

    throw new IllegalArgumentException("Invalid item type");
  }
}
