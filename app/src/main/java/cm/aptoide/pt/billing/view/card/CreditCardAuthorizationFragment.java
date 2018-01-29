package cm.aptoide.pt.billing.view.card;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;
import cm.aptoide.pt.AptoideApplication;
import cm.aptoide.pt.R;
import cm.aptoide.pt.analytics.ScreenTagHistory;
import cm.aptoide.pt.billing.Billing;
import cm.aptoide.pt.billing.BillingAnalytics;
import cm.aptoide.pt.billing.BillingFactory;
import cm.aptoide.pt.billing.payment.CreditCard;
import cm.aptoide.pt.billing.view.BillingActivity;
import cm.aptoide.pt.billing.view.BillingNavigator;
import cm.aptoide.pt.navigator.ActivityResultNavigator;
import cm.aptoide.pt.permission.PermissionServiceFragment;
import com.braintreepayments.cardform.view.CardForm;
import com.jakewharton.rxbinding.view.RxView;
import com.jakewharton.rxrelay.PublishRelay;
import javax.inject.Inject;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;

public class CreditCardAuthorizationFragment extends PermissionServiceFragment
    implements CreditCardAuthorizationView {

  private View progressBar;
  private ClickHandler clickHandler;
  private CardForm cardForm;
  private Button nextButton;
  private Toolbar toolbar;

  @Inject CreditCardAuthorizationPresenter presenter;

  private PublishRelay<Void> backButton;
  private PublishRelay<Void> keyboardNextRelay;

  public static CreditCardAuthorizationFragment create(Bundle bundle) {
    final CreditCardAuthorizationFragment fragment = new CreditCardAuthorizationFragment();
    fragment.setArguments(bundle);
    return fragment;
  }

  @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);

    getFragmentComponent(savedInstanceState).inject(this);

    backButton = PublishRelay.create();
    keyboardNextRelay = PublishRelay.create();

    setHasOptionsMenu(true);
    toolbar = (Toolbar) view.findViewById(R.id.fragment_credit_card_authorization_toolbar);
    ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
    ((AppCompatActivity) getActivity()).getSupportActionBar()
        .setDisplayHomeAsUpEnabled(true);

    progressBar = view.findViewById(R.id.fragment_credit_card_authorization_progress_bar);
    nextButton = (Button) view.findViewById(R.id.fragment_credit_card_authorization_next_button);
    cardForm = (CardForm) view.findViewById(R.id.fragment_credit_card_authorization_form);

    clickHandler = new ClickHandler() {
      @Override public boolean handle() {
        backButton.call(null);
        return true;
      }
    };
    registerClickHandler(clickHandler);

    cardForm.cardRequired(true)
        .expirationRequired(true)
        .cvvRequired(true)
        .postalCodeRequired(false)
        .mobileNumberRequired(false)
        .actionLabel(getString(R.string.fragment_credit_card_authorization_next_button))
        .setup(getActivity());

    showKeyboard(cardForm.getCardEditText());

    cardForm.setOnCardFormValidListener(valid -> {
      if (valid) {
        nextButton.setEnabled(true);
      } else {
        nextButton.setEnabled(false);
      }
    });

    cardForm.setOnCardFormSubmitListener(() -> {
      hideKeyboard();
      keyboardNextRelay.call(null);
    });

    attachPresenter(presenter);
  }

  @Override public ScreenTagHistory getHistoryTracker() {
    return ScreenTagHistory.Builder.build(this.getClass()
        .getSimpleName());
  }

  @Override public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    return inflater.inflate(R.layout.fragment_credit_card_authorization, container, false);
  }

  @Override public boolean onOptionsItemSelected(MenuItem item) {
    if (item.getItemId() == android.R.id.home) {
      backButton.call(null);
      return true;
    }
    return super.onOptionsItemSelected(item);
  }

  @Override public void onDestroyView() {
    unregisterClickHandler(clickHandler);
    toolbar = null;
    progressBar = null;
    nextButton = null;
    cardForm.setOnCardFormSubmitListener(null);
    cardForm.setOnCardFormValidListener(null);
    cardForm = null;
    backButton = null;
    keyboardNextRelay = null;
    super.onDestroyView();
  }

  @Override public void showNetworkError() {
    Toast.makeText(getContext(), R.string.connection_error, Toast.LENGTH_SHORT).show();
  }

  @Override public void showUnknownError() {
    Toast.makeText(getContext(), R.string.all_message_general_error, Toast.LENGTH_SHORT).show();
  }

  @Override public void showLoading() {
    progressBar.setVisibility(View.VISIBLE);
  }

  @Override public void hideLoading() {
    progressBar.setVisibility(View.GONE);
  }

  @Override public Observable<CreditCard> saveCreditCardEvent() {
    return Observable.merge(keyboardNextRelay, RxView.clicks(nextButton))
        .map(__ -> new CreditCard(cardForm.getCardNumber(), cardForm.getExpirationMonth(),
            cardForm.getExpirationYear(), cardForm.getCvv()));
  }

  @Override public Observable<Void> cancelEvent() {
    return backButton;
  }
}
