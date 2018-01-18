package cm.aptoide.pt.billing.view.payment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import cm.aptoide.pt.AptoideApplication;
import cm.aptoide.pt.R;
import cm.aptoide.pt.analytics.ScreenTagHistory;
import cm.aptoide.pt.billing.Billing;
import cm.aptoide.pt.billing.BillingAnalytics;
import cm.aptoide.pt.billing.authorization.Authorization;
import cm.aptoide.pt.billing.product.Product;
import cm.aptoide.pt.billing.view.BillingActivity;
import cm.aptoide.pt.billing.view.BillingNavigator;
import cm.aptoide.pt.navigator.ActivityResultNavigator;
import cm.aptoide.pt.networking.image.ImageLoader;
import cm.aptoide.pt.permission.PermissionServiceFragment;
import cm.aptoide.pt.view.rx.RxAlertDialog;
import cm.aptoide.pt.view.spannable.SpannableFactory;
import com.bumptech.glide.Glide;
import com.jakewharton.rxbinding.view.RxView;
import com.jakewharton.rxrelay.PublishRelay;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;

public class PaymentFragment extends PermissionServiceFragment implements PaymentView {

  private View progressView;
  private ImageView productIcon;
  private TextView productName;
  private TextView merchantNameText;
  private Button buyButton;
  private TextView productPrice;
  private ClickHandler handler;
  private PublishRelay<Void> cancelRelay;

  private RxAlertDialog networkErrorDialog;
  private RxAlertDialog unknownErrorDialog;
  private SpannableFactory spannableFactory;
  private Toolbar toolbar;

  private Billing billing;
  private BillingAnalytics billingAnalytics;
  private BillingNavigator billingNavigator;
  private ImageView authorizationIcon;
  private TextView authorizationDescription;

  public static Fragment create(Bundle bundle) {
    final PaymentFragment fragment = new PaymentFragment();
    fragment.setArguments(bundle);
    return fragment;
  }

  @Override public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    billing = ((AptoideApplication) getContext().getApplicationContext()).getBilling(
        getArguments().getString(BillingActivity.EXTRA_MERCHANT_PACKAGE_NAME));
    billingAnalytics =
        ((AptoideApplication) getContext().getApplicationContext()).getBillingAnalytics();
    billingNavigator = ((ActivityResultNavigator) getContext()).getBillingNavigator();
    cancelRelay = PublishRelay.create();
  }

  @Nullable @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    return inflater.inflate(R.layout.fragment_payment, container, false);
  }

  @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    setHasOptionsMenu(true);
    toolbar = (Toolbar) view.findViewById(R.id.fragment_payment_toolbar);
    ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
    ((AppCompatActivity) getActivity()).getSupportActionBar()
        .setDisplayHomeAsUpEnabled(true);

    productIcon = (ImageView) view.findViewById(R.id.include_payment_product_icon);

    spannableFactory = new SpannableFactory();
    progressView = view.findViewById(R.id.fragment_payment_global_progress_bar);

    productIcon = (ImageView) view.findViewById(R.id.include_payment_product_icon);
    productName = (TextView) view.findViewById(R.id.fragment_payment_product_name);
    merchantNameText = (TextView) view.findViewById(R.id.payment_fragment_merchant_name);
    authorizationIcon = (ImageView) view.findViewById(R.id.fragment_payment_authorization_icon);
    authorizationDescription =
        (TextView) view.findViewById(R.id.fragment_payment_authorization_description);

    productPrice = (TextView) view.findViewById(R.id.include_payment_product_price);

    buyButton = (Button) view.findViewById(R.id.fragment_payment_buy_button);

    networkErrorDialog =
        new RxAlertDialog.Builder(getContext()).setMessage(R.string.connection_error)
            .setPositiveButton(android.R.string.ok)
            .build();
    unknownErrorDialog =
        new RxAlertDialog.Builder(getContext()).setMessage(R.string.all_message_general_error)
            .setPositiveButton(android.R.string.ok)
            .build();

    handler = () -> {
      cancelRelay.call(null);
      return true;
    };
    registerClickHandler(handler);

    attachPresenter(new PaymentPresenter(this, billing, billingNavigator, billingAnalytics,
        getArguments().getString(BillingActivity.EXTRA_MERCHANT_PACKAGE_NAME), AndroidSchedulers.mainThread()));
  }

  @Override public boolean onOptionsItemSelected(MenuItem item) {
    if (item.getItemId() == android.R.id.home) {
      cancelRelay.call(null);
      return true;
    }
    return super.onOptionsItemSelected(item);
  }

  @Override public void onDestroyView() {
    unregisterClickHandler(handler);
    spannableFactory = null;
    progressView = null;
    productIcon = null;
    productName = null;
    merchantNameText = null;
    productPrice = null;
    buyButton = null;
    networkErrorDialog.dismiss();
    networkErrorDialog = null;
    unknownErrorDialog.dismiss();
    unknownErrorDialog = null;
    super.onDestroyView();
  }

  @Override public ScreenTagHistory getHistoryTracker() {
    return ScreenTagHistory.Builder.build(this.getClass()
        .getSimpleName());
  }

  @Override public Observable<Void> cancelEvent() {
    return cancelRelay;
  }

  @Override public Observable<Void> buyEvent() {
    return RxView.clicks(buyButton);
  }

  @Override public void showLoading() {
    progressView.setVisibility(View.VISIBLE);
  }

  @Override public void showAuthorization(Authorization authorization) {

    Glide.with(this)
        .load(authorization.getIcon())
        .into(authorizationIcon);

    CharSequence description;
    if (TextUtils.isEmpty(authorization.getDescription())) {
      description = authorization.getName();
    } else {
      description = spannableFactory.createTextAppearanceSpan(getContext(),
          R.style.TextAppearance_Aptoide_Small,
          authorization.getName() + "\n" + authorization.getDescription(),
          authorization.getDescription());
    }

    authorizationDescription.setText(description);
  }

  @Override public void showProduct(Product product) {
    ImageLoader.with(getContext())
        .load(product.getIcon(), productIcon);
    productName.setText(product.getTitle());
    productPrice.setText(product.getPrice()
        .getCurrencySymbol() + " " + product.getPrice()
        .getAmount());
  }

  @Override public void showMerchant(String merchantName) {
    merchantNameText.setText(merchantName);
  }

  @Override public void hideLoading() {
    progressView.setVisibility(View.GONE);
  }

  @Override public void showNetworkError() {
    if (!networkErrorDialog.isShowing() && !unknownErrorDialog.isShowing()) {
      networkErrorDialog.show();
    }
  }

  @Override public void showUnknownError() {
    if (!networkErrorDialog.isShowing() && !unknownErrorDialog.isShowing()) {
      unknownErrorDialog.show();
    }
  }
}