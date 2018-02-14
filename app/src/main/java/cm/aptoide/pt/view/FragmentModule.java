package cm.aptoide.pt.view;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import cm.aptoide.accountmanager.AptoideAccountManager;
import cm.aptoide.pt.account.AccountAnalytics;
import cm.aptoide.pt.account.ErrorsMapper;
import cm.aptoide.pt.account.view.AccountErrorMapper;
import cm.aptoide.pt.account.view.AccountNavigator;
import cm.aptoide.pt.account.view.ImagePickerNavigator;
import cm.aptoide.pt.account.view.ImagePickerPresenter;
import cm.aptoide.pt.account.view.ImagePickerView;
import cm.aptoide.pt.account.view.ImageValidator;
import cm.aptoide.pt.account.view.PhotoFileGenerator;
import cm.aptoide.pt.account.view.UriToPathResolver;
import cm.aptoide.pt.account.view.store.ManageStoreErrorMapper;
import cm.aptoide.pt.account.view.store.ManageStoreNavigator;
import cm.aptoide.pt.account.view.store.ManageStorePresenter;
import cm.aptoide.pt.account.view.store.ManageStoreView;
import cm.aptoide.pt.account.view.user.CreateUserErrorMapper;
import cm.aptoide.pt.account.view.user.ManageUserNavigator;
import cm.aptoide.pt.account.view.user.ManageUserPresenter;
import cm.aptoide.pt.account.view.user.ManageUserView;
import cm.aptoide.pt.billing.BillingAnalytics;
import cm.aptoide.pt.billing.BillingFactory;
import cm.aptoide.pt.billing.view.BillingActivity;
import cm.aptoide.pt.billing.view.BillingNavigator;
import cm.aptoide.pt.billing.view.card.CreditCardAuthorizationPresenter;
import cm.aptoide.pt.billing.view.card.CreditCardAuthorizationView;
import cm.aptoide.pt.billing.view.login.PaymentLoginPresenter;
import cm.aptoide.pt.billing.view.login.PaymentLoginView;
import cm.aptoide.pt.billing.view.payment.PaymentMethodsFragment;
import cm.aptoide.pt.billing.view.payment.PaymentMethodsPresenter;
import cm.aptoide.pt.billing.view.payment.PaymentMethodsView;
import cm.aptoide.pt.billing.view.payment.PaymentPresenter;
import cm.aptoide.pt.billing.view.payment.PaymentView;
import cm.aptoide.pt.billing.view.payment.SavedPaymentPresenter;
import cm.aptoide.pt.billing.view.payment.SavedPaymentView;
import cm.aptoide.pt.crashreports.CrashReport;
import cm.aptoide.pt.networking.image.ImageLoader;
import cm.aptoide.pt.orientation.ScreenOrientationManager;
import cm.aptoide.pt.permission.AccountPermissionProvider;
import cm.aptoide.pt.presenter.LoginSignUpCredentialsPresenter;
import cm.aptoide.pt.presenter.LoginSignUpCredentialsView;
import dagger.Module;
import dagger.Provides;
import java.util.Arrays;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

@Module public class FragmentModule {

  private final Fragment fragment;
  private final Bundle savedInstance;
  private final Bundle arguments;
  private final boolean isCreateStoreUserPrivacyEnabled;
  private final String packageName;

  public FragmentModule(Fragment fragment, Bundle savedInstance, Bundle arguments,
      boolean isCreateStoreUserPrivacyEnabled, String packageName) {
    this.fragment = fragment;
    this.savedInstance = savedInstance;
    this.arguments = arguments;
    this.isCreateStoreUserPrivacyEnabled = isCreateStoreUserPrivacyEnabled;
    this.packageName = packageName;
  }

  @Provides @FragmentScope LoginSignUpCredentialsPresenter provideLoginSignUpPresenter(
      AptoideAccountManager accountManager, AccountNavigator accountNavigator,
      AccountErrorMapper errorMapper, AccountAnalytics accountAnalytics) {
    return new LoginSignUpCredentialsPresenter((LoginSignUpCredentialsView) fragment,
        accountManager, CrashReport.getInstance(),
        arguments.getBoolean("dismiss_to_navigate_to_main_view"),
        arguments.getBoolean("clean_back_stack"), accountNavigator,
        Arrays.asList("email", "user_friends"), Arrays.asList("email"), errorMapper,
        accountAnalytics);
  }

  @Provides @FragmentScope ImagePickerPresenter provideImagePickerPresenter(
      AccountPermissionProvider accountPermissionProvider, PhotoFileGenerator photoFileGenerator,
      ImageValidator imageValidator, UriToPathResolver uriToPathResolver,
      ImagePickerNavigator imagePickerNavigator) {
    return new ImagePickerPresenter((ImagePickerView) fragment, CrashReport.getInstance(),
        accountPermissionProvider, photoFileGenerator, imageValidator,
        AndroidSchedulers.mainThread(), uriToPathResolver, imagePickerNavigator,
        fragment.getActivity()
            .getContentResolver(), ImageLoader.with(fragment.getContext()));
  }

