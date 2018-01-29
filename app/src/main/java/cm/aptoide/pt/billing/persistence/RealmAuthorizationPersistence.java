/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 01/09/2016.
 */

package cm.aptoide.pt.billing.persistence;

import cm.aptoide.pt.billing.authorization.Authorization;
import cm.aptoide.pt.billing.authorization.AuthorizationPersistence;
import cm.aptoide.pt.database.accessors.Database;
import cm.aptoide.pt.database.realm.RealmAuthorization;
import io.realm.Realm;
import java.util.List;
import rx.Completable;
import rx.Scheduler;
import rx.Single;

public class RealmAuthorizationPersistence implements AuthorizationPersistence {

  private final Database database;
  private final RealmAuthorizationMapper authorizationMapper;
  private final Scheduler scheduler;

  public RealmAuthorizationPersistence(Database database,
      RealmAuthorizationMapper authorizationMapper, Scheduler scheduler) {
    this.database = database;
    this.authorizationMapper = authorizationMapper;
    this.scheduler = scheduler;
  }

  @Override public Completable saveAuthorization(Authorization authorization) {
    return Completable.fromAction(() -> database.insert(authorizationMapper.map(authorization)))
        .subscribeOn(scheduler);
  }

  @Override public Single<List<Authorization>> getAuthorizations(String customerId) {
    return Single.fromCallable(() -> getRealmAuthorizations(customerId))
        .subscribeOn(scheduler);
  }

  @Override public Completable removeAuthorization(long authorizationId) {
    return Completable.fromAction(
        () -> database.delete(RealmAuthorization.class, RealmAuthorization.ID, authorizationId))
        .subscribeOn(scheduler);
  }

  private List<Authorization> getRealmAuthorizations(String customerId) {
    final Realm realm = database.get();
    try {
      return authorizationMapper.map(realm.where(RealmAuthorization.class)
          .equalTo(RealmAuthorization.CUSTOMER_ID, customerId)
          .findAll());
    } finally {
      if (realm != null) {
        realm.close();
      }
    }
  }
}
