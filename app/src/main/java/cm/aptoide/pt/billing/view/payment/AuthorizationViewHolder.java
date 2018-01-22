package cm.aptoide.pt.billing.view.payment;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import cm.aptoide.pt.R;
import cm.aptoide.pt.billing.authorization.Authorization;
import cm.aptoide.pt.networking.image.ImageLoader;
import rx.subjects.PublishSubject;

/**
 * Created by jdandrade on 18/01/2018.
 */

class AuthorizationViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

  private final PublishSubject<Authorization> selectPaymentSubject;
  private final ImageView logo;
  private final TextView name;
  private final TextView description;
  private final ImageView selected;
  private final View view;
  private Authorization paymentMethod;

  public AuthorizationViewHolder(View view, PublishSubject<Authorization> selectPaymentSubject) {
    super(view);
    this.view = view;
    logo = (ImageView) view.findViewById(R.id.payment_method_logo);
    name = (TextView) view.findViewById(R.id.payment_method_name);
    description = (TextView) view.findViewById(R.id.payment_method_description);
    selected = (ImageView) view.findViewById(R.id.payment_method_selection);
    this.selectPaymentSubject = selectPaymentSubject;
  }

  public void setPost(Authorization paymentMethod) {
    this.paymentMethod = paymentMethod;
    ImageLoader.with(view.getContext())
        .load(paymentMethod.getIcon(), logo);
    name.setText(paymentMethod.getName());
    description.setText(paymentMethod.getDescription());
    if (paymentMethod.isDefault()) {
      selected.setVisibility(View.VISIBLE);
    } else {
      selected.setVisibility(View.INVISIBLE);
    }
  }

  @Override public void onClick(View v) {
    selectPaymentSubject.onNext(paymentMethod);
  }
}
