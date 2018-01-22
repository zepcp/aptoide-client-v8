package cm.aptoide.pt.billing.authorization;

import java.util.List;
import rx.Completable;
import rx.Observable;
import rx.Single;

public interface AuthorizationPersistence {

  Observable<List<Authorization>> getAuthorizations(String customerId);

  Completable saveAuthorization(Authorization authorization);

  Completable removeAuthorization(String authorizationId);
}
