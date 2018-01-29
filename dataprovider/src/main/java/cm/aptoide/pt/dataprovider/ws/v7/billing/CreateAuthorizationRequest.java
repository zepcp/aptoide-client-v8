package cm.aptoide.pt.dataprovider.ws.v7.billing;

import android.content.SharedPreferences;
import cm.aptoide.pt.dataprovider.BuildConfig;
import cm.aptoide.pt.dataprovider.interfaces.TokenInvalidator;
import cm.aptoide.pt.dataprovider.model.v7.BaseV7Response;
import cm.aptoide.pt.dataprovider.ws.BodyInterceptor;
import cm.aptoide.pt.dataprovider.ws.v7.BaseBody;
import cm.aptoide.pt.dataprovider.ws.v7.V7;
import cm.aptoide.pt.preferences.toolbox.ToolboxManager;
import com.fasterxml.jackson.annotation.JsonProperty;
import okhttp3.OkHttpClient;
import retrofit2.Converter;
import retrofit2.Response;
import rx.Observable;

public class CreateAuthorizationRequest
    extends V7<Response<CreateAuthorizationRequest.ResponseBody>, CreateAuthorizationRequest.RequestBody> {

  private CreateAuthorizationRequest(RequestBody body, String baseHost, OkHttpClient httpClient,
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

  public static CreateAuthorizationRequest ofAdyen(String token,
      SharedPreferences sharedPreferences, OkHttpClient httpClient,
      Converter.Factory converterFactory, BodyInterceptor<BaseBody> bodyInterceptorV7,
      TokenInvalidator tokenInvalidator, long paymentMethodId) {
    final RequestBody requestBody = new RequestBody();
    requestBody.setServiceId(paymentMethodId);
    final RequestBody.Data data = new RequestBody.Data();
    data.setToken(token);
    requestBody.setServiceData(data);
    return new CreateAuthorizationRequest(requestBody, getHost(sharedPreferences), httpClient,
        converterFactory, bodyInterceptorV7, tokenInvalidator);
  }

  @Override protected Observable<Response<ResponseBody>> loadDataFromNetwork(Interfaces interfaces,
      boolean bypassCache) {
    return interfaces.createBillingAuthorization(body, bypassCache);
  }

  public static class RequestBody extends BaseBody {

    @JsonProperty("service_id") private long serviceId;
    @JsonProperty("service_data") private Data serviceData;

    public Data getServiceData() {
      return serviceData;
    }

    public void setServiceData(Data serviceData) {
      this.serviceData = serviceData;
    }

    public static class Data {

      private String token;

      public String getToken() {
        return token;
      }

      public void setToken(String token) {
        this.token = token;
      }
    }

    public long getServiceId() {
      return serviceId;
    }

    public void setServiceId(long serviceId) {
      this.serviceId = serviceId;
    }
  }

  public static class ResponseBody extends BaseV7Response {

    private GetAuthorizationRequest.ResponseBody.Authorization data;

    public GetAuthorizationRequest.ResponseBody.Authorization getData() {
      return data;
    }

    public void setData(GetAuthorizationRequest.ResponseBody.Authorization data) {
      this.data = data;
    }
  }
}
