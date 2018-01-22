package cm.aptoide.pt.billing.customer;

import cm.aptoide.pt.billing.authorization.Authorization;
import cm.aptoide.pt.billing.payment.PaymentMethod;
import java.util.Collections;
import java.util.List;

public class Customer {

  private final String id;
  private final Boolean authenticated;
  private final List<PaymentMethod> paymentMethods;
  private final List<Authorization> authorizations;
  private final Authorization selectedAuthorization;
  private final PaymentMethod selectedPaymentMethod;
  private final Status status;

  public Customer(String id, boolean authenticated, List<PaymentMethod> paymentMethods,
      List<Authorization> authorizations, Authorization selectedAuthorization,
      PaymentMethod selectedPaymentMethod, Status status) {
    this.id = id;
    this.authenticated = authenticated;
    this.paymentMethods = paymentMethods;
    this.authorizations = authorizations;
    this.selectedAuthorization = selectedAuthorization;
    this.selectedPaymentMethod = selectedPaymentMethod;
    this.status = status;
  }

  public String getId() {
    return id;
  }

  public boolean isAuthenticated() {
    return authenticated;
  }

  public boolean isPaymentMethodSelected() {
    return selectedPaymentMethod != null;
  }

  public boolean isAuthorizationSelected() {
    return selectedAuthorization != null;
  }

  public List<PaymentMethod> getPaymentMethods() {
    return paymentMethods;
  }

  public List<Authorization> getAuthorizations() {
    return authorizations;
  }

  public Authorization getSelectedAuthorization() {
    return selectedAuthorization;
  }

  public PaymentMethod getSelectedPaymentMethod() {
    return selectedPaymentMethod;
  }

  public Status getStatus() {
    return status;
  }

  public static Customer loading() {
    return new Customer(null, false, Collections.emptyList(), Collections.emptyList(), null, null,
        Status.LOADING);
  }

  public static Customer error() {
    return new Customer(null, false, Collections.emptyList(), Collections.emptyList(), null, null,
        Status.LOADING_ERROR);
  }

  public static Customer authenticated(List<PaymentMethod> paymentMethods,
      List<Authorization> authorizations, Authorization selectedAuthorization,
      PaymentMethod selectedPaymentMethod, String id) {
    return new Customer(id, true, paymentMethods, authorizations, selectedAuthorization,
        selectedPaymentMethod, Status.LOADED);
  }

  public static Customer notAuthenticated() {
    return new Customer(null, false, null, null, null, null, Status.LOADED);
  }

  public static Customer withPaymentMethod(PaymentMethod selectedPaymentMethod) {
    return new Customer(null, true, null, null, null, selectedPaymentMethod, null);
  }

  public static Customer withoutPaymentMethod() {
    return new Customer(null, true, null, null, null, null, null);
  }

  public static Customer withAuthorization(PaymentMethod paymentMethod,
      Authorization authorization) {
    return new Customer(null, true, null, null, authorization, paymentMethod, null);
  }

  public static Customer consolidate(Customer oldCustomer, Customer newCustomer) {

    String id = oldCustomer.id;
    Boolean authenticated = oldCustomer.authenticated;
    List<PaymentMethod> paymentMethods = oldCustomer.paymentMethods;
    List<Authorization> authorizations = oldCustomer.authorizations;
    Status status = oldCustomer.status;

    if (newCustomer.id != null) {
      id = newCustomer.id;
    }

    if (newCustomer.authenticated != null) {
      authenticated = newCustomer.authenticated;
    }

    if (newCustomer.paymentMethods != null) {
      paymentMethods = newCustomer.paymentMethods;
    }

    if (newCustomer.authorizations != null) {
      authorizations = newCustomer.authorizations;
    }

    if (newCustomer.status != null) {
      status = newCustomer.status;
    }

    return new Customer(id, authenticated, paymentMethods, authorizations,
        newCustomer.selectedAuthorization, newCustomer.selectedPaymentMethod, status);
  }

  public enum Status {
    LOADING, LOADING_ERROR, LOADED
  }
}
