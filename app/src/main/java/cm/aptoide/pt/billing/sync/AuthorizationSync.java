/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 22/11/2016.
 */

package cm.aptoide.pt.billing.sync;

import cm.aptoide.pt.billing.UserPersistence;
import cm.aptoide.pt.billing.authorization.AdyenAuthorization;
import cm.aptoide.pt.billing.authorization.AuthorizationPersistence;
import cm.aptoide.pt.billing.authorization.AuthorizationService;
import cm.aptoide.pt.billing.authorization.PayPalAuthorization;
import cm.aptoide.pt.sync.Sync;
import rx.Completable;
import rx.Observable;

public class AuthorizationSync extends Sync {

  private final UserPersistence userPersistence;
  private final AuthorizationService authorizationService;
  private final AuthorizationPersistence authorizationPersistence;

  public AuthorizationSync(String id, UserPersistence userPersistence,
      AuthorizationService authorizationService, AuthorizationPersistence authorizationPersistence,
      boolean periodic, boolean exact, long interval, long trigger) {
    super(id, periodic, exact, trigger, interval);
    this.userPersistence = userPersistence;
    this.authorizationService = authorizationService;
    this.authorizationPersistence = authorizationPersistence;
  }

  @Override public Completable execute() {
    return userPersistence.getUser()
        .first()
        .toSingle()
        .flatMapCompletable(customer -> syncAuthorizations(customer.getId()));
  }

  private Completable syncAuthorizations(String customerId) {
    return authorizationPersistence.getAuthorizations(customerId)
        .first()
        .flatMapIterable(authorizations -> authorizations)
        .publish(published -> Observable.merge(published.ofType(AdyenAuthorization.class)
            .flatMap(adyenAuthorization -> authorizationService.updateAuthorization(customerId,
                adyenAuthorization.getId(), adyenAuthorization.getPayload())
                .toObservable()), published.ofType(PayPalAuthorization.class)
            .flatMap(payPalAuthorization -> authorizationService.updateAuthorization(customerId,
                payPalAuthorization.getId(), payPalAuthorization.getPayKey())
                .toObservable())))
        .flatMapCompletable(
            authorization -> authorizationPersistence.removeAuthorization(authorization.getId()))
        .toCompletable();
  }
}