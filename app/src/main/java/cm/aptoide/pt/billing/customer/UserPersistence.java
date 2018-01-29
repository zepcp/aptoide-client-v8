/*
 * Copyright (c) 2017.
 * Modified by Marcelo Benites on 06/01/2017.
 */

package cm.aptoide.pt.billing.customer;

import cm.aptoide.pt.billing.customer.User;
import rx.Observable;

public interface UserPersistence {

  Observable<User> getUser();

}
