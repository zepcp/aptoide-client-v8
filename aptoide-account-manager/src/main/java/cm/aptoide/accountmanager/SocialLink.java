package cm.aptoide.accountmanager;

import org.parceler.Parcel;

/**
 * Created by pedroribeiro on 10/11/17.
 */

@Parcel public class SocialLink {

  public final static String FACEBOOK = "FACEBOOK";
  public final static String TWITCH = "TWITCH";
  public final static String TWITTER = "TWITTER";
  public final static String YOUTUBE = "YOUTUBE";
  public final static String BLOG = "BLOG";

  String type;
  String url;

  public SocialLink() {
  }

  public SocialLink(String type, String url) {
    this.type = type;
    this.url = url;
  }

  public String getType() {
    return type;
  }

  public String getUrl() {
    return url;
  }
}
