package cm.aptoide.pt.billing.persistence;

import cm.aptoide.pt.billing.authorization.AdyenAuthorization;
import cm.aptoide.pt.billing.authorization.Authorization;
import cm.aptoide.pt.billing.authorization.AuthorizationFactory;
import cm.aptoide.pt.billing.authorization.PayPalAuthorization;
import cm.aptoide.pt.database.realm.RealmAuthorization;

public class RealmAuthorizationMapper {

  private final AuthorizationFactory authorizationFactory;

  public RealmAuthorizationMapper(AuthorizationFactory authorizationFactory) {
    this.authorizationFactory = authorizationFactory;
  }

  public RealmAuthorization map(Authorization authorization) {

    String type = null;
    String metadata = null;

    if (authorization instanceof AdyenAuthorization) {
      type = Authorization.ADYEN_SDK;
      metadata = ((AdyenAuthorization) authorization).getPayload();
    }

    if (authorization instanceof PayPalAuthorization) {
      type = Authorization.PAYPAL_SDK;
      metadata = ((PayPalAuthorization) authorization).getPayKey();
    }

    if (type == null) {
      throw new IllegalArgumentException(
          "Unsupported Authorization. Can not map to RealmAuthorization");
    }

    return new RealmAuthorization(authorization.getId(), authorization.getCustomerId(),
        authorization.getStatus()
            .name(), metadata, authorization.getDescription(), type, authorization.getIcon(),
        authorization.getName());
  }

  public Authorization map(RealmAuthorization realmAuthorization) {
    return authorizationFactory.create(realmAuthorization.getId(),
        realmAuthorization.getCustomerId(), realmAuthorization.getType(),
        Authorization.Status.valueOf(realmAuthorization.getStatus()), realmAuthorization.getMetadata(),
        null, realmAuthorization.getDescription(), null, realmAuthorization.getIcon(),
        realmAuthorization.getName());
  }
}