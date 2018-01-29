/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 11/08/2016.
 */

package cm.aptoide.pt.iab;

import android.content.Intent;
import android.os.IBinder;
import cm.aptoide.pt.BaseService;
import cm.aptoide.pt.BuildConfig;
import cm.aptoide.pt.billing.BillingAnalytics;
import cm.aptoide.pt.billing.BillingFactory;
import cm.aptoide.pt.billing.binder.BillingBinder;
import cm.aptoide.pt.billing.binder.BillingBinderSerializer;
import cm.aptoide.pt.billing.view.PaymentThrowableCodeMapper;
import cm.aptoide.pt.billing.view.PurchaseBundleMapper;
import cm.aptoide.pt.crashreports.CrashReport;
import javax.inject.Inject;

public class InAppBillingService extends BaseService {

  @Inject BillingBinderSerializer serializer;
  @Inject PaymentThrowableCodeMapper throwableCodeMapper;
  @Inject PurchaseBundleMapper purchaseBundleMapper;
  @Inject BillingFactory billingFactory;
  @Inject BillingAnalytics billingAnalytics;

  @Override public void onCreate() {
    super.onCreate();
    getApplicationComponent().inject(this);
  }

  @Override public IBinder onBind(Intent intent) {
    return new BillingBinder(this, serializer, throwableCodeMapper, purchaseBundleMapper,
        CrashReport.getInstance(), BuildConfig.IN_BILLING_SUPPORTED_API_VERSION, billingAnalytics,
        getPackageManager(), billingFactory);
  }
}