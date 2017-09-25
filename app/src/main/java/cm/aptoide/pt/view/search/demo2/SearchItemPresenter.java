package cm.aptoide.pt.view.search.demo2;

import android.os.Bundle;
import cm.aptoide.pt.presenter.Presenter;

public class SearchItemPresenter implements Presenter{

  private final SearchResultItemView view;

  public SearchItemPresenter(SearchResultItemView view) {
    this.view = view;
  }

  @Override public void present() {

  }

  @Override public void saveState(Bundle state) {

  }

  @Override public void restoreState(Bundle state) {

  }
}
