package cm.aptoide.pt.billing;

import cm.aptoide.pt.billing.authorization.Authorization;
import cm.aptoide.pt.billing.customer.AuthorizationPersistence;
import cm.aptoide.pt.billing.authorization.PayPalAuthorization;
import cm.aptoide.pt.billing.customer.CustomerManager;
import cm.aptoide.pt.billing.customer.UserPersistence;
import cm.aptoide.pt.billing.payment.PaymentServiceAdapter;
import java.util.HashMap;
import java.util.Map;
import rx.subjects.PublishSubject;

public class BillingFactory {

  private final Map<String, Billing> pool;
  private final Map<BillingService, PaymentServiceAdapter> adapters;
  private final Map<BillingService, CustomerManager> customerManagers;
  private final BillingServiceFactory billingServiceFactory;
  private final Map<String, PaymentService> services;
  private final AuthorizationPersistence authorizationPersistence;
  private final PurchaseTokenDecoder tokenDecoder;
  private final MerchantVersionProvider versionProvider;
  private final UserPersistence userPersistence;
  private final PayPalAuthorization payPalAuthorization;

  private BillingFactory(Map<String, Billing> pool,
      Map<BillingService, PaymentServiceAdapter> adapters,
      Map<BillingService, CustomerManager> customerManagers,
      BillingServiceFactory billingServiceFactory, Map<String, PaymentService> services,
      AuthorizationPersistence authorizationPersistence, PurchaseTokenDecoder tokenDecoder,
      MerchantVersionProvider versionProvider, UserPersistence userPersistence,
      PayPalAuthorization payPalAuthorization) {
    this.pool = pool;
    this.adapters = adapters;
    this.customerManagers = customerManagers;
    this.billingServiceFactory = billingServiceFactory;
    this.services = services;
    this.authorizationPersistence = authorizationPersistence;
    this.tokenDecoder = tokenDecoder;
    this.versionProvider = versionProvider;
    this.userPersistence = userPersistence;
    this.payPalAuthorization = payPalAuthorization;
  }

  public Billing create(String merchantPackageName) {
    if (!pool.containsKey(merchantPackageName)) {

      final BillingService billingService = billingServiceFactory.create(merchantPackageName);

      if (!adapters.containsKey(billingService)) {
        adapters.put(billingService,
            new PaymentServiceAdapter(services, billingService, authorizationPersistence));
      }

      if (!customerManagers.containsKey(billingService)) {
        customerManagers.put(billingService,
            new CustomerManager(PublishSubject.create(), userPersistence, billingService,
                authorizationPersistence, payPalAuthorization, adapters.get(billingService)));
      }

      pool.put(merchantPackageName,
          new Billing(merchantPackageName, billingService, tokenDecoder, versionProvider,
              adapters.get(billingService), PublishSubject.create(),
              customerManagers.get(billingService)));
    }

    return pool.get(merchantPackageName);
  }

  public static class Builder {

    private UserPersistence userPersistence;
    private PurchaseTokenDecoder tokenDecoder;
    private MerchantVersionProvider versionProvider;
    private Map<String, PaymentService> services;
    private AuthorizationPersistence authorizationPersistence;
    private String payPalIcon;
    private BillingServiceFactory serviceFactory;

    public Builder() {
      this.services = new HashMap<>();
    }

    public Builder setUserPersistence(UserPersistence userPersistence) {
      this.userPersistence = userPersistence;
      return this;
    }

    public Builder setPurchaseTokenDecoder(PurchaseTokenDecoder tokenDecoder) {
      this.tokenDecoder = tokenDecoder;
      return this;
    }

    public Builder setMerchantVersionProvider(MerchantVersionProvider versionProvider) {
      this.versionProvider = versionProvider;
      return this;
    }

    public Builder setAuthorizationPersistence(AuthorizationPersistence authorizationPersistence) {
      this.authorizationPersistence = authorizationPersistence;
      return this;
    }

    public Builder registerPaymentService(String type, PaymentService service) {
      services.put(type, service);
      return this;
    }

    public Builder setPayPalIcon(String payPalIcon) {
      this.payPalIcon = payPalIcon;
      return this;
    }

    public Builder setBillingServiceFactory(BillingServiceFactory serviceFactory) {
      this.serviceFactory = serviceFactory;
      return this;
    }

    public BillingFactory build() {

      if (services.isEmpty()) {
        throw new IllegalStateException("Register at least 1 payment service");
      }

      return new BillingFactory(new HashMap<>(), new HashMap<>(), new HashMap<>(), serviceFactory,
          services, authorizationPersistence, tokenDecoder, versionProvider, userPersistence,
          new PayPalAuthorization(-1, null, null, null, null, null, payPalIcon, "PayPal", true,
              Authorization.PAYPAL_SDK, null, 1));
    }
  }
}
