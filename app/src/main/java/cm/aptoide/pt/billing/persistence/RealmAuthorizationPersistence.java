/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 01/09/2016.
 */

package cm.aptoide.pt.billing.persistence;

import cm.aptoide.pt.billing.authorization.Authorization;
import cm.aptoide.pt.billing.authorization.AuthorizationFactory;
import cm.aptoide.pt.billing.authorization.AuthorizationPersistence;
import cm.aptoide.pt.billing.authorization.LocalIdGenerator;
import cm.aptoide.pt.database.accessors.Database;
import cm.aptoide.pt.database.realm.RealmAuthorization;
import com.jakewharton.rxrelay.PublishRelay;
import io.realm.Realm;
import io.realm.RealmResults;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import rx.Completable;
import rx.Observable;
import rx.Scheduler;

public class RealmAuthorizationPersistence implements AuthorizationPersistence {

  private final Map<String, Authorization> authorizations;
  private final PublishRelay<List<Authorization>> authorizationRelay;
  private final Database database;
  private final RealmAuthorizationMapper authorizationMapper;
  private final Scheduler scheduler;
  private final AuthorizationFactory authorizationFactory;
  private final LocalIdGenerator idGenerator;

  public RealmAuthorizationPersistence(Map<String, Authorization> authorizations,
      PublishRelay<List<Authorization>> authorizationRelay, Database database,
      RealmAuthorizationMapper authorizationMapper, Scheduler scheduler,
      AuthorizationFactory authorizationFactory, LocalIdGenerator idGenerator) {
    this.authorizations = authorizations;
    this.authorizationRelay = authorizationRelay;
    this.database = database;
    this.authorizationMapper = authorizationMapper;
    this.scheduler = scheduler;
    this.authorizationFactory = authorizationFactory;
    this.idGenerator = idGenerator;
  }

  @Override public Completable saveAuthorization(Authorization authorization) {
    return Completable.fromAction(() -> {

      if (authorization.isPendingSync()) {
        database.insert(authorizationMapper.map(authorization));
      } else {
        authorizations.put(authorization.getId(), authorization);
      }

      authorizationRelay.call(getAuthorizations());
    })
        .subscribeOn(scheduler);
  }

  @Override public Observable<List<Authorization>> getAuthorizations(String customerId) {
    return authorizationRelay.startWith(getAuthorizations())
        .flatMap(authorizations -> Observable.from(authorizations)
            .filter(authorization -> authorization.getCustomerId()
                .equals(customerId))
            .toList())
        .subscribeOn(scheduler);
  }

  @Override public Completable removeAuthorization(String authorizationId) {
    return Completable.fromAction(
        () -> database.delete(RealmAuthorization.class, RealmAuthorization.ID, authorizationId))
        .subscribeOn(scheduler);
  }

  private List<Authorization> getAuthorizations() {

    final Map<String, Authorization> resolvedAuthorizations = new HashMap<>(authorizations);
    for (Authorization localAuthorization : getLocalAuthorization()) {
      resolvedAuthorizations.put(localAuthorization.getId(),
          resolveAuthorization(authorizations.get(localAuthorization.getId()), localAuthorization));
    }
    return new ArrayList<>(resolvedAuthorizations.values());
  }

  private Authorization resolveAuthorization(Authorization authorization,
      Authorization localAuthorization) {

    if (authorization == null) {
      return localAuthorization;
    }

    if (localAuthorization == null
        || authorization.isProcessing()
        || authorization.isActive()
        || authorization.isFailed()) {
      return authorization;
    }

    return localAuthorization;
  }

  private List<Authorization> getLocalAuthorization() {
    Realm realm = database.get();

    try {

      final RealmResults<RealmAuthorization> realmAuthorizations =
          realm.where(RealmAuthorization.class)
              .findAll();

      final List<Authorization> pendingSyncAuthorizations =
          new ArrayList<>(realmAuthorizations.size());

      for (RealmAuthorization realmAuthorization : realmAuthorizations) {
        pendingSyncAuthorizations.add(authorizationMapper.map(realmAuthorization));
      }

      return pendingSyncAuthorizations;
    } finally {
      if (realm != null) {
        realm.close();
      }
    }
  }
}
