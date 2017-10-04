package cm.aptoide.pt.dataprovider.ws.v7;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by pedroribeiro on 28/06/17.
 */

public class BiUtmAnalyticsRequestBody extends BaseBody {

  private final Data data;

  public BiUtmAnalyticsRequestBody(Data data) {
    this.data = data;
  }

  public Data getData() {
    return data;
  }

  public static class Data {
    private String entryPoint;
    private String siteVersion;
    private App app;
    private UTM utm;
    private String userAgent;

    public String getEntryPoint() {
      return entryPoint;
    }

    public void setEntryPoint(String entryPoint) {
      this.entryPoint = entryPoint;
    }

    public String getSiteVersion() {
      return siteVersion;
    }

    public void setSiteVersion(String siteVersion) {
      this.siteVersion = siteVersion;
    }

    public App getApp() {
      return app;
    }

    public void setApp(App app) {
      this.app = app;
    }

    public UTM getUtm() {
      return utm;
    }

    public void setUtm(UTM utm) {
      this.utm = utm;
    }

    public String getUserAgent() {
      return userAgent;
    }

    public void setUserAgent(String userAgent) {
      this.userAgent = userAgent;
    }
  }

  public static class App {
    private String url;
    @JsonProperty("package") private String packageName;

    public String getUrl() {
      return url;
    }

    public void setUrl(String url) {
      this.url = url;
    }

    public String getPackageName() {
      return packageName;
    }

    public void setPackageName(String packageName) {
      this.packageName = packageName;
    }
  }

  public static class UTM {
    private String source;
    private String medium;
    private String campaign;
    private String content;

    public String getSource() {
      return source;
    }

    public void setSource(String source) {
      this.source = source;
    }

    public String getMedium() {
      return medium;
    }

    public void setMedium(String medium) {
      this.medium = medium;
    }

    public String getCampaign() {
      return campaign;
    }

    public void setCampaign(String campaign) {
      this.campaign = campaign;
    }

    public String getContent() {
      return content;
    }

    public void setContent(String content) {
      this.content = content;
    }
  }
}
