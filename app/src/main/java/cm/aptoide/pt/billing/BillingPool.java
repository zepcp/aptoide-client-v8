package cm.aptoide.pt.billing;

import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Build;
import cm.aptoide.accountmanager.AptoideAccountManager;
import cm.aptoide.pt.BuildConfig;
import cm.aptoide.pt.billing.authorization.AuthorizationFactory;
import cm.aptoide.pt.billing.authorization.AuthorizationPersistence;
import cm.aptoide.pt.billing.customer.AccountUserPersistence;
import cm.aptoide.pt.billing.external.ExternalBillingSerializer;
import cm.aptoide.pt.billing.networking.AuthorizationMapperV3;
import cm.aptoide.pt.billing.networking.AuthorizationMapperV7;
import cm.aptoide.pt.billing.networking.BillingServiceV3;
import cm.aptoide.pt.billing.networking.BillingServiceV7;
import cm.aptoide.pt.billing.networking.PaymentMethodMapper;
import cm.aptoide.pt.billing.networking.ProductMapperV3;
import cm.aptoide.pt.billing.networking.ProductMapperV7;
import cm.aptoide.pt.billing.networking.PurchaseMapperV3;
import cm.aptoide.pt.billing.networking.PurchaseMapperV7;
import cm.aptoide.pt.billing.networking.TransactionMapperV3;
import cm.aptoide.pt.billing.networking.TransactionMapperV7;
import cm.aptoide.pt.billing.payment.Adyen;
import cm.aptoide.pt.billing.payment.CreditCardPaymentService;
import cm.aptoide.pt.billing.payment.PayPalPaymentService;
import cm.aptoide.pt.billing.payment.PaymentMethod;
import cm.aptoide.pt.billing.persistence.RealmAuthorizationMapper;
import cm.aptoide.pt.billing.persistence.RealmAuthorizationPersistence;
import cm.aptoide.pt.billing.purchase.Base64PurchaseTokenDecoder;
import cm.aptoide.pt.billing.purchase.PurchaseFactory;
import cm.aptoide.pt.billing.transaction.TransactionFactory;
import cm.aptoide.pt.crashreports.CrashLogger;
import cm.aptoide.pt.crashreports.CrashReport;
import cm.aptoide.pt.database.accessors.Database;
import cm.aptoide.pt.dataprovider.interfaces.TokenInvalidator;
import cm.aptoide.pt.dataprovider.ws.BodyInterceptor;
import cm.aptoide.pt.dataprovider.ws.v3.BaseBody;
import cm.aptoide.pt.install.PackageRepository;
import cm.aptoide.pt.networking.AuthenticationPersistence;
import java.util.HashMap;
import java.util.Map;
import okhttp3.OkHttpClient;
import retrofit2.Converter;
import rx.schedulers.Schedulers;

public class BillingPool {

  private final Map<String, Billing> pool;
  private final SharedPreferences sharedPreferences;
  private final BodyInterceptor<BaseBody> bodyInterceptorV3;
  private final OkHttpClient httpClient;
  private final AptoideAccountManager accountManager;
  private final Database database;
  private final Resources resources;
  private final PackageRepository packageRepository;
  private final TokenInvalidator tokenInvalidator;
  private final ExternalBillingSerializer externalBillingSerializer;
  private final BodyInterceptor<cm.aptoide.pt.dataprovider.ws.v7.BaseBody>
      accountSettingsBodyInterceptorPoolV7;
  private final Converter.Factory converterFactory;
  private final CrashLogger crashLogger;
  private final Adyen adyen;
  private final PurchaseFactory purchaseFactory;
  private final int minimumAPILevelPayPal;
  private final int minimumAPILevelAdyen;
  private final AuthenticationPersistence authenticationPersistence;
  private final String marketName;
  private final String payPalIcon;

  private BillingService billingServiceV7;
  private BillingService billingServiceV3;

  private AuthorizationPersistence authorizationPersistence;
  private UserPersistence userPersistence;
  private TransactionFactory transactionFactory;
  private AuthorizationFactory authorizationFactory;
  private PurchaseTokenDecoder purchaseTokenDecoder;
  private MerchantVersionProvider merchantVersionCodeProvider;
  private PayPalPaymentService payPalPaymentService;
  private CreditCardPaymentService creditCardPaymentService;

  public BillingPool(SharedPreferences sharedPreferences,
      BodyInterceptor<BaseBody> bodyInterceptorV3, OkHttpClient httpClient,
      AptoideAccountManager accountManager, Database database, Resources resources,
      PackageRepository packageRepository, TokenInvalidator tokenInvalidator,
      ExternalBillingSerializer externalBillingSerializer,
      BodyInterceptor<cm.aptoide.pt.dataprovider.ws.v7.BaseBody> accountSettingsBodyInterceptorPoolV7,
      HashMap<String, Billing> poll, Converter.Factory converterFactory, CrashReport crashLogger,
      Adyen adyen, PurchaseFactory purchaseFactory, int minimumAPILevelPayPal,
      int minimumAPILevelAdyen, AuthenticationPersistence authenticationPersistence,
      String marketName, String payPalIcon) {
    this.sharedPreferences = sharedPreferences;
    this.pool = poll;
    this.bodyInterceptorV3 = bodyInterceptorV3;
    this.httpClient = httpClient;
    this.accountManager = accountManager;
    this.database = database;
    this.resources = resources;
    this.packageRepository = packageRepository;
    this.tokenInvalidator = tokenInvalidator;
    this.externalBillingSerializer = externalBillingSerializer;
    this.accountSettingsBodyInterceptorPoolV7 = accountSettingsBodyInterceptorPoolV7;
    this.converterFactory = converterFactory;
    this.crashLogger = crashLogger;
    this.adyen = adyen;
    this.purchaseFactory = purchaseFactory;
    this.minimumAPILevelPayPal = minimumAPILevelPayPal;
    this.minimumAPILevelAdyen = minimumAPILevelAdyen;
    this.authenticationPersistence = authenticationPersistence;
    this.marketName = marketName;
    this.payPalIcon = payPalIcon;
  }

