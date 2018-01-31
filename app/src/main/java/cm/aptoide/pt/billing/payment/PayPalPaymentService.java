package cm.aptoide.pt.billing.payment;

import cm.aptoide.pt.billing.BillingService;
import cm.aptoide.pt.billing.PaymentService;
import cm.aptoide.pt.billing.authorization.Authorization;
import cm.aptoide.pt.billing.authorization.AuthorizationFactory;
import cm.aptoide.pt.billing.authorization.PayPalAuthorization;
import cm.aptoide.pt.billing.customer.AuthorizationPersistence;
import cm.aptoide.pt.billing.transaction.Transaction;
import rx.Single;

public class PayPalPaymentService implements PaymentService<PayPalResult> {

  private final AuthorizationFactory authorizationFactory;
  private final PayPalAuthorization defaultPayPalAuthorization;

  public PayPalPaymentService(AuthorizationFactory authorizationFactory,
      PayPalAuthorization defaultPayPalAuthorization) {
    this.authorizationFactory = authorizationFactory;
    this.defaultPayPalAuthorization = defaultPayPalAuthorization;
  }

  @Override
  public Single<Transaction> pay(String customerId, long productId, long authorizationId,
      BillingService billingService, String payload) {
    return billingService.createTransaction(customerId, authorizationId, productId, payload);
  }

  @Override
  public Single<PayPalAuthorization> authorize(String customerId, PayPalResult payPalResult,
      AuthorizationPersistence authorizationPersistence, BillingService billingService,
      long paymentMethodId) {

    if (payPalResult.isPending()) {
      return billingService.createPayPalAuthorization(customerId, payPalResult.getProductId(),
          paymentMethodId);
    }

    if (payPalResult.isSuccess()) {

      final PayPalAuthorization authorization =
          (PayPalAuthorization) authorizationFactory.create(payPalResult.getAuthorizationId(),
              customerId, paymentMethodId, defaultPayPalAuthorization.getIcon(),
              defaultPayPalAuthorization.getName(), defaultPayPalAuthorization.getDescription(),
              defaultPayPalAuthorization.getType(), defaultPayPalAuthorization.isDefault(),
              Authorization.Status.PROCESSING, payPalResult.getPayKey(),
              defaultPayPalAuthorization.getPrice(),
              defaultPayPalAuthorization.getProductDescription(), null);

      return authorizationPersistence.saveAuthorization(authorization)
          .andThen(Single.just(authorization));
    }

    return Single.just(
        (PayPalAuthorization) authorizationFactory.create(defaultPayPalAuthorization.getId(),
            customerId, paymentMethodId, defaultPayPalAuthorization.getIcon(),
            defaultPayPalAuthorization.getName(), defaultPayPalAuthorization.getDescription(),
            defaultPayPalAuthorization.getType(), defaultPayPalAuthorization.isDefault(),
            Authorization.Status.FAILED, defaultPayPalAuthorization.getPayKey(),
            defaultPayPalAuthorization.getPrice(),
            defaultPayPalAuthorization.getProductDescription(), null));
  }
}
