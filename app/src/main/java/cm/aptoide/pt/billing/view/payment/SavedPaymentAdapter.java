package cm.aptoide.pt.billing.view.payment;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import cm.aptoide.pt.R;
import cm.aptoide.pt.billing.authorization.Authorization;
import java.util.List;
import rx.Observable;
import rx.subjects.PublishSubject;

/**
 * Created by jdandrade on 18/01/2018.
 */

class SavedPaymentAdapter extends RecyclerView.Adapter<AuthorizationViewHolder> {
  private final PublishSubject<Authorization> selectPaymentSubject;
  private List<Authorization> paymentMethods;

  SavedPaymentAdapter(List<Authorization> paymentMethods,
      PublishSubject<Authorization> selectPaymentSubject) {
    this.paymentMethods = paymentMethods;
    this.selectPaymentSubject = selectPaymentSubject;
  }

  @Override public AuthorizationViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
    return new AuthorizationViewHolder(LayoutInflater.from(viewGroup.getContext())
        .inflate(R.layout.payment_method_item, viewGroup, false), selectPaymentSubject);
  }

  @Override
  public void onBindViewHolder(AuthorizationViewHolder authorizationViewHolder, int position) {
    authorizationViewHolder.setPost(paymentMethods.get(position));
  }

  @Override public int getItemCount() {
    return paymentMethods.size();
  }

  Observable<Authorization> authorizationSelected() {
    return selectPaymentSubject;
  }

  void addPaymentMethods(List<Authorization> paymentMethods) {
    this.paymentMethods.addAll(paymentMethods);
    notifyDataSetChanged();
  }
}
