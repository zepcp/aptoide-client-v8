package cm.aptoide.pt.billing.persistence;

import cm.aptoide.pt.billing.authorization.Authorization;
import cm.aptoide.pt.billing.authorization.AuthorizationFactory;
import cm.aptoide.pt.billing.authorization.CreditCardAuthorization;
import cm.aptoide.pt.billing.authorization.PayPalAuthorization;
import cm.aptoide.pt.database.realm.RealmAuthorization;
import io.realm.RealmResults;
import java.util.ArrayList;
import java.util.List;

public class RealmAuthorizationMapper {

  private final AuthorizationFactory authorizationFactory;

  public RealmAuthorizationMapper(AuthorizationFactory authorizationFactory) {
    this.authorizationFactory = authorizationFactory;
  }

  public RealmAuthorization map(Authorization authorization) {

    String type = null;
    String metadata = null;

    if (authorization instanceof CreditCardAuthorization) {
      type = Authorization.ADYEN_SDK;
      metadata = ((CreditCardAuthorization) authorization).getPayload();
    }

    if (authorization instanceof PayPalAuthorization) {
      type = Authorization.PAYPAL_SDK;
      metadata = ((PayPalAuthorization) authorization).getPayKey();
    }

    if (type == null) {
      throw new IllegalArgumentException(
          "Unsupported authorization type. Can not map to RealmAuthorization");
    }

    return new RealmAuthorization(authorization.getId(), authorization.getCustomerId(), metadata,
        type, authorization.getPaymentMethodId());
  }

  public List<Authorization> map(RealmResults<RealmAuthorization> realmAuthorizations) {

    final List<Authorization> authorizations = new ArrayList<>();

    for (RealmAuthorization realmAuthorization : realmAuthorizations) {
      authorizations.add(authorizationFactory.create(realmAuthorization.getId(),
          realmAuthorization.getCustomerId(), realmAuthorization.getPaymentMethodId(), null, null,
          null, realmAuthorization.getType(), false, Authorization.Status.PROCESSING,
          realmAuthorization.getMetadata(), null, null, null));
    }
    return authorizations;
  }
}