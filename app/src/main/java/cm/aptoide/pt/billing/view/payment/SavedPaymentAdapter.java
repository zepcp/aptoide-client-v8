package cm.aptoide.pt.billing.view.payment;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import cm.aptoide.pt.R;
import cm.aptoide.pt.billing.authorization.Authorization;
import java.util.ArrayList;
import java.util.List;
import rx.Observable;
import rx.subjects.PublishSubject;

/**
 * Created by jdandrade on 18/01/2018.
 */

class SavedPaymentAdapter extends RecyclerView.Adapter<AuthorizationViewHolder> {
  private final PublishSubject<Authorization> selectPaymentSubject;
  private final List<Authorization> selectedPayments;
  private PaymentMethodSelectedListener paymentSelectionListener;
  private List<Authorization> authorizedPaymentMethods;
  private long selectedAuthorizationId;
  private boolean multiSelectionMode;

  SavedPaymentAdapter(List<Authorization> authorizedPaymentMethods,
      PublishSubject<Authorization> selectPaymentSubject) {
    this.authorizedPaymentMethods = authorizedPaymentMethods;
    this.selectPaymentSubject = selectPaymentSubject;
    this.selectedPayments = new ArrayList<>();
    this.paymentSelectionListener = this::setSelected;
    this.selectedAuthorizationId = -1;
  }

  @Override public AuthorizationViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
    return new AuthorizationViewHolder(LayoutInflater.from(viewGroup.getContext())
        .inflate(R.layout.payment_method_item, viewGroup, false), selectPaymentSubject,
        paymentSelectionListener);
  }

  @Override
  public void onBindViewHolder(AuthorizationViewHolder authorizationViewHolder, int position) {
    Authorization authorization = authorizedPaymentMethods.get(position);
    authorizationViewHolder.setAuthorization(authorization, multiSelectionMode,
        selectedPayments.contains(authorization), selectedAuthorizationId);
  }

  @Override public int getItemCount() {
    return authorizedPaymentMethods.size();
  }

  Observable<Authorization> authorizationSelected() {
    return selectPaymentSubject.filter(authorization -> !multiSelectionMode);
  }

  List<Authorization> getPaymentMethodsForRemoval() {
    return selectedPayments;
  }

  void unsubscribeListeners() {
    paymentSelectionListener = null;
  }

  void addAuthorizedPaymentMethods(List<Authorization> authorizedPaymentMethods,
      long selectedAuthorizationId) {
    this.authorizedPaymentMethods.addAll(authorizedPaymentMethods);
    this.selectedAuthorizationId = selectedAuthorizationId;
    notifyDataSetChanged();
  }

  void multiSelectionMode(boolean multiSelection) {
    this.multiSelectionMode = multiSelection;
    notifyDataSetChanged();
  }

  void removePaymentMethods(List<Authorization> authorizations) {
    authorizedPaymentMethods.removeAll(authorizations);
    notifyDataSetChanged();
  }

  private void setSelected(Authorization authorization, boolean isChecked) {
    if (isChecked && !selectedPayments.contains(authorization)) {
      selectedPayments.add(authorization);
    } else {
      selectedPayments.remove(authorization);
    }
  }

  public void clearSelectedPayments() {
    selectedPayments.clear();
  }

}