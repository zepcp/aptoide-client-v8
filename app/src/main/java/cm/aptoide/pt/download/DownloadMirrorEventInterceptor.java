package cm.aptoide.pt.download;

import cm.aptoide.pt.analytics.Analytics;
import java.io.IOException;
import okhttp3.Headers;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by marcelobenites on 20/04/17.
 */
public class DownloadMirrorEventInterceptor implements Interceptor {

  private static final String VERSION_CODE = "versioncode";
  private static final String PACKAGE = "package";
  private static final String FILE_TYPE = "fileType";

  private final Analytics analytics;

  public DownloadMirrorEventInterceptor(Analytics analytics) {
    this.analytics = analytics;
  }

  @Override public Response intercept(Chain chain) throws IOException {

    final Request request = chain.request();
    String versionCode = request.header(VERSION_CODE);
    String packageName = request.header(PACKAGE);
    int fileType = Integer.parseInt(request.header(FILE_TYPE));

    final Response response = chain.proceed(request.newBuilder()
        .removeHeader(VERSION_CODE)
        .removeHeader(PACKAGE)
        .removeHeader(FILE_TYPE)
        .build());

    if (response != null) {
      Headers allHeaders = response.headers();
      if (allHeaders != null) {
        String mirror = allHeaders.get("X-Mirror");
        addMirrorToDownloadEvent(versionCode, packageName, fileType, mirror);
      }
    }

    return response;
  }

  private void addMirrorToDownloadEvent(String v, String packageName, int fileType, String mirror) {

    final DownloadEvent event = (DownloadEvent) analytics.get(packageName + v, DownloadEvent.class);

    if (event != null) {
      if (fileType == 0) {
        event.setMirrorApk(mirror);
      } else if (fileType == 1) {
        event.setMirrorObbMain(mirror);
      } else if (fileType == 2) {
        event.setMirrorObbPatch(mirror);
      }
    }
  }
}
