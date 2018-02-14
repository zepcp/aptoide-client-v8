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

class AuthorizationViewHolder extends RecyclerView.ViewHolder {

  private final PublishSubject<Authorization> selectPaymentSubject;
  private final PaymentMethodSelectedListener paymentSelectionListener;
  private final ImageView logo;
  private final TextView name;
  private final TextView description;
  private final ImageView defaultPaymentIcon;
  private final View view;
  private final CheckBox selectionBox;
  private boolean multiSelectionMode;

  public AuthorizationViewHolder(View view, PublishSubject<Authorization> selectPaymentSubject,
      PaymentMethodSelectedListener paymentSelectionListener) {
    super(view);
    this.view = view;
    this.selectPaymentSubject = selectPaymentSubject;
    this.paymentSelectionListener = paymentSelectionListener;
    this.logo = (ImageView) view.findViewById(R.id.payment_method_logo);
    this.name = (TextView) view.findViewById(R.id.payment_method_name);
    this.description = (TextView) view.findViewById(R.id.payment_method_description);
    this.defaultPaymentIcon = (ImageView) view.findViewById(R.id.payment_method_default);
    this.selectionBox = (CheckBox) view.findViewById(R.id.payment_method_selection);
  }

  public void setAuthorization(Authorization authorizedPaymentMethod, boolean multiSelectionMode,
      boolean isChecked) {
    this.multiSelectionMode = multiSelectionMode;
    ImageLoader.with(view.getContext())
        .load(authorizedPaymentMethod.getIcon(), logo);
    this.name.setText(authorizedPaymentMethod.getName());
    this.description.setText(authorizedPaymentMethod.getDescription());

    //feature toggle
    //setupDefaultPayment(authorizedPaymentMethod);

    setupSelectionMode(authorizedPaymentMethod, isChecked);
  }

  private void setupSelectionMode(Authorization authorizedPaymentMethod, boolean isChecked) {
    itemView.setOnClickListener(view -> selectPaymentSubject.onNext(authorizedPaymentMethod));
    if (multiSelectionMode) {
      selectionBox.setChecked(isChecked);
      selectionBox.setVisibility(View.VISIBLE);
      selectionBox.setOnCheckedChangeListener(
          (buttonView, checked) -> paymentSelectionListener.onCheck(authorizedPaymentMethod,
              checked));
    } else {
      selectionBox.setVisibility(View.GONE);
    }
  }

  private void setupDefaultPayment(Authorization authorizedPaymentMethod) {
    if (authorizedPaymentMethod.isDefault() && !multiSelectionMode) {
      defaultPaymentIcon.setVisibility(View.VISIBLE);
    } else {
      defaultPaymentIcon.setVisibility(View.GONE);
    }
  }
}