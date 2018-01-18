/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 19/08/2016.
 */

package cm.aptoide.pt.billing.view.payment;

import cm.aptoide.pt.billing.authorization.Authorization;
import cm.aptoide.pt.billing.product.Product;
import cm.aptoide.pt.presenter.View;
import rx.Observable;

public interface PaymentView extends View {

  Observable<Void> cancelEvent();

  Observable<Void> buyEvent();

  void showLoading();

  void showAuthorization(Authorization authorization);

  void showProduct(Product product);

  void showMerchant(String merchantName);

  void hideLoading();

  void showNetworkError();

  void showUnknownError();
}
