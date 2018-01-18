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

  public static Customer loaded(Boolean authenticated, List<PaymentMethod> paymentMethods,
      List<Authorization> authorizations, Authorization selectedAuthorization,
      PaymentMethod selectedPaymentMethod, String id) {
    return new Customer(id, authenticated, paymentMethods, authorizations, selectedAuthorization,
        selectedPaymentMethod, Status.LOADED);
  }

  public static Customer withPaymentMethod(PaymentMethod selectedPaymentMethod) {
    return loaded(null, null, null, null, selectedPaymentMethod, null);
  }

  public static Customer consolidate(Customer oldCustomer, Customer newCustomer) {

    String id = oldCustomer.id;
    Boolean authenticated = oldCustomer.authenticated;
    List<PaymentMethod> paymentMethods = oldCustomer.paymentMethods;
    List<Authorization> authorizations = oldCustomer.authorizations;
    Authorization selectedAuthorization = oldCustomer.selectedAuthorization;
    PaymentMethod selectedPaymentMethod = oldCustomer.selectedPaymentMethod;
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

    if (newCustomer.authenticated != null) {
      authenticated = newCustomer.authenticated;
    }

    if (newCustomer.selectedAuthorization != null) {
      selectedAuthorization = newCustomer.selectedAuthorization;
    }

    if (newCustomer.selectedPaymentMethod != null) {
      selectedPaymentMethod = newCustomer.selectedPaymentMethod;
    }

    if (newCustomer.status != null) {
      status = newCustomer.status;
    }

    return new Customer(id, authenticated, paymentMethods, authorizations, selectedAuthorization,
        selectedPaymentMethod, status);
  }

  public enum Status {
    LOADING, LOADING_ERROR, LOADED
  }
}
