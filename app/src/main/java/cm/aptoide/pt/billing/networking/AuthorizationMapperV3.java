/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 16/12/2016.
 */

package cm.aptoide.pt.billing.networking;

import cm.aptoide.pt.billing.Price;
import cm.aptoide.pt.billing.authorization.Authorization;
import cm.aptoide.pt.billing.authorization.AuthorizationFactory;
import cm.aptoide.pt.dataprovider.model.v3.ErrorResponse;
import cm.aptoide.pt.dataprovider.model.v3.PaidApp;
import cm.aptoide.pt.dataprovider.model.v3.PaymentServiceResponse;
import cm.aptoide.pt.dataprovider.model.v3.TransactionResponse;
import java.util.List;

public class AuthorizationMapperV3 {

  private final AuthorizationFactory authorizationFactory;

  public AuthorizationMapperV3(AuthorizationFactory authorizationFactory) {
    this.authorizationFactory = authorizationFactory;
  }

  public Authorization map(String customerId, long authorizationId, long transactionId,
      PaidApp paidApp, String icon, String name, TransactionResponse transactionResponse) {

    final String productDescription = paidApp.getApp()
        .getName();

    final List<PaymentServiceResponse> paymentServiceResponses = paidApp.getPayment()
        .getPaymentServices();

    Price price = null;
    if (paymentServiceResponses != null && !paymentServiceResponses.isEmpty()) {
      final PaymentServiceResponse paymentServiceResponse = paymentServiceResponses.get(0);
      price = new Price(paymentServiceResponse.getPrice(), paymentServiceResponse.getCurrency(),
          paymentServiceResponse.getSign());
    }

    if (transactionResponse.hasErrors()) {
      return getErrorAuthorization(customerId, 1, authorizationId, transactionId,
          Authorization.PAYPAL_SDK, name, icon, price, productDescription,
          transactionResponse.getErrors());
    }

    Authorization.Status status;
    switch (transactionResponse.getTransactionStatus()) {
      case "COMPLETED":
        status = Authorization.Status.REDEEMED;
        break;
      case "PENDING_USER_AUTHORIZATION":
      case "CREATED":
        status = Authorization.Status.PENDING;
        break;
      case "PROCESSING":
      case "PENDING":
        status = Authorization.Status.PROCESSING;
        break;
      case "FAILED":
      case "CANCELED":
      default:
        status = Authorization.Status.FAILED;
    }

    return authorizationFactory.create(authorizationId, customerId, Authorization.PAYPAL_SDK,
        status, null, price, productDescription, null, icon, name, null, true,
        transactionResponse.getServiceId(), transactionId);
  }

  private Authorization getErrorAuthorization(String customerId, long paymentMethodId,
      long authorizationId, long transactionId, String type, String name, String icon, Price price,
      String productDescription, List<ErrorResponse> errors) {

    Authorization authorization =
        authorizationFactory.create(authorizationId, customerId, type, Authorization.Status.FAILED,
            null, price, productDescription, null, icon, name, null, true, paymentMethodId,
            transactionId);

    if (errors == null || errors.isEmpty()) {
      return authorization;
    }

    final ErrorResponse error = errors.get(0);

    if ("PRODUCT-204".equals(error.code)
        || "PRODUCT-209".equals(error.code)
        || "PRODUCT-214".equals(error.code)) {
      authorization = authorizationFactory.create(authorizationId, customerId, type,
          Authorization.Status.PENDING, null, price, productDescription, null, icon, name, null,
          true, paymentMethodId, transactionId);
    }

    if ("PRODUCT-200".equals(error.code)) {
      authorization = authorizationFactory.create(authorizationId, customerId, type,
          Authorization.Status.ACTIVE, null, price, productDescription, null, icon, name, null,
          true, paymentMethodId, transactionId);
    }

    if ("PRODUCT-216".equals(error.code)) {
      authorization = authorizationFactory.create(authorizationId, customerId, type,
          Authorization.Status.PROCESSING, null, price, productDescription, null, icon, name, null,
          true, paymentMethodId, transactionId);
    }

    return authorization;
  }
}