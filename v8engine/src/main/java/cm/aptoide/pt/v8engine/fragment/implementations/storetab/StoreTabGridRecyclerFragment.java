/*
 * Copyright (c) 2016.
 * Modified by SithEngineer on 02/08/2016.
 */

package cm.aptoide.pt.v8engine.fragment.implementations.storetab;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import cm.aptoide.pt.dataprovider.ws.v7.V7;
import cm.aptoide.pt.model.v7.Event;
import cm.aptoide.pt.model.v7.Layout;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.fragment.GridRecyclerSwipeFragment;
import cm.aptoide.pt.v8engine.fragment.implementations.MyStoresFragment;
import cm.aptoide.pt.v8engine.repository.RepositoryFactory;
import cm.aptoide.pt.v8engine.repository.StoreRepository;
import cm.aptoide.pt.v8engine.util.StoreThemeEnum;
import cm.aptoide.pt.v8engine.util.ThemeUtils;
import cm.aptoide.pt.v8engine.util.Translator;
import cm.aptoide.pt.v8engine.view.recycler.displayable.Displayable;
import java.util.List;
import rx.Observable;

/**
 * Created by neuro on 29-04-2016.
 */
public abstract class StoreTabGridRecyclerFragment extends GridRecyclerSwipeFragment {

  protected StoreRepository storeRepository;

  protected Event.Type type;
  protected Event.Name name;
  protected Layout layout;
  protected String action;
  protected String title;
  protected String tag;
  protected String storeTheme;

  public static StoreTabGridRecyclerFragment newInstance(Event event, String title,
      String storeTheme, String tag) {
    Bundle args = buildBundle(event, title, storeTheme, tag);
    StoreTabGridRecyclerFragment fragment = createFragment(event.getName());
    fragment.setArguments(args);
    return fragment;
  }

  public static StoreTabGridRecyclerFragment newInstance(Event event, String storeTheme,
      String tag) {
    return newInstance(event, null, storeTheme, tag);
  }

  @NonNull
  protected static Bundle buildBundle(Event event, String title, String storeTheme, String tag) {
    Bundle args = new Bundle();

    if (event.getType() != null) {
      args.putString(BundleCons.TYPE, event.getType().toString());
    }

    if (event.getName() != null) {
      args.putString(BundleCons.NAME, event.getName().toString());
    }

    if (event.getData() != null && event.getData().getLayout() != null) {
      args.putString(BundleCons.LAYOUT, event.getData().getLayout().toString());
    }

    args.putString(BundleCons.TITLE, title);
    args.putString(BundleCons.ACTION, event.getAction());
    args.putString(BundleCons.STORE_THEME, storeTheme);
    args.putString(BundleCons.TAG, tag);
    return args;
  }

  private static StoreTabGridRecyclerFragment createFragment(Event.Name name) {
    // TODO: 28-12-2016 neuro newInstance needed, reflection even more..
    switch (name) {
      case listApps:
        return new ListAppsFragment();
      case getStore:
        return new GetStoreFragment();
      case getStoresRecommended:
      case getMyStoresSubscribed:
        return new MyStoresSubscribedFragment();
      case myStores:
        return new MyStoresFragment();
      case getStoreWidgets:
        return new GetStoreWidgetsFragment();
      case listReviews:
        return new ListReviewsFragment();
      case getAds:
        return new GetAdsFragment();
      case listStores:
        return new ListStoresFragment();
      default:
        throw new RuntimeException("Fragment " + name + " not implemented!");
    }
  }

  public static boolean validateAcceptedName(Event.Name name) {
    if (name != null) {
      switch (name) {
        case myStores:
        case getMyStoresSubscribed:
        case getStoresRecommended:
        case listApps:
        case getStore:
        case getStoreWidgets:
        case getReviews:
          //case getApkComments:
        case getAds:
        case listStores:
        case listComments:
        case listReviews:
          return true;
      }
    }

    return false;
  }

  @Override public void onCreate(@Nullable Bundle savedInstanceState) {
    storeRepository = RepositoryFactory.getStoreRepository();

    super.onCreate(savedInstanceState);
  }

  @Override public void loadExtras(Bundle args) {
    if (args.containsKey(BundleCons.TYPE)) {
      type = Event.Type.valueOf(args.getString(BundleCons.TYPE));
    }
    if (args.containsKey(BundleCons.NAME)) {
      name = Event.Name.valueOf(args.getString(BundleCons.NAME));
    }
    if (args.containsKey(BundleCons.LAYOUT)) {
      layout = Layout.valueOf(args.getString(BundleCons.LAYOUT));
    }
    if (args.containsKey(BundleCons.TAG)) {
      tag = args.getString(BundleCons.TAG);
    }
    title = args.getString(Translator.translate(BundleCons.TITLE));
    action = args.getString(BundleCons.ACTION);
    storeTheme = args.getString(BundleCons.STORE_THEME);
  }

  @Override public void load(boolean create, boolean refresh, Bundle savedInstanceState) {
    super.load(create, refresh, savedInstanceState);
    if (create || refresh) {
      String url = action != null ? action.replace(V7.BASE_HOST, "") : null;

      if (!validateAcceptedName(name)) {
        throw new RuntimeException(
            "Invalid name(" + name + ") for event on " + getClass().getSimpleName() + "!");
      }

      // TODO: 28-12-2016 neuro martelo martelo martelo
      Observable<List<? extends Displayable>> displayablesObservable =
          buildDisplayables(refresh, url);
      if (displayablesObservable != null) {
        displayablesObservable.compose(bindUntilEvent(LifecycleEvent.DESTROY_VIEW))
            .subscribe(this::setDisplayables);
      }
    }
  }

  @Override public int getContentViewId() {
    // title flag whether toolbar should be shown or not
    if (title != null) {
      return R.layout.recycler_swipe_fragment_with_toolbar;
    } else {
      return super.getContentViewId();
    }
  }

  @Override public void setupToolbar() {
    super.setupToolbar();
    if (toolbar != null) {
      ((AppCompatActivity) getActivity()).getSupportActionBar()
          .setTitle(Translator.translate(title));
      ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
      toolbar.setLogo(R.drawable.ic_aptoide_toolbar);
    }
  }

  @Override public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
    super.onCreateOptionsMenu(menu, inflater);
    inflater.inflate(R.menu.menu_empty, menu);
  }

  @Override public boolean onOptionsItemSelected(MenuItem item) {
    if (item.getItemId() == android.R.id.home) {
      getActivity().onBackPressed();
      return true;
    }
    return super.onOptionsItemSelected(item);
  }

  @Override public void setupViews() {
    super.setupViews();
    setupToolbar();
    setHasOptionsMenu(true);
  }

  @Nullable @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    if (storeTheme != null) {
      ThemeUtils.setStoreTheme(getActivity(), storeTheme);
      ThemeUtils.setStatusBarThemeColor(getActivity(), StoreThemeEnum.get(storeTheme));
    }
    return super.onCreateView(inflater, container, savedInstanceState);
  }

  @Nullable
  protected abstract Observable<List<? extends Displayable>> buildDisplayables(boolean refresh,
      String url);

  private static class BundleCons {

    public static final String TYPE = "type";
    public static final String NAME = "name";
    public static final String TITLE = "title";
    public static final String ACTION = "action";
    public static final String STORE_THEME = "storeTheme";
    public static final String LAYOUT = "layout";
    public static final String TAG = "tag";
  }
}
