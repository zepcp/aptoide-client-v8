package cm.aptoide.pt.view;

import cm.aptoide.pt.account.view.LoginSignUpCredentialsFragment;
import cm.aptoide.pt.account.view.store.ManageStoreFragment;
import cm.aptoide.pt.account.view.user.ManageUserFragment;
import cm.aptoide.pt.addressbook.view.AddressBookFragment;
import cm.aptoide.pt.app.view.AppViewFragment;
import cm.aptoide.pt.billing.view.card.CreditCardAuthorizationFragment;
import cm.aptoide.pt.billing.view.login.PaymentLoginFragment;
import cm.aptoide.pt.billing.view.payment.PaymentFragment;
import cm.aptoide.pt.billing.view.payment.PaymentMethodsFragment;
import cm.aptoide.pt.billing.view.paypal.PayPalAuthorizationFragment;
import cm.aptoide.pt.share.NotLoggedInShareFragment;
import cm.aptoide.pt.updates.view.rollback.RollbackFragment;
import dagger.Subcomponent;

@FragmentScope @Subcomponent(modules = { FragmentModule.class })
public interface FragmentComponent {

  void inject(AddressBookFragment addressBookFragment);

  void inject(RollbackFragment rollbackFragment);

  void inject(LoginSignUpCredentialsFragment loginSignUpCredentialsFragment);

  void inject(ManageUserFragment manageUserFragment);

  void inject(ManageStoreFragment manageStoreFragment);

  void inject(CreditCardAuthorizationFragment creditCardAuthorizationFragment);

  void inject(PaymentLoginFragment paymentLoginFragment);

  void inject(AppViewFragment appViewFragment);

  void inject(PaymentMethodsFragment paymentMethodsFragment);

  void inject(PaymentFragment paymentFragment);

  void inject(PayPalAuthorizationFragment payPalAuthorizationFragment);

  void inject(NotLoggedInShareFragment notLoggedInShareFragment);
}
