package cm.aptoide.pt.billing;

import cm.aptoide.pt.billing.authorization.Authorization;
import cm.aptoide.pt.billing.authorization.CreditCardAuthorization;
import cm.aptoide.pt.billing.authorization.PayPalAuthorization;
import cm.aptoide.pt.billing.payment.PaymentMethod;
import cm.aptoide.pt.billing.product.Product;
import cm.aptoide.pt.billing.purchase.Purchase;
import cm.aptoide.pt.billing.transaction.Transaction;
import java.util.List;
import rx.Completable;
import rx.Single;

public interface BillingService {

  Single<List<PaymentMethod>> getPaymentMethods();

  Single<Merchant> getMerchant(String packageName, int versionCode);

  Completable deletePurchase(long purchaseId);

  Single<List<Purchase>> getPurchases(String merchantName);

  Single<Purchase> getPurchase(long productId);

  Single<List<Product>> getProducts(String merchantName, List<String> skus);

  Single<Product> getProduct(String sku, String merchantName);

  Single<List<Authorization>> getAuthorizations(String customerId);

  Single<PayPalAuthorization> updatePayPalAuthorization(String customerId, long transactionId,
      String payKey, long paymentMethodId, long authorizationId);

  Single<PayPalAuthorization> createPayPalAuthorization(String customerId, String token);

  Single<CreditCardAuthorization> updateCreditCardAuthorization(String customerId,
      long authorizationId, String metadata, long paymentMethodId);

  Single<CreditCardAuthorization> createCreditCardAuthorization(String customerId, String token,
      long paymentMethodId);

  Single<Transaction> getTransaction(String customerId, long productId);

  Single<Transaction> createTransaction(String customerId, long productId,
      long authorizationId);
}