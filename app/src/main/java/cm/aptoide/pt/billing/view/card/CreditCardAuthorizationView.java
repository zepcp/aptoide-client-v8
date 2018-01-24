package cm.aptoide.pt.billing.view.card;

import cm.aptoide.pt.billing.payment.CreditCard;
import cm.aptoide.pt.presenter.View;
import rx.Observable;

public interface CreditCardAuthorizationView extends View {

  void showLoading();

  void hideLoading();

  Observable<CreditCard> saveCreditCardEvent();

  Observable<Void> cancelEvent();

  void showNetworkError();

  void showUnknownError();
}
