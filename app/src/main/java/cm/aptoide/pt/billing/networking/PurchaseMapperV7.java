/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 25/08/2016.
 */

package cm.aptoide.pt.billing.networking;

import cm.aptoide.pt.billing.binder.BillingBinderSerializer;
import cm.aptoide.pt.billing.purchase.Purchase;
import cm.aptoide.pt.billing.purchase.PurchaseFactory;
import cm.aptoide.pt.dataprovider.ws.v7.V7;
import cm.aptoide.pt.dataprovider.ws.v7.billing.GetPurchaseRequest;
import cm.aptoide.pt.dataprovider.ws.v7.billing.GetPurchasesRequest;
import cm.aptoide.pt.dataprovider.ws.v7.billing.PurchaseResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import retrofit2.Response;
import rx.Single;

public class PurchaseMapperV7 {

  private final BillingBinderSerializer serializer;
  private final PurchaseFactory purchaseFactory;

  public PurchaseMapperV7(BillingBinderSerializer serializer, PurchaseFactory purchaseFactory) {
    this.serializer = serializer;
    this.purchaseFactory = purchaseFactory;
  }

  public Single<List<Purchase>> map(Response<GetPurchasesRequest.ResponseBody> response) {

    if (response.code() == 401 || response.code() == 403) {
      // If user not logged in return a empty purchase list.
      return Single.<List<Purchase>>just(Collections.emptyList());
    }

    if (response.isSuccessful() && response.body() != null && response.body()
        .isOk()) {
      try {

        final List<Purchase> purchases = new ArrayList<>(response.body()
            .getList()
            .size());

        for (PurchaseResponse purchaseResponse : response.body()
            .getList()) {
          purchases.add(map(purchaseResponse));
        }
        return Single.just(purchases);
      } catch (JsonProcessingException e) {
        return Single.error(e);
      }
    }

    return Single.error(new IllegalStateException(V7.getErrorMessage(response.body())));
  }

  public Single<Purchase> map(Response<GetPurchaseRequest.ResponseBody> response, long productId) {

    if (response.code() == 404) {
      return Single.just(purchaseFactory.create(productId, null, null, Purchase.Status.NEW, null));
    }

    if (response.isSuccessful() && response.body() != null && response.body()
        .isOk()) {
      try {
        return Single.just(map(response.body()
            .getData()));
      } catch (JsonProcessingException e) {
        return Single.error(e);
      }
    }

    return Single.error(new IllegalStateException(V7.getErrorMessage(response.body())));
  }

  private Purchase map(PurchaseResponse purchaseResponse) throws JsonProcessingException {
    return purchaseFactory.create(purchaseResponse.getProduct()
            .getId(), purchaseResponse.getSignature(), serializer.serializePurchase(
        purchaseResponse.getData()
            .getDeveloperPurchase()), purchaseResponse.getData()
            .getDeveloperPurchase()
            .getPurchaseState() == 0 ? Purchase.Status.COMPLETED : Purchase.Status.FAILED,
        purchaseResponse.getProduct()
            .getSku());
  }
}
