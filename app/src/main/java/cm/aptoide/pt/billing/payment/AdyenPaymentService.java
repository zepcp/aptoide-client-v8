package cm.aptoide.pt.billing.payment;

import cm.aptoide.pt.billing.PaymentService;
import cm.aptoide.pt.billing.authorization.Authorization;
import cm.aptoide.pt.billing.authorization.AuthorizationRepository;
import cm.aptoide.pt.billing.transaction.Transaction;
import cm.aptoide.pt.billing.transaction.TransactionRepository;
import rx.Single;

public class AdyenPaymentService implements PaymentService<CreditCard> {

  public static final String TYPE = "ADYEN";
  private final Adyen adyen;

  public AdyenPaymentService(Adyen adyen) {
    this.adyen = adyen;
  }

  @Override public Single<Transaction> processPayment(String customerId, String productId,
      String paymentMethodId, String payload, TransactionRepository transactionRepository) {
    return adyen.createToken()
        .flatMap(
            token -> transactionRepository.createTransaction(customerId, productId, paymentMethodId,
                payload, token));
  }

  @Override
  public Single<Authorization> createAuthorization(String customerId, String authorizationId,
      CreditCard creditCard, AuthorizationRepository authorizationRepository) {
    return authorizationRepository.updateAuthorization(customerId, authorizationId,
        creditCard.getCardNumber(),
        Authorization.Status.PENDING_SYNC);
  }
}