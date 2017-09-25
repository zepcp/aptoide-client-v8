package cm.aptoide.pt.view.search.demo2;

import cm.aptoide.pt.presenter.Presenter;

public class SearchViewItemPresenterFactory implements ViewItemPresenterFactory {

  @Override public <V extends ChildView> Presenter create(V view) {
    if(SearchResultItemView.class.isAssignableFrom(view.getClass())){
      return new SearchItemPresenter((SearchResultItemView)view);
    }

    return null;
  }
}
