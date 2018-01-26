package cm.aptoide.pt.dataprovider.ws.v7.billing;

import android.content.SharedPreferences;
import cm.aptoide.pt.dataprovider.BuildConfig;
import cm.aptoide.pt.dataprovider.interfaces.TokenInvalidator;
import cm.aptoide.pt.dataprovider.ws.BodyInterceptor;
import cm.aptoide.pt.dataprovider.ws.v7.BaseBody;
import cm.aptoide.pt.dataprovider.ws.v7.V7;
import cm.aptoide.pt.preferences.toolbox.ToolboxManager;
import okhttp3.OkHttpClient;
import retrofit2.Converter;
import retrofit2.Response;
import rx.Observable;

public class CreateTransactionRequest
    extends V7<Response<GetTransactionRequest.ResponseBody>, CreateTransactionRequest.RequestBody> {

  private CreateTransactionRequest(RequestBody body, String baseHost, OkHttpClient httpClient,
      Converter.Factory converterFactory, BodyInterceptor bodyInterceptor,
      TokenInvalidator tokenInvalidator) {
    super(body, baseHost, httpClient, converterFactory, bodyInterceptor, tokenInvalidator);
  }

  public static String getHost(SharedPreferences sharedPreferences) {
    return (ToolboxManager.isToolboxEnableHttpScheme(sharedPreferences) ? "http"
        : BuildConfig.APTOIDE_WEB_SERVICES_SCHEME)
        + "://"
        + BuildConfig.APTOIDE_WEB_SERVICES_WRITE_V7_HOST
        + "/api/7/";
  }

  public static CreateTransactionRequest of(long productId, long authorizationId,
      BodyInterceptor<BaseBody> bodyInterceptor, OkHttpClient httpClient,
      Converter.Factory converterFactory, TokenInvalidator tokenInvalidator,
      SharedPreferences sharedPreferences, String payload) {
    final RequestBody body = new RequestBody();
    body.setProductId(productId);
    body.setAuthorizationId(authorizationId);
    body.setPayload(payload);
    return new CreateTransactionRequest(body, getHost(sharedPreferences), httpClient,
        converterFactory, bodyInterceptor, tokenInvalidator);
  }

  @Override protected Observable<Response<GetTransactionRequest.ResponseBody>> loadDataFromNetwork(
      Interfaces interfaces,
      boolean bypassCache) {
    return interfaces.createBillingTransaction(body, bypassCache);
  }

  public static class RequestBody extends BaseBody {

    private long productId;
    private long authorizationId;
    private String payload;

    public long getProductId() {
      return productId;
    }

    public void setProductId(long productId) {
      this.productId = productId;
    }

    public long getAuthorizationId() {
      return authorizationId;
    }

    public void setAuthorizationId(long authorizationId) {
      this.authorizationId = authorizationId;
    }

    public String getPayload() {
      return payload;
    }

    public void setPayload(String payload) {
      this.payload = payload;
    }
  }
}
