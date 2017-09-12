package cm.aptoide.pt.download;

import cm.aptoide.pt.analytics.Analytics;
import cm.aptoide.pt.analytics.DownloadCompleteAnalytics;
import cm.aptoide.pt.dataprovider.ws.v7.analyticsbody.Result;

/**
 * Created by trinkes on 04/01/2017.
 */

public class DownloadAnalytics implements cm.aptoide.pt.downloadmanager.external.Analytics {
  private Analytics analytics;
  private DownloadCompleteAnalytics downloadCompleteAnalytics;

  public DownloadAnalytics(Analytics analytics,
      DownloadCompleteAnalytics downloadCompleteAnalytics) {
    this.analytics = analytics;
    this.downloadCompleteAnalytics = downloadCompleteAnalytics;
  }

  @Override public void onError(String packageName, int versionCode, Throwable throwable) {
    DownloadEvent report =
        (DownloadEvent) analytics.get(packageName + versionCode, DownloadEvent.class);
    if (report != null) {
      report.setResultStatus(Result.ResultStatus.FAIL);
      report.setError(throwable);
      analytics.sendEvent(report);
    }
  }

  @Override public void onDownloadComplete(String applicationHashCode) {
    downloadCompleteAnalytics.downloadCompleted(applicationHashCode);
  }
}
