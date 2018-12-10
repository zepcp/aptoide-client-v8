package cm.aptoide.pt.dataprovider.model.v7;

import com.fasterxml.jackson.annotation.JsonProperty;

public class AppCoinsCampaign {
  private String uid;
  @JsonProperty("package") private String packageName;
  private String label;
  private String icon;
  private Integer downloads;
  private Rating rating;
  private Campaign campaign;
  private Apk apk;

  public AppCoinsCampaign() {
  }

  public String getUid() {
    return uid;
  }

  public void setUid(String uid) {
    this.uid = uid;
  }

  public String getPackageName() {
    return packageName;
  }

  public void setPackageName(String packageName) {
    this.packageName = packageName;
  }

  public String getLabel() {
    return label;
  }

  public void setLabel(String label) {
    this.label = label;
  }

  public Integer getDownloads() {
    return downloads;
  }

  public void setDownloads(Integer downloads) {
    this.downloads = downloads;
  }

  public Rating getRating() {
    return rating;
  }

  public void setRating(Rating rating) {
    this.rating = rating;
  }

  public Campaign getCampaign() {
    return campaign;
  }

  public void setCampaign(Campaign campaign) {
    this.campaign = campaign;
  }

  public Apk getApk() {
    return apk;
  }

  public void setApk(Apk apk) {
    this.apk = apk;
  }

  public String getIcon() {
    return icon;
  }

  public void setIcon(String icon) {
    this.icon = icon;
  }

  public int hashCode() {
    final int PRIME = 59;
    int result = 1;
    final Object $uid = this.getUid();
    result = result * PRIME + ($uid == null ? 43 : $uid.hashCode());
    final Object $packageName = this.getPackageName();
    result = result * PRIME + ($packageName == null ? 43 : $packageName.hashCode());
    final Object $label = this.getLabel();
    result = result * PRIME + ($label == null ? 43 : $label.hashCode());
    final Object $icon = this.getIcon();
    result = result * PRIME + ($icon == null ? 43 : $icon.hashCode());
    final Object $downloads = this.getDownloads();
    result = result * PRIME + ($downloads == null ? 43 : $downloads.hashCode());
    final Object $rating = this.getRating();
    result = result * PRIME + ($rating == null ? 43 : $rating.hashCode());
    final Object $campaign = this.getCampaign();
    result = result * PRIME + ($campaign == null ? 43 : $campaign.hashCode());
    final Object $apk = this.getApk();
    result = result * PRIME + ($apk == null ? 43 : $apk.hashCode());
    return result;
  }

  protected boolean canEqual(Object other) {
    return other instanceof GetAppMeta.App;
  }

  public static class Campaign {
    private Urls urls;

    public Urls getUrls() {
      return urls;
    }

    public void setUrls(Urls urls) {
      this.urls = urls;
    }

    protected boolean canEqual(Object other) {
      return other instanceof Urls;
    }

    public int hashCode() {
      final int PRIME = 59;
      int result = 1;
      final Object $urls = this.getUrls();
      result = result * PRIME + ($urls == null ? 43 : $urls.hashCode());
      return result;
    }

    public boolean equals(Object o) {
      if (o == this) return true;
      if (!(o instanceof Campaign)) return false;
      final Campaign other = (Campaign) o;
      if (!other.canEqual((Object) this)) return false;
      final Object this$urls = this.getUrls();
      final Object other$urls = other.getUrls();
      if (this$urls == null ? other$urls != null : !this$urls.equals(other$urls)) return false;
      return true;
    }

    public String toString() {
      return "AppCoinsCampaign.Campaign(UrlClick="
          + urls.getClick()
          + ", UrlInstall="
          + urls.getInstall()
          + ")";
    }
  }

  public boolean equals(Object o) {
    if (o == this) return true;
    if (!(o instanceof AppCoinsCampaign)) return false;
    final AppCoinsCampaign other = (AppCoinsCampaign) o;
    if (!other.canEqual((Object) this)) return false;

    final Object this$uid = this.getUid();
    final Object other$uid = other.getUid();
    if (this$uid == null ? other$uid != null : !this$uid.equals(other$uid)) return false;

    final Object this$packageName = this.getPackageName();
    final Object other$packageName = other.getPackageName();
    if (this$packageName == null ? other$packageName != null
        : !this$packageName.equals(other$packageName)) {
      return false;
    }
    final Object this$icon = this.getIcon();
    final Object other$icon = other.getIcon();
    if (this$icon == null ? other$icon != null : !this$icon.equals(other$icon)) return false;

    if (this.getDownloads() != other.getDownloads()) return false;

    final Object this$rating = this.getRating();
    final Object other$rating = other.getRating();
    if (this$rating == null ? other$rating != null : !this$rating.equals(other$rating)) {
      return false;
    }

    final Object this$campaign = this.getCampaign();
    final Object other$campaign = other.getCampaign();
    if (this$campaign == null ? other$campaign != null : !this$campaign.equals(other$campaign)) {
      return false;
    }
    final Object this$apk = this.getApk();
    final Object other$apk = other.getApk();
    if (this$apk == null ? other$apk != null : !this$apk.equals(other$apk)) {
      return false;
    }

    return true;
  }

