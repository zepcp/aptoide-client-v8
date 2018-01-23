package cm.aptoide.pt.billing.view.payment;

import cm.aptoide.pt.billing.authorization.Authorization;
import cm.aptoide.pt.presenter.View;
import java.util.List;
import rx.Observable;
import rx.subjects.PublishSubject;

/**
 * Created by jdandrade on 18/01/2018.
 */

interface SavedPaymentView extends View {

  PublishSubject<List<Authorization>> paymentMethodsToRemove();

  Observable<Void> actionDeleteMenuClicked();

  Observable<Void> onBackPressed();

  Observable<Void> addPaymentClicked();

  PublishSubject<Authorization> paymentAuthorizationSelected();

  void showPaymentMethods(List<Authorization> authorizedPayments);

  void setPaymentMethodSelected(Authorization authorization);

  void enterPaymentMethodRemovalMode();

  void hidePaymentMethods(List<Authorization> authorizations);
}
