package cm.aptoide.pt.billing.view.paypal;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import cm.aptoide.pt.R;
import cm.aptoide.pt.analytics.ScreenTagHistory;
import cm.aptoide.pt.permission.PermissionServiceFragment;
import cm.aptoide.pt.view.rx.RxAlertDialog;
import javax.inject.Inject;
import rx.Observable;

public class PayPalAuthorizationFragment extends PermissionServiceFragment implements PayPalView {

  private ProgressBar progressBar;
  private RxAlertDialog networkErrorDialog;

  @Inject PayPalAuthorizationPresenter presenter;

  public static Fragment create(Bundle bundle) {
    final PayPalAuthorizationFragment fragment = new PayPalAuthorizationFragment();
    fragment.setArguments(bundle);
    return fragment;
  }

  @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    getFragmentComponent(savedInstanceState).inject(this);

    progressBar = (ProgressBar) view.findViewById(R.id.fragment_paypal_progress_bar);

    networkErrorDialog =
        new RxAlertDialog.Builder(getContext()).setMessage(R.string.connection_error)
            .setPositiveButton(R.string.ok)
            .build();

    attachPresenter(presenter);
  }

  @Override public ScreenTagHistory getHistoryTracker() {
    return ScreenTagHistory.Builder.build(this.getClass()
        .getSimpleName());
  }

  @Nullable @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    return inflater.inflate(R.layout.fragment_paypal_authorization, container, false);
  }

  @Override public void onDestroyView() {
    progressBar = null;
    networkErrorDialog.dismiss();
    networkErrorDialog = null;
    super.onDestroyView();
  }

  @Override public void showLoading() {
    progressBar.setVisibility(View.VISIBLE);
  }

  @Override public void hideLoading() {
    progressBar.setVisibility(View.GONE);
  }

  @Override public void showNetworkError() {
    if (!networkErrorDialog.isShowing()) {
      networkErrorDialog.show();
    }
  }

  @Override public Observable<Void> errorDismisses() {
    return networkErrorDialog.dismisses()
        .map(dialogInterface -> null);
  }
}
