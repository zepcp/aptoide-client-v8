package cm.aptoide.pt.billing.view.payment;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.CheckBox;
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
  private final PaymentMethodSelectedListener paymentSelectionListener;
  private final ImageView logo;
  private final TextView name;
  private final TextView description;
  private final ImageView defaultPaymentIcon;
  private final View view;
  private final CheckBox selectionBox;
  private Authorization paymentMethod;
  private boolean multiSelectionMode;

  public AuthorizationViewHolder(View view, PublishSubject<Authorization> selectPaymentSubject,
      PaymentMethodSelectedListener paymentSelectionListener) {
    super(view);
    this.view = view;
    this.selectPaymentSubject = selectPaymentSubject;
    this.paymentSelectionListener = paymentSelectionListener;
    logo = (ImageView) view.findViewById(R.id.payment_method_logo);
    name = (TextView) view.findViewById(R.id.payment_method_name);
    description = (TextView) view.findViewById(R.id.payment_method_description);
    defaultPaymentIcon = (ImageView) view.findViewById(R.id.payment_method_default);
    selectionBox = (CheckBox) view.findViewById(R.id.payment_method_selection);
  }

  public void setAuthorization(Authorization paymentMethod, boolean multiSelectionMode,
      boolean isChecked) {
    this.paymentMethod = paymentMethod;
    this.multiSelectionMode = multiSelectionMode;
    ImageLoader.with(view.getContext())
        .load(paymentMethod.getIcon(), logo);
    name.setText(paymentMethod.getName());
    description.setText(paymentMethod.getDescription());

    setupDefaultPayment(paymentMethod);

    setupSelectionMode(isChecked);
  }

  private void setupSelectionMode(boolean isChecked) {
    if (multiSelectionMode) {
      selectionBox.setChecked(isChecked);
      selectionBox.setVisibility(View.VISIBLE);
      selectionBox.setOnCheckedChangeListener(
          (buttonView, checked) -> paymentSelectionListener.onCheck(paymentMethod, checked));
    } else {
      selectionBox.setVisibility(View.GONE);
    }
  }

  private void setupDefaultPayment(Authorization paymentMethod) {
    if (paymentMethod.isDefault() && !multiSelectionMode) {
      defaultPaymentIcon.setVisibility(View.VISIBLE);
    } else {
      defaultPaymentIcon.setVisibility(View.GONE);
    }
  }

  @Override public void onClick(View v) {
    selectPaymentSubject.onNext(paymentMethod);
  }
}
