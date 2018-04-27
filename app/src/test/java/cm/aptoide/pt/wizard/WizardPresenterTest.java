package cm.aptoide.pt.wizard;

import cm.aptoide.accountmanager.Account;
import cm.aptoide.accountmanager.AptoideAccountManager;
import cm.aptoide.pt.account.AccountAnalytics;
import cm.aptoide.pt.crashreports.CrashReport;
import cm.aptoide.pt.presenter.View;
import cm.aptoide.pt.view.wizard.WizardFragment;
import cm.aptoide.pt.view.wizard.WizardPresenter;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import rx.Completable;
import rx.Observable;
import rx.schedulers.Schedulers;
import rx.subjects.PublishSubject;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by franciscocalado on 27/04/18.
 */

public class WizardPresenterTest {

  @Mock private WizardFragment view;
  @Mock private AptoideAccountManager accountManager;
  @Mock private CrashReport crashReport;
  @Mock private AccountAnalytics analytics;
  @Mock private Account account;

  private WizardPresenter presenter;
  private PublishSubject<View.LifecycleEvent> lifecycleEvent;

  @Before public void setUpWizardPresenter() {
    MockitoAnnotations.initMocks(this);
    lifecycleEvent = PublishSubject.create();

    presenter =
        new WizardPresenter(view, accountManager, crashReport, analytics, Schedulers.immediate());

    when(view.getLifecycle()).thenReturn(lifecycleEvent);
  }

  @Test public void wizardPresentTest() {
    when(accountManager.accountStatus()).thenReturn(Observable.just(account));
    when(view.createWizardAdapter(account)).thenReturn(Completable.complete());
    when(view.skipWizardClick()).thenReturn(Observable.just(null));

    presenter.present();
    lifecycleEvent.onNext(View.LifecycleEvent.CREATE);

    verify(view).createWizardAdapter(account);
    verify(view).skipWizardClick();
    verify(view).skipWizard();
  }
}
