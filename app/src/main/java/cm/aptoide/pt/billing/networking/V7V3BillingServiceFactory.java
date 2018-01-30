package cm.aptoide.pt.billing.networking;

import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Build;
import cm.aptoide.pt.BuildConfig;
import cm.aptoide.pt.billing.BillingService;
import cm.aptoide.pt.billing.BillingServiceFactory;
import cm.aptoide.pt.billing.authorization.AuthorizationFactory;
import cm.aptoide.pt.billing.binder.BillingBinderSerializer;
import cm.aptoide.pt.billing.payment.PaymentMethod;
import cm.aptoide.pt.billing.purchase.PurchaseFactory;
import cm.aptoide.pt.billing.transaction.TransactionFactory;
import cm.aptoide.pt.dataprovider.interfaces.TokenInvalidator;
import cm.aptoide.pt.dataprovider.ws.BodyInterceptor;
import cm.aptoide.pt.dataprovider.ws.v3.BaseBody;
import cm.aptoide.pt.networking.AuthenticationPersistence;
import okhttp3.OkHttpClient;
import retrofit2.Converter;

public class V7V3BillingServiceFactory implements BillingServiceFactory {

  private final BodyInterceptor<BaseBody> bodyInterceptorV3;
  private final OkHttpClient httpClient;
  private final Converter.Factory converterFactory;
  private final TokenInvalidator tokenInvalidator;
  private final SharedPreferences sharedPreferences;
  private final PurchaseFactory purchaseFactory;
  private final AuthorizationFactory authorizationFactory;
  private final Resources resources;
  private final int minimumAPILevelPayPal;
  private final String marketName;
  private final TransactionFactory transactionFactory;
  private final BodyInterceptor<cm.aptoide.pt.dataprovider.ws.v7.BaseBody>
      accountSettingsBodyInterceptorPoolV7;
  private final BillingBinderSerializer billingBinderSerializer;
  private final int minimumAPILevelAdyen;
  private final AuthenticationPersistence authenticationPersistence;
  private final String payPalIcon;

  private BillingServiceV3 billingServiceV3;
  private BillingServiceV7 billingServiceV7;

  public V7V3BillingServiceFactory(BodyInterceptor<BaseBody> bodyInterceptorV3,
      OkHttpClient httpClient, Converter.Factory converterFactory,
      TokenInvalidator tokenInvalidator, SharedPreferences sharedPreferences,
      PurchaseFactory purchaseFactory, AuthorizationFactory authorizationFactory,
      Resources resources, int minimumAPILevelPayPal, String marketName,
      TransactionFactory transactionFactory,
      BodyInterceptor<cm.aptoide.pt.dataprovider.ws.v7.BaseBody> accountSettingsBodyInterceptorPoolV7,
      BillingBinderSerializer billingBinderSerializer, int minimumAPILevelAdyen,
      AuthenticationPersistence authenticationPersistence, String payPalIcon) {
    this.bodyInterceptorV3 = bodyInterceptorV3;
    this.httpClient = httpClient;
    this.converterFactory = converterFactory;
    this.tokenInvalidator = tokenInvalidator;
    this.sharedPreferences = sharedPreferences;
    this.purchaseFactory = purchaseFactory;
    this.authorizationFactory = authorizationFactory;
    this.resources = resources;
    this.minimumAPILevelPayPal = minimumAPILevelPayPal;
    this.marketName = marketName;
    this.transactionFactory = transactionFactory;
    this.accountSettingsBodyInterceptorPoolV7 = accountSettingsBodyInterceptorPoolV7;
    this.billingBinderSerializer = billingBinderSerializer;
    this.minimumAPILevelAdyen = minimumAPILevelAdyen;
    this.authenticationPersistence = authenticationPersistence;
    this.payPalIcon = payPalIcon;
  }

  @Override public BillingService create(String merchantPackageName) {
    if (merchantPackageName.equals(BuildConfig.APPLICATION_ID)) {
      return getBillingServiceV3();
    } else {
      return getBillingServiceV7();
    }
  }

  private BillingService getBillingServiceV3() {
    if (billingServiceV3 == null) {
      billingServiceV3 =
          new BillingServiceV3(bodyInterceptorV3, httpClient, converterFactory, tokenInvalidator,
              sharedPreferences, new PurchaseMapperV3(purchaseFactory), new ProductMapperV3(),
              resources,
              new PaymentMethod(1, PaymentMethod.PAYPAL, "PayPal", null, payPalIcon, true),
              Build.VERSION.SDK_INT, minimumAPILevelPayPal, marketName, transactionFactory,
              payPalIcon, authorizationFactory);
    }
    return billingServiceV3;
  }

  private BillingService getBillingServiceV7() {
    if (billingServiceV7 == null) {
      billingServiceV7 =
          new BillingServiceV7(accountSettingsBodyInterceptorPoolV7, httpClient, converterFactory,
              tokenInvalidator, sharedPreferences,
              new PurchaseMapperV7(billingBinderSerializer, purchaseFactory), new ProductMapperV7(),
              new PaymentMethodMapper(Build.VERSION.SDK_INT, minimumAPILevelAdyen,
                  minimumAPILevelPayPal), authenticationPersistence,
              new AuthorizationMapperV7(authorizationFactory),
              new TransactionMapperV7(transactionFactory));
    }
    return billingServiceV7;
  }
}