  public static class Apk {
    private Version version;

    public Version getVersion() {
      return version;
    }

    public void setVersion(Version version) {
      this.version = version;
    }

    protected boolean canEqual(Object other) {
      return other instanceof GetAppMeta.Stats.Rating;
    }

    public boolean equals(Object o) {
      if (o == this) return true;
      if (!(o instanceof Apk)) return false;
      final Apk other = (Apk) o;
      if (!other.canEqual((Object) this)) return false;
      if (this.getVersion() != other.getVersion()) return false;
      return true;
    }

    public int hashCode() {
      final int PRIME = 59;
      int result = 1;
      result = result * PRIME + (this.getVersion() == null ? 43 : this.getVersion()
          .hashCode());
      return result;
    }

    public String toString() {
      return "AppCoinsCampaign.Apk(Version.name=" + version.getName() + ")";
    }
  }

  public static class Version {
    private String name;

    public String getName() {
      return name;
    }

    public void setName(String name) {
      this.name = name;
    }

    protected boolean canEqual(Object other) {
      return other instanceof GetAppMeta.Stats.Rating;
    }

    public boolean equals(Object o) {
      if (o == this) return true;
      if (!(o instanceof Version)) return false;
      final Version other = (Version) o;
      if (!other.canEqual((Object) this)) return false;
      if (!this.getName()
          .equals(other.getName())) {
        return false;
      }
      return true;
    }

    public int hashCode() {
      final int PRIME = 59;
      int result = 1;
      result = result * PRIME + (this.getName() == null ? 43 : this.getName()
          .hashCode());
      return result;
    }

    public String toString() {
      return "AppCoinsCampaign.Apk.Version(Name=" + name + ")";
    }
  }

  public static class Rating {
    private float average;
    private int total;

    public float getAverage() {
      return average;
    }

    public void setAverage(float average) {
      this.average = average;
    }

    public int getTotal() {
      return total;
    }

    public void setTotal(int total) {
      this.total = total;
    }

    protected boolean canEqual(Object other) {
      return other instanceof GetAppMeta.Stats.Rating;
    }

    public boolean equals(Object o) {
      if (o == this) return true;
      if (!(o instanceof Rating)) return false;
      final Rating other = (Rating) o;
      if (!other.canEqual((Object) this)) return false;
      if (Float.compare(this.getAverage(), other.getAverage()) != 0) return false;
      if (this.getTotal() != other.getTotal()) return false;
      return true;
    }

    public int hashCode() {
      final int PRIME = 59;
      int result = 1;
      result = result * PRIME + Float.floatToIntBits(this.getAverage());
      result = result * PRIME + this.getTotal();
      return result;
    }

    public String toString() {
      return "AppCoinsCampaign.Rating(avg="
          + this.getAverage()
          + ", total="
          + this.getTotal()
          + ")";
    }
  }

  public static class Urls {

    private String click;
    private String install;

    public Urls() {
    }

    public String getClick() {
      return click;
    }

    public void setClick(String click) {
      this.click = click;
    }

    public String getInstall() {
      return install;
    }

    public void setInstall(String install) {
      this.install = install;
    }

    protected boolean canEqual(Object other) {
      return other instanceof Urls;
    }

    public int hashCode() {
      final int PRIME = 59;
      int result = 1;
      final Object $w = this.getClick();
      result = result * PRIME + ($w == null ? 43 : $w.hashCode());
      final Object $m = this.getInstall();
      result = result * PRIME + ($m == null ? 43 : $m.hashCode());
      return result;
    }

    public boolean equals(Object o) {
      if (o == this) return true;
      if (!(o instanceof Urls)) return false;
      final Urls other = (Urls) o;
      if (!other.canEqual((Object) this)) return false;
      final Object this$click = this.getClick();
      final Object other$click = other.getClick();
      if (this$click == null ? other$click != null : !this$click.equals(other$click)) return false;
      final Object this$install = this.getInstall();
      final Object other$install = other.getInstall();
      if (this$install == null ? other$install != null : !this$install.equals(other$install)) {
        return false;
      }
      return true;
    }

    public String toString() {
      return "AppCoinsCampaign.Urls(w=" + this.getClick() + ", m=" + this.getInstall() + ")";
    }
  }




}
