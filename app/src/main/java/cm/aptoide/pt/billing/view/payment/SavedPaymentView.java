package cm.aptoide.pt.billing.view.payment;

import cm.aptoide.pt.billing.authorization.Authorization;
import cm.aptoide.pt.presenter.View;
import java.util.List;
import rx.Observable;
import rx.subjects.PublishSubject;

/**
 * Created by jdandrade on 18/01/2018.
 */

public interface SavedPaymentView extends View {

  PublishSubject<List<Authorization>> paymentMethodsToRemove();

  Observable<Void> actionDeleteMenuClicked();

  Observable<Void> onBackPressed();

  Observable<Void> addPaymentClicked();

  Observable<Authorization> paymentAuthorizationSelected();

  void showAuthorizedPaymentMethods(List<Authorization> authorizedPayments);

  void enterPaymentMethodRemovalMode();

  void hidePaymentMethods(List<Authorization> authorizations);

  void setAuthorizationSelected(Authorization authorization);
}