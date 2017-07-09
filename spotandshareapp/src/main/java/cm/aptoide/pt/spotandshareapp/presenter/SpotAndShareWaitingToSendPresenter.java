package cm.aptoide.pt.spotandshareapp.presenter;

import android.os.Bundle;
import cm.aptoide.pt.spotandshareandroid.SpotAndShare;
import cm.aptoide.pt.spotandshareapp.view.SpotAndShareWaitingToSendView;
import cm.aptoide.pt.v8engine.presenter.Presenter;
import cm.aptoide.pt.v8engine.presenter.View;

/**
 * Created by filipe on 07-07-2017.
 */

public class SpotAndShareWaitingToSendPresenter implements Presenter {
  private SpotAndShareWaitingToSendView view;
  private SpotAndShare spotAndShare;

  public SpotAndShareWaitingToSendPresenter(SpotAndShareWaitingToSendView view,
      SpotAndShare spotAndShare) {
    this.view = view;
    this.spotAndShare = spotAndShare;
  }

  @Override public void present() {

    view.getLifecycle()
        .filter(lifecycleEvent -> lifecycleEvent.equals(View.LifecycleEvent.CREATE))
        .flatMap(created -> view.backButtonEvent())
        .doOnNext(click -> view.showExitWarning())
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(created -> {
        }, error -> error.printStackTrace());

    view.getLifecycle()
        .filter(lifecycleEvent -> lifecycleEvent.equals(View.LifecycleEvent.CREATE))
        .flatMap(created -> view.exitEvent())
        .doOnNext(clicked -> leaveGroup())
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(created -> {
        }, error -> error.printStackTrace());
  }

  private void leaveGroup() {
    spotAndShare.leaveGroup(() -> view.navigateBack(), err -> view.onLeaveGroupError());
  }

  @Override public void saveState(Bundle state) {

  }

  @Override public void restoreState(Bundle state) {

  }
}
