package cm.aptoide.pt.view.search.demo2;

import cm.aptoide.pt.presenter.Presenter;

public interface ViewItemPresenterFactory {
  <V extends ChildView> Presenter create(V view);
}
