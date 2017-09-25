package cm.aptoide.pt.view.search.demo2;

import android.os.Bundle;
import android.support.annotation.NonNull;
import cm.aptoide.pt.presenter.Presenter;
import cm.aptoide.pt.presenter.View;
import com.trello.rxlifecycle.LifecycleTransformer;
import rx.Observable;

public abstract class ChildView implements View {
  private final View parentView;

  protected ChildView(View parentView) {
    this.parentView = parentView;
  }

  @NonNull @Override
  public <T> LifecycleTransformer<T> bindUntilEvent(@NonNull LifecycleEvent lifecycleEvent) {
    return parentView.bindUntilEvent(lifecycleEvent);
  }

  @Override public Observable<LifecycleEvent> getLifecycle() {
    return parentView.getLifecycle();
  }

  @Override
  public void attachPresenter(Presenter presenter, @Deprecated Bundle savedInstanceState) {
    presenter.present();
  }
}
