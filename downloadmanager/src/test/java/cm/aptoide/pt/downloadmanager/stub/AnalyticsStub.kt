package cm.aptoide.pt.downloadmanager.stub

import android.util.Log
import cm.aptoide.pt.downloadmanager.external.Analytics


class AnalyticsStub : Analytics {
  private val TAG: String = AnalyticsStub::class.java.name

  override fun onDownloadComplete(applicationHashCode: String?) {
    Log.d(TAG, "onDownloadComplete :: %s".format(applicationHashCode))
  }

  override fun onError(packageName: String?, versionCode: Int, throwable: Throwable?) {
    Log.d(TAG, "onError :: %s".format(packageName, versionCode))
    if (throwable != null) {
      Log.e(TAG, throwable.message)
    }
  }

}