  public Billing get(String merchantPackageName) {
    if (!pool.containsKey(merchantPackageName)) {
      pool.put(merchantPackageName, create(merchantPackageName));
    }
    return pool.get(merchantPackageName);
  }

  private Billing create(String merchantPackageName) {
    if (merchantPackageName.equals(BuildConfig.APPLICATION_ID)) {
      return new Billing.Builder().setMerchantPackageName(merchantPackageName)
          .setMerchantVersionProvider(getMerchantVersionProvider())
          .setUserPersistence(getUserPersistence())
          .setPurchaseTokenDecoder(getPurchaseTokenDecoder())
          .setBillingService(getBillingServiceV3())
          .setAuthorizationPersistence(getAuthorizationPersistence())
          .registerPaymentService(PaymentMethod.CREDIT_CARD, getCreditCardPaymentService())
          .registerPaymentService(PaymentMethod.PAYPAL, getPayPalPaymentService())
          .setPayPalIcon(payPalIcon)
          .build();
    } else {
      return new Billing.Builder().setMerchantPackageName(merchantPackageName)
          .setMerchantVersionProvider(getMerchantVersionProvider())
          .setUserPersistence(getUserPersistence())
          .setPurchaseTokenDecoder(getPurchaseTokenDecoder())
          .setBillingService(getBillingServiceV7())
          .setAuthorizationPersistence(getAuthorizationPersistence())
          .registerPaymentService(PaymentMethod.CREDIT_CARD, getCreditCardPaymentService())
          .registerPaymentService(PaymentMethod.PAYPAL, getPayPalPaymentService())
          .setPayPalIcon(payPalIcon)
          .build();
    }
  }

  private PayPalPaymentService getPayPalPaymentService() {
    if (payPalPaymentService == null) {
      payPalPaymentService = new PayPalPaymentService();
    }
    return payPalPaymentService;
  }

  private CreditCardPaymentService getCreditCardPaymentService() {
    if (creditCardPaymentService == null) {
      creditCardPaymentService = new CreditCardPaymentService(adyen);
    }
    return creditCardPaymentService;
  }

  private MerchantVersionProvider getMerchantVersionProvider() {
    if (merchantVersionCodeProvider == null) {
      merchantVersionCodeProvider = new MerchantPackageRepositoryVersionProvider(packageRepository);
    }
    return merchantVersionCodeProvider;
  }

  private BillingService getBillingServiceV3() {
    if (billingServiceV3 == null) {
      billingServiceV3 =
          new BillingServiceV3(bodyInterceptorV3, httpClient, converterFactory, tokenInvalidator,
              sharedPreferences, new PurchaseMapperV3(purchaseFactory), new ProductMapperV3(),
              new AuthorizationMapperV3(getAuthorizationFactory()), resources,
              new PaymentMethod(1, PaymentMethod.PAYPAL, "PayPal", null, "", true),
              Build.VERSION.SDK_INT, minimumAPILevelPayPal, marketName,
              new TransactionMapperV3(getTransactionFactory()), getTransactionFactory());
    }
    return billingServiceV3;
  }

  private BillingService getBillingServiceV7() {
    if (billingServiceV7 == null) {
      billingServiceV7 =
          new BillingServiceV7(accountSettingsBodyInterceptorPoolV7, httpClient, converterFactory,
              tokenInvalidator, sharedPreferences,
              new PurchaseMapperV7(externalBillingSerializer, purchaseFactory),
              new ProductMapperV7(),
              new PaymentMethodMapper(crashLogger, Build.VERSION.SDK_INT, minimumAPILevelAdyen,
                  minimumAPILevelPayPal), purchaseFactory, authenticationPersistence,
              new AuthorizationMapperV7(getAuthorizationFactory()),
              new TransactionMapperV7(getTransactionFactory()), getTransactionFactory(),
              getAuthorizationFactory());
    }
    return billingServiceV7;
  }

  private PurchaseTokenDecoder getPurchaseTokenDecoder() {
    if (purchaseTokenDecoder == null) {
      purchaseTokenDecoder = new Base64PurchaseTokenDecoder();
    }
    return purchaseTokenDecoder;
  }

  private AuthorizationPersistence getAuthorizationPersistence() {
    if (authorizationPersistence == null) {
      authorizationPersistence = new RealmAuthorizationPersistence(database,
          new RealmAuthorizationMapper(getAuthorizationFactory()), Schedulers.io());
    }
    return authorizationPersistence;
  }

  private AuthorizationFactory getAuthorizationFactory() {
    if (authorizationFactory == null) {
      authorizationFactory = new AuthorizationFactory();
    }
    return authorizationFactory;
  }

  private UserPersistence getUserPersistence() {
    if (userPersistence == null) {
      userPersistence = new AccountUserPersistence(accountManager);
    }
    return userPersistence;
  }

  private TransactionFactory getTransactionFactory() {
    if (transactionFactory == null) {
      transactionFactory = new TransactionFactory();
    }
    return transactionFactory;
  }
}
