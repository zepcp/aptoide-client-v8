package cm.aptoide.pt.view.wizard;

import cm.aptoide.accountmanager.AptoideAccountManager;
import cm.aptoide.pt.account.AccountAnalytics;
import cm.aptoide.pt.crashreports.CrashReport;
import cm.aptoide.pt.presenter.Presenter;
import cm.aptoide.pt.presenter.View;
import cm.aptoide.pt.view.custom.AptoideViewPager;
import rx.Completable;
import rx.Observable;
import rx.Scheduler;

public class WizardPresenter implements Presenter, AptoideViewPager.OnPageChangeListener {

  private final WizardView view;
  private final AptoideAccountManager accountManager;
  private final CrashReport crashReport;
  private final AccountAnalytics accountAnalytics;
  private final Scheduler scheduler;

  public WizardPresenter(WizardView view, AptoideAccountManager accountManager,
      CrashReport crashReport, AccountAnalytics accountAnalytics, Scheduler scheduler) {
    this.view = view;
    this.accountManager = accountManager;
    this.crashReport = crashReport;
    this.accountAnalytics = accountAnalytics;
    this.scheduler = scheduler;
  }

  private Completable createViewsAndButtons() {
    return accountManager.accountStatus()
        .first()
        .toSingle()
        .observeOn(scheduler)
        .flatMapCompletable(account -> view.createWizardAdapter(account));
  }

  private Observable<Void> setupHandlers() {

    Observable<Void> skipWizardClick = view.skipWizardClick()
        .observeOn(scheduler)
        .doOnNext(__ -> view.skipWizard());

    return skipWizardClick;
  }

  @Override public void present() {
    view.getLifecycle()
        .filter(event -> event == View.LifecycleEvent.CREATE)
        .flatMap(__ -> createViewsAndButtons().andThen(setupHandlers()))
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(__ -> {
        }, err -> crashReport.log(err));
  }

  @Override
  public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
  }

  @Override public void onPageSelected(int position) {
    if (position == 2) {
      //Inside the wizards third page
      accountAnalytics.enterAccountScreen(AccountAnalytics.AccountOrigins.WIZARD);
    }
    view.handleSelectedPage(position);
  }

  @Override public void onPageScrollStateChanged(int state) {
  }
}
