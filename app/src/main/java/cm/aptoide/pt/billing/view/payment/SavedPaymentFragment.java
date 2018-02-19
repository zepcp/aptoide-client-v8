package cm.aptoide.pt.billing.view.payment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import cm.aptoide.pt.R;
import cm.aptoide.pt.analytics.ScreenTagHistory;
import cm.aptoide.pt.billing.authorization.Authorization;
import cm.aptoide.pt.view.BackButtonFragment;
import com.jakewharton.rxbinding.view.RxView;
import com.jakewharton.rxrelay.PublishRelay;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;
import rx.Observable;
import rx.subjects.PublishSubject;

/**
 * Created by jdandrade on 18/01/2018.
 */

public class SavedPaymentFragment extends BackButtonFragment implements SavedPaymentView {

  @Inject SavedPaymentPresenter presenter;
  private Toolbar toolbar;
  private RecyclerView list;
  private SavedPaymentAdapter adapter;
  private ClickHandler backClickHandler;
  private PublishRelay<Void> backRelay;
  private PublishSubject<Authorization> selectPaymentSubject;
  private PublishSubject<Void> deleteMenuSubject;
  private PublishSubject<List<Authorization>> removePaymentsSubject;
  private FloatingActionButton addPayment;

  public static Fragment create(Bundle bundle) {
    SavedPaymentFragment fragment = new SavedPaymentFragment();
    fragment.setArguments(bundle);
    return fragment;
  }

  @Override public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    backRelay = PublishRelay.create();
    selectPaymentSubject = PublishSubject.create();
    deleteMenuSubject = PublishSubject.create();
    removePaymentsSubject = PublishSubject.create();
    setHasOptionsMenu(true);
  }

  @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    toolbar = (Toolbar) view.findViewById(R.id.toolbar);
    list = (RecyclerView) view.findViewById(R.id.payments_list);
    addPayment = (FloatingActionButton) view.findViewById(R.id.payments_add_payment);
    setupToolbar();
    backClickHandler = () -> {
      backRelay.call(null);
      return true;
    };
    registerClickHandler(backClickHandler);
    adapter = new SavedPaymentAdapter(new ArrayList<>(), selectPaymentSubject);
    list.setLayoutManager(new LinearLayoutManager(getContext()));
    list.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));
    list.setAdapter(adapter);
    attachPresenter(presenter);
  }

  public ScreenTagHistory getHistoryTracker() {
    return ScreenTagHistory.Builder.build(this.getClass()
        .getSimpleName());
  }

  @Nullable @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    getFragmentComponent(savedInstanceState).inject(this);
    return inflater.inflate(R.layout.add_payment_fragment, container, false);
  }

  private void setupToolbar() {
    if (toolbar != null && getActivity() instanceof AppCompatActivity) {
      ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
      toolbar.setEnabled(true);
      ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
      actionBar.setDisplayHomeAsUpEnabled(true);
      actionBar.setHomeButtonEnabled(true);
      actionBar.setTitle(R.string.iab_title_payment_methods);
    }
  }

  @Override public void onDestroy() {
    list = null;
    toolbar = null;
    adapter.unsubscribeListeners();
    adapter = null;
    unregisterClickHandler(backClickHandler);
    backRelay = null;
    super.onDestroy();
  }

  @Override public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
    //feature toggle - hidding delete payment option
    //inflater.inflate(R.menu.saved_payments_menu, menu);
    super.onCreateOptionsMenu(menu, inflater);
  }

  @Override public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      case R.id.action_delete:
        deleteMenuSubject.onNext(null);
        return true;
    }
    return super.onOptionsItemSelected(item);
  }

  @Override public PublishSubject<List<Authorization>> paymentMethodsToRemove() {
    return removePaymentsSubject;
  }

  @Override public Observable<Void> actionDeleteMenuClicked() {
    return deleteMenuSubject;
  }

  @Override public Observable<Void> onBackPressed() {
    return backRelay;
  }

  @Override public Observable<Void> addPaymentClicked() {
    return RxView.clicks(addPayment);
  }

  @Override public Observable<Authorization> paymentAuthorizationSelected() {
    return adapter.authorizationSelected();
  }

  @Override public void showAuthorizedPaymentMethods(List<Authorization> authorizedPayments,
      long selectedAuthorizationId) {
    adapter.addAuthorizedPaymentMethods(authorizedPayments, selectedAuthorizationId);
  }

  @Override public void enterPaymentMethodRemovalMode() {
    AppCompatActivity activity = (AppCompatActivity) getActivity();
    activity.startSupportActionMode(new ActionMode.Callback() {
      @Override public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {
        addPayment.setVisibility(View.GONE);
        adapter.multiSelectionMode(true);
        actionMode.getMenuInflater()
            .inflate(R.menu.payments_action_mode_delete_menu, menu);
        return true;
      }

      @Override public boolean onPrepareActionMode(ActionMode actionMode, Menu menu) {
        return false;
      }

      @Override public boolean onActionItemClicked(ActionMode actionMode, MenuItem menuItem) {
        removePaymentsSubject.onNext(adapter.getPaymentMethodsForRemoval());
        adapter.clearSelectedPayments();
        actionMode.finish();
        return true;
      }

      @Override public void onDestroyActionMode(ActionMode actionMode) {
        adapter.clearSelectedPayments();
        adapter.multiSelectionMode(false);
        addPayment.setVisibility(View.VISIBLE);
      }
    });
  }

  @Override public void hidePaymentMethods(List<Authorization> authorizations) {
    adapter.removePaymentMethods(authorizations);
  }

}