package cm.aptoide.pt.billing.view.payment;

import cm.aptoide.pt.billing.authorization.Authorization;

/**
 * Created by jdandrade on 23/01/2018.
 */

interface PaymentMethodSelectedListener {

  void onCheck(Authorization authorization, boolean isChecked);
}
