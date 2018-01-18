/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 19/08/2016.
 */

package cm.aptoide.pt.billing.view;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import cm.aptoide.pt.R;
import cm.aptoide.pt.billing.view.payment.PaymentMethodsFragment;
import cm.aptoide.pt.view.BackButtonActivity;

public class BillingActivity extends BackButtonActivity {

  public static final String EXTRA_MERCHANT_PACKAGE_NAME =
      "cm.aptoide.pt.view.payment.intent.extra.MERCHANT_PACKAGE_NAME ";
  public static final String EXTRA_SERVICE_NAME =
      "cm.aptoide.pt.view.payment.intent.extra.SERVICE_NAME";

  public static Intent getIntent(Context context, String merchantName) {
    final Intent intent = new Intent(context, BillingActivity.class);
    intent.putExtra(EXTRA_MERCHANT_PACKAGE_NAME, merchantName);
    return intent;
  }

  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.empty_frame);

    if (savedInstanceState == null) {
      getFragmentNavigator().navigateToWithoutBackSave(
          PaymentMethodsFragment.create(getIntent().getExtras()), true);
    }
  }
}
