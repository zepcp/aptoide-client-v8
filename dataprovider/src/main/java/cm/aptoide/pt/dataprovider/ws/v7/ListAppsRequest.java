/*
 * Copyright (c) 2016.
 * Modified by Neurophobic Animal on 06/07/2016.
 */

package cm.aptoide.pt.dataprovider.ws.v7;

import cm.aptoide.pt.model.v7.ListApps;
import cm.aptoide.pt.model.v7.Type;
import cm.aptoide.pt.preferences.managed.ManagerPreferences;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import okhttp3.OkHttpClient;
import retrofit2.Converter;
import rx.Observable;

/**
 * Created by neuro on 27-04-2016.
 */
@Data @EqualsAndHashCode(callSuper = true) public class ListAppsRequest
    extends BaseRequestWithStore<ListApps, ListAppsRequest.Body> {

  private static final int LINES_PER_REQUEST = 6;
  private String url;

  private ListAppsRequest(String url, Body body, BodyInterceptor<BaseBody> bodyInterceptor,
      OkHttpClient httpClient, Converter.Factory converterFactory) {
    super(body, BASE_HOST, httpClient, converterFactory, bodyInterceptor);
    this.url = url;
  }

  private ListAppsRequest(Body body, OkHttpClient httpClient, Converter.Factory converterFactory,
      BodyInterceptor<BaseBody> bodyInterceptor) {
    super(body, BASE_HOST, httpClient, converterFactory, bodyInterceptor);
  }

  public static ListAppsRequest ofAction(String url,
      BaseRequestWithStore.StoreCredentials storeCredentials,
      BodyInterceptor<BaseBody> bodyInterceptor, OkHttpClient httpClient,
      Converter.Factory converterFactory) {
    V7Url listAppsV7Url = new V7Url(url).remove("listApps");
    if (listAppsV7Url.containsLimit()) {
      return new ListAppsRequest(listAppsV7Url.get(), new Body(storeCredentials), bodyInterceptor,
          httpClient, converterFactory);
    } else {
      return new ListAppsRequest(listAppsV7Url.get(),
          new Body(storeCredentials, Type.APPS_GROUP.getPerLineCount() * LINES_PER_REQUEST),
          bodyInterceptor, httpClient, converterFactory);
    }
  }

  public static ListAppsRequest of(String groupName, OkHttpClient httpClient,
      Converter.Factory converterFactory, BodyInterceptor<BaseBody> bodyInterceptor) {
    return new ListAppsRequest(new Body(groupName), httpClient, converterFactory, bodyInterceptor);
  }

  public static ListAppsRequest of(String storeName, String groupName, OkHttpClient httpClient,
      Converter.Factory converterFactory, BodyInterceptor<BaseBody> bodyInterceptor) {
    return new ListAppsRequest(new Body(storeName, groupName), httpClient, converterFactory,
        bodyInterceptor);
  }

  public static ListAppsRequest of(String storeName, String groupName, Section section,
      OkHttpClient httpClient, Converter.Factory converterFactory,
      BodyInterceptor<BaseBody> bodyInterceptor) {
    return new ListAppsRequest(new Body(storeName, groupName, section), httpClient,
        converterFactory, bodyInterceptor);
  }

  @Override
  protected Observable<ListApps> loadDataFromNetwork(Interfaces interfaces, boolean bypassCache) {
    return interfaces.listApps(url != null ? url : "", body, bypassCache);
  }

  public enum Section {
    high, main,
  }

  @EqualsAndHashCode(callSuper = true) public static class Body extends BaseBodyWithStore
      implements Endless {

    @Getter private Integer limit;
    @Getter @Setter private int offset;
    @Getter @Setter private String groupName;
    @Getter private Section section;
    @Getter private String notApkTags;

    public Body(BaseRequestWithStore.StoreCredentials storeCredentials) {
      super(storeCredentials);
      setNotApkTags();
    }

    public Body(BaseRequestWithStore.StoreCredentials storeCredentials, int limit) {
      super(storeCredentials);
      this.limit = limit;
      setNotApkTags();
    }

    public Body(String groupName) {
      this.groupName = groupName;
      setNotApkTags();
    }

    public Body(String storeName, String groupName) {
      this(new StoreCredentials(storeName));
      this.groupName = groupName;
      setNotApkTags();
    }

    public Body(String storeName, String groupName, Section section) {
      this(new StoreCredentials(storeName));
      this.groupName = groupName;
      this.section = section;
      setNotApkTags();
    }

    /**
     * Method to check not Apk Tags on this particular request
     */
    private void setNotApkTags() {
      if (ManagerPreferences.getUpdatesFilterAlphaBetaKey()) {
        this.notApkTags = "alpha,beta";
      }
    }
  }
}
