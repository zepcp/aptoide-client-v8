package cm.aptoide.pt.dataprovider.ws.v7.billing;

import android.content.SharedPreferences;
import cm.aptoide.pt.dataprovider.BuildConfig;
import cm.aptoide.pt.dataprovider.interfaces.TokenInvalidator;
import cm.aptoide.pt.dataprovider.ws.BodyInterceptor;
import cm.aptoide.pt.dataprovider.ws.v7.BaseBody;
import cm.aptoide.pt.dataprovider.ws.v7.V7;
import cm.aptoide.pt.preferences.toolbox.ToolboxManager;
import com.fasterxml.jackson.annotation.JsonProperty;
import okhttp3.OkHttpClient;
import retrofit2.Converter;
import retrofit2.Response;
import rx.Observable;

public class UpdateAuthorizationRequest extends
    V7<Response<CreateAuthorizationRequest.ResponseBody>, UpdateAuthorizationRequest.RequestBody> {

  private UpdateAuthorizationRequest(RequestBody body, String baseHost, OkHttpClient httpClient,
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

  public static UpdateAuthorizationRequest of(long authorizationId, String metadata,
      SharedPreferences sharedPreferences, OkHttpClient httpClient,
      Converter.Factory converterFactory, BodyInterceptor<BaseBody> bodyInterceptorV7,
      TokenInvalidator tokenInvalidator) {
    final RequestBody requestBody = new RequestBody();
    requestBody.setAuthorizationId(authorizationId);
    final RequestBody.Data data = new RequestBody.Data();
    data.setPayKey(metadata);
    requestBody.setServiceData(data);
    return new UpdateAuthorizationRequest(requestBody, getHost(sharedPreferences), httpClient,
        converterFactory, bodyInterceptorV7, tokenInvalidator);
  }

  @Override
  protected Observable<Response<CreateAuthorizationRequest.ResponseBody>> loadDataFromNetwork(
      Interfaces interfaces,
      boolean bypassCache) {
    return interfaces.updateBillingAuthorization(body, bypassCache);
  }

  public static class RequestBody extends BaseBody {

    private long authorizationId;
    private Data serviceData;

    public Data getServiceData() {
      return serviceData;
    }

    public void setServiceData(Data serviceData) {
      this.serviceData = serviceData;
    }

    public long getAuthorizationId() {
      return authorizationId;
    }

    public void setAuthorizationId(long authorizationId) {
      this.authorizationId = authorizationId;
    }

    public static class Data {
      @JsonProperty("paykey") private String payKey;

      public String getPayKey() {
        return payKey;
      }

      public void setPayKey(String payKey) {
        this.payKey = payKey;
      }

    }
  }
}