  @Provides @FragmentScope CreditCardAuthorizationPresenter provideCreditCardAuthorizationPresenter(
      BillingFactory billingFactory, BillingAnalytics analytics, BillingNavigator navigator) {
    return new CreditCardAuthorizationPresenter((CreditCardAuthorizationView) fragment,
        billingFactory.create(arguments.getString(BillingActivity.EXTRA_MERCHANT_PACKAGE_NAME)),
        navigator, analytics, arguments.getString(BillingActivity.EXTRA_SERVICE_NAME),
        AndroidSchedulers.mainThread());
  }

  @Provides @FragmentScope PaymentLoginPresenter providePaymentLoginPresenter(
      AptoideAccountManager accountManager, AccountNavigator accountNavigator,
      BillingAnalytics billingAnalytics, BillingNavigator billingNavigator,
      AccountAnalytics accountAnalytics, AccountErrorMapper errorMapper,
      ScreenOrientationManager orientationManager) {
    return new PaymentLoginPresenter((PaymentLoginView) fragment,
        Arrays.asList("email", "user_friends"), accountNavigator, Arrays.asList("email"),
        accountManager, CrashReport.getInstance(), errorMapper, AndroidSchedulers.mainThread(),
        orientationManager, accountAnalytics, billingAnalytics, billingNavigator,
        arguments.getString(BillingActivity.EXTRA_MERCHANT_PACKAGE_NAME));
  }

  @Provides @FragmentScope PaymentMethodsPresenter providePaymentMethodsPresenter(
      BillingFactory billingFactory, BillingNavigator billingNavigator) {
    return new PaymentMethodsPresenter((PaymentMethodsView) fragment,
        billingFactory.create(arguments.getString(BillingActivity.EXTRA_MERCHANT_PACKAGE_NAME)),
        AndroidSchedulers.mainThread(), billingNavigator,
        arguments.getString(BillingActivity.EXTRA_MERCHANT_PACKAGE_NAME),
        arguments.getBoolean(PaymentMethodsFragment.CHANGE_PAYMENT_KEY, false));
  }

  @Provides @FragmentScope SavedPaymentPresenter provideSavedPaymentPresenter(
      BillingFactory billingFactory, BillingNavigator billingNavigator) {
    return new SavedPaymentPresenter((SavedPaymentView) fragment,
        billingFactory.create(arguments.getString(BillingActivity.EXTRA_MERCHANT_PACKAGE_NAME)),
        billingNavigator, AndroidSchedulers.mainThread(),
        arguments.getString(BillingActivity.EXTRA_MERCHANT_PACKAGE_NAME));
  }

  @Provides @FragmentScope PaymentPresenter providePaymentPresenter(BillingFactory billingFactory,
      BillingNavigator billingNavigator, BillingAnalytics billingAnalytics) {
    return new PaymentPresenter((PaymentView) fragment,
        billingFactory.create(arguments.getString(BillingActivity.EXTRA_MERCHANT_PACKAGE_NAME)),
        billingNavigator, billingAnalytics,
        arguments.getString(BillingActivity.EXTRA_MERCHANT_PACKAGE_NAME),
        AndroidSchedulers.mainThread());
  }

  @FragmentScope @Provides ManageStorePresenter provideManageStorePresenter(
      UriToPathResolver uriToPathResolver, ManageStoreNavigator manageStoreNavigator,
      ManageStoreErrorMapper manageStoreErrorMapper, AptoideAccountManager accountManager) {
    return new ManageStorePresenter((ManageStoreView) fragment, CrashReport.getInstance(),
        uriToPathResolver, packageName, manageStoreNavigator,
        arguments.getBoolean("go_to_home", true), manageStoreErrorMapper, accountManager, 394587);
  }

  @FragmentScope @Provides ManageUserPresenter provideManageUserPresenter(
      AptoideAccountManager accountManager, CreateUserErrorMapper errorMapper,
      ManageUserNavigator manageUserNavigator, UriToPathResolver uriToPathResolver) {
    return new ManageUserPresenter((ManageUserView) fragment, CrashReport.getInstance(),
        accountManager, errorMapper, manageUserNavigator, arguments.getBoolean("is_edit", false),
        uriToPathResolver, isCreateStoreUserPrivacyEnabled, savedInstance == null);
  }

  @FragmentScope @Provides ImageValidator provideImageValidator() {
    return new ImageValidator(ImageLoader.with(fragment.getContext()), Schedulers.computation());
  }

  @FragmentScope @Provides ManageStoreErrorMapper provideManageStoreErrorMapper() {
    return new ManageStoreErrorMapper(fragment.getResources(), new ErrorsMapper());
  }
}
