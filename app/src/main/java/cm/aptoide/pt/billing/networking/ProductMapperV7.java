/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 16/08/2016.
 */

package cm.aptoide.pt.billing.networking;

import cm.aptoide.pt.billing.product.Price;
import cm.aptoide.pt.billing.product.Product;
import cm.aptoide.pt.dataprovider.ws.v7.billing.GetProductsRequest;
import java.util.ArrayList;
import java.util.List;

public class ProductMapperV7 {

  public List<Product> map(List<GetProductsRequest.ResponseBody.Product> responseList) {

    final List<Product> products = new ArrayList<>(responseList.size());

    for (GetProductsRequest.ResponseBody.Product response : responseList) {

      products.add(map(response));
    }

    return products;
  }

  public Product map(GetProductsRequest.ResponseBody.Product response) {
    long id = response.getId();
    String sku = response.getSku();
    String icon = response.getIcon();
    String title = response.getTitle();
    String description = response.getDescription();
    Price price = new Price(response.getPrice()
        .getAmount(), response.getPrice()
        .getCurrency(), response.getPrice()
        .getSign());
    return new Product(id, icon, title, description, price, sku);
  }
}
