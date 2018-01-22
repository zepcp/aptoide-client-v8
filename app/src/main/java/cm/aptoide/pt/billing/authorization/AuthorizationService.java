package cm.aptoide.pt.billing.authorization;

import rx.Single;

public interface AuthorizationService {

  Single<Authorization> updateAuthorization(String customerId, String authorizationId,
      String metadata);

}
