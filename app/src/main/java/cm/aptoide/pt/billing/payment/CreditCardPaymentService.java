package cm.aptoide.pt.billing.payment;

import cm.aptoide.pt.billing.BillingService;
import cm.aptoide.pt.billing.PaymentService;
import cm.aptoide.pt.billing.authorization.Authorization;
import cm.aptoide.pt.billing.authorization.AuthorizationPersistence;
import cm.aptoide.pt.billing.authorization.CreditCardAuthorization;
import cm.aptoide.pt.billing.transaction.Transaction;
import rx.Single;

public class CreditCardPaymentService implements PaymentService<CreditCard> {

  private final Adyen adyen;

  public CreditCardPaymentService(Adyen adyen) {
    this.adyen = adyen;
  }

  @Override
  public Single<Transaction> pay(String customerId, long productId, long authorizationId,
      BillingService billingService) {
    return billingService.createTransaction(customerId, productId, authorizationId);
  }

  @Override public Single<Authorization> authorize(String customerId, CreditCard creditCard,
      AuthorizationPersistence authorizationPersistence, BillingService billingService,
      long paymentMethodId) {
    adyen.closeSession();
    return adyen.createSession()
        .flatMap(sessionToken -> billingService.createCreditCardAuthorization(customerId, sessionToken,
            paymentMethodId))
        .flatMap(authorization -> {

          if (authorization.isPending()) {
            return updateCreditCardAuthorization(creditCard, authorizationPersistence,
                authorization.getSession(), authorization.getId(), authorization.getCustomerId(),
                authorization.getStatus(), authorization.getIcon(), authorization.getName(),
                authorization.getDescription(), authorization.isDefault(), authorization.getType(),
                authorization.getPaymentMethodId());
          }

          return Single.just(authorization);
        });
  }

  private Single<CreditCardAuthorization> updateCreditCardAuthorization(CreditCard creditCard,
      AuthorizationPersistence authorizationPersistence, String session, long id, String customerId,
      Authorization.Status status, String icon, String name, String description,
      boolean defaultAuthorization, String type, long paymentMethodId) {
    return adyen.openSession(session)
        .andThen(adyen.registerCreditCard(creditCard))
        .flatMap(payload -> {

          final CreditCardAuthorization cardAuthorization =
              new CreditCardAuthorization(id, customerId, status, session, payload, icon, name,
                  description, defaultAuthorization, type, paymentMethodId);

          return authorizationPersistence.saveAuthorization(cardAuthorization)
              .andThen(Single.just(cardAuthorization));
        });
  }
}