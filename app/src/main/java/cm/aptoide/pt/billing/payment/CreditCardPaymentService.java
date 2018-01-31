package cm.aptoide.pt.billing.payment;

import cm.aptoide.pt.billing.BillingService;
import cm.aptoide.pt.billing.PaymentService;
import cm.aptoide.pt.billing.authorization.Authorization;
import cm.aptoide.pt.billing.authorization.AuthorizationFactory;
import cm.aptoide.pt.billing.authorization.CreditCardAuthorization;
import cm.aptoide.pt.billing.customer.AuthorizationPersistence;
import cm.aptoide.pt.billing.transaction.Transaction;
import rx.Single;

public class CreditCardPaymentService implements PaymentService<CreditCard> {

  private final Adyen adyen;
  private final AuthorizationFactory authorizationFactory;

  public CreditCardPaymentService(Adyen adyen, AuthorizationFactory authorizationFactory) {
    this.adyen = adyen;
    this.authorizationFactory = authorizationFactory;
  }

  @Override
  public Single<Transaction> pay(String customerId, long productId, long authorizationId,
      BillingService billingService, String payload) {
    return billingService.createTransaction(customerId, authorizationId, productId, payload);
  }

  @Override public Single<CreditCardAuthorization> authorize(String customerId, CreditCard creditCard,
      AuthorizationPersistence authorizationPersistence, BillingService billingService,
      long paymentMethodId) {
    adyen.closeSession();
    return adyen.createSession()
        .flatMap(sessionToken -> billingService.createCreditCardAuthorization(customerId, sessionToken,
            paymentMethodId))
        .flatMap(authorization -> {

          if (authorization.isPending()) {
            return adyen.openSession(authorization.getSession())
                .andThen(adyen.registerCreditCard(creditCard))
                .flatMap(payload -> {

                  final CreditCardAuthorization cardAuthorization =
                      (CreditCardAuthorization) authorizationFactory.create(authorization.getId(),
                          authorization.getCustomerId(), authorization.getPaymentMethodId(),
                          authorization.getIcon(), authorization.getName(),
                          authorization.getDescription(), authorization.getType(),
                          authorization.isDefault(), authorization.getStatus(), payload, null, null,
                          authorization.getSession());

                  return authorizationPersistence.saveAuthorization(cardAuthorization)
                      .andThen(Single.just(cardAuthorization));
                });
          }

          return Single.just(authorization);
        });
  }
}