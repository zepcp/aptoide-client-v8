package cm.aptoide.pt.billing.customer;

import cm.aptoide.accountmanager.AptoideAccountManager;
import rx.Observable;

public class AccountUserPersistence implements UserPersistence {

  private final AptoideAccountManager accountManager;

  public AccountUserPersistence(AptoideAccountManager accountManager) {
    this.accountManager = accountManager;
  }

  @Override public Observable<User> getUser() {
    return accountManager.accountStatus()
        .map(account -> new User(account.getId(), account.isLoggedIn()));
  }
}
