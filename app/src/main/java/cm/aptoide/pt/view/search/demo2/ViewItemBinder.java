package cm.aptoide.pt.view.search.demo2;

import cm.aptoide.pt.presenter.Presenter;
import java.util.Map;

public class ViewItemBinder {

  private final ViewItemFactory viewItemFactory;
  private final ViewItemPresenterFactory viewItemPresenterFactory;

  private final Map<Object, ChildView> itemToViewItem;
  private final Map<ChildView, Presenter> viewItemToPresenter;

  public ViewItemBinder(ViewItemFactory viewItemFactory,
      ViewItemPresenterFactory viewItemPresenterFactory, Map<Object, ChildView> itemToViewItem,
      Map<ChildView, Presenter> viewItemToPresenter) {
    this.viewItemFactory = viewItemFactory;
    this.viewItemPresenterFactory = viewItemPresenterFactory;
    this.itemToViewItem = itemToViewItem;
    this.viewItemToPresenter = viewItemToPresenter;
  }

  public <T> void bind(T item) {
    ChildView childView;

    if (itemToViewItem.containsKey(item)) {
      childView = itemToViewItem.get(item);
    } else {
      childView = viewItemFactory.create(item);
      itemToViewItem.put(item, childView);
    }

    if (!viewItemToPresenter.containsKey(childView)) {
      final Presenter presenter = viewItemPresenterFactory.create(childView);
      viewItemToPresenter.put(childView, presenter);
      childView.attachPresenter(presenter, null);
    }
  }
}
