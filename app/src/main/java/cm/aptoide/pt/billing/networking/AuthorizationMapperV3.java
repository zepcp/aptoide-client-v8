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

  public Authorization map(String authorizationId, String customerId, String transactionId,
      TransactionResponse transactionResponse, PaidApp paidApp, String icon, String name) {

    final String description = paidApp.getApp()
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
      return getErrorAuthorization(transactionResponse.getErrors(), authorizationId, transactionId,
          customerId, Authorization.PAYPAL_SDK, description, price, icon, name);
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
        status, null, price, description, null, icon, name);
  }

  private Authorization getErrorAuthorization(List<ErrorResponse> errors, String authorizationId,
      String transactionId, String customerId, String type, String description, Price price,
      String icon, String name) {

    Authorization authorization =
        authorizationFactory.create(authorizationId, customerId, type, Authorization.Status.FAILED,
            null, price, description, null, icon, name);

    if (errors == null || errors.isEmpty()) {
      return authorization;
    }

    final ErrorResponse error = errors.get(0);

    if ("PRODUCT-204".equals(error.code)
        || "PRODUCT-209".equals(error.code)
        || "PRODUCT-214".equals(error.code)) {
      authorization = authorizationFactory.create(authorizationId, customerId, type,
          Authorization.Status.PENDING, null, price, description, null, icon, name);
    }

    if ("PRODUCT-200".equals(error.code)) {
      authorization = authorizationFactory.create(authorizationId, customerId, type,
          Authorization.Status.ACTIVE, null, price, description, null, icon, name);
    }

    if ("PRODUCT-216".equals(error.code)) {
      authorization = authorizationFactory.create(authorizationId, customerId, type,
          Authorization.Status.PROCESSING, null, price, description, null, icon, name);
    }

    return authorization;
  }
}