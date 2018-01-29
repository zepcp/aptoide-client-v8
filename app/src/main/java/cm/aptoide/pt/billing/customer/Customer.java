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

  public Customer(String id, Boolean authenticated, List<PaymentMethod> paymentMethods,
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
    return new Customer(null, null, null, null, null, null,
        Status.LOADING);
  }

  public static Customer error() {
    return new Customer(null, null, null, null, null, null,
        Status.LOADING_ERROR);
  }

  public static Customer authenticated(List<PaymentMethod> paymentMethods,
      List<Authorization> authorizations, Authorization selectedAuthorization,
      PaymentMethod selectedPaymentMethod, String id) {
    return new Customer(id, true, paymentMethods, authorizations, selectedAuthorization,
        selectedPaymentMethod, Status.LOADED);
  }

  public static Customer notAuthenticated() {
    return new Customer(null, false, Collections.emptyList(), Collections.emptyList(), null, null,
        Status.LOADED);
  }

  public static Customer withPaymentMethod(PaymentMethod selectedPaymentMethod) {
    return new Customer(null, null, null, null, null, selectedPaymentMethod, Status.LOADED);
  }

  public static Customer withoutPaymentMethod() {
    return new Customer(null, null, null, null, null, null, Status.LOADED);
  }

  public static Customer withAuthorization(PaymentMethod paymentMethod,
      Authorization authorization) {
    return new Customer(null, null, null, null, authorization, paymentMethod, Status.LOADED);
  }

  public static Customer consolidate(Customer oldCustomer, Customer newCustomer) {

    String id = oldCustomer.id;
    Boolean authenticated = oldCustomer.authenticated;
    List<PaymentMethod> paymentMethods = oldCustomer.paymentMethods;
    List<Authorization> authorizations = oldCustomer.authorizations;
    Authorization selectedAuthorization = oldCustomer.selectedAuthorization;
    PaymentMethod selectedPaymentMethod = oldCustomer.selectedPaymentMethod;
    Status status = newCustomer.status;

    if (newCustomer.getStatus()
        .equals(Status.LOADING_ERROR) || newCustomer.getStatus()
        .equals(Status.LOADING)) {
      return new Customer(id, authenticated, paymentMethods, authorizations,
          selectedAuthorization, selectedPaymentMethod, status);
    }

    selectedAuthorization = newCustomer.selectedAuthorization;
    selectedPaymentMethod = newCustomer.selectedPaymentMethod;

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

    if (selectedAuthorization != null && !authorizations.contains(selectedAuthorization)) {
      authorizations.add(selectedAuthorization);
    }

    return new Customer(id, authenticated, paymentMethods, authorizations,
        selectedAuthorization, selectedPaymentMethod, status);
  }

  @Override public String toString() {
    return "Customer{"
        + "id='"
        + id
        + '\''
        + ", authenticated="
        + authenticated
        + ", paymentMethods="
        + paymentMethods
        + ", authorizations="
        + authorizations
        + ", selectedAuthorization="
        + selectedAuthorization
        + ", selectedPaymentMethod="
        + selectedPaymentMethod
        + ", status="
        + status
        + '}';
  }

  public enum Status {
    LOADING, LOADING_ERROR, LOADED
  }
}
