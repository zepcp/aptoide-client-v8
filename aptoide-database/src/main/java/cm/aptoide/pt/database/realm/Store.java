/*
 * Copyright (c) 2016.
 * Modified by Neurophobic Animal on 24/05/2016.
 */

package cm.aptoide.pt.database.realm;

import io.realm.RealmObject;
import io.realm.annotations.Index;
import io.realm.annotations.PrimaryKey;

/**
 * Created on 12/05/16.
 *
 * TODO create mapper POJO <> this
 */

public class Store extends RealmObject {

  public static final String STORE_ID = "storeId";
  public static final String ICON_PATH = "iconPath";
  public static final String THEME = "theme";
  public static final String DOWNLOADS = "downloads";
  public static final String STORE_NAME = "storeName";
  public static final String USERNAME = "username";
  public static final String PASSWORD_SHA1 = "passwordSha1";
  public static final String FACEBOOK_URL = "facebookUrl";
  public static final String TWITCH_URL = "twitchUrl";
  public static final String TWITTER_URL = "twitterUrl";
  public static final String YOUTUBE_URL = "youtubeUrl";

  @PrimaryKey private long storeId;
  private String iconPath;
  private String theme;
  private long downloads;
  @Index private String storeName;
  private String username;
  private String passwordSha1;
  private String facebookUrl;
  private String twitchUrl;
  private String twitterUrl;
  private String youtubeUrl;

  public long getStoreId() {
    return storeId;
  }

  public void setStoreId(long storeId) {
    this.storeId = storeId;
  }

  public String getIconPath() {
    return iconPath;
  }

  public void setIconPath(String iconPath) {
    this.iconPath = iconPath;
  }

  public String getTheme() {
    return theme;
  }

  public void setTheme(String theme) {
    this.theme = theme;
  }

  public long getDownloads() {
    return downloads;
  }

  public void setDownloads(long downloads) {
    this.downloads = downloads;
  }

  public String getStoreName() {
    return storeName;
  }

  public void setStoreName(String storeName) {
    this.storeName = storeName;
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public String getPasswordSha1() {
    return passwordSha1;
  }

  public void setPasswordSha1(String passwordSha1) {
    this.passwordSha1 = passwordSha1;
  }

  public String getFacebookUrl() {
    return facebookUrl;
  }

  public void setFacebookUrl(String facebookUrl) {
    this.facebookUrl = facebookUrl;
  }

  public String getTwitchUrl() {
    return twitchUrl;
  }

  public void setTwitchUrl(String twitchUrl) {
    this.twitchUrl = twitchUrl;
  }

  public String getTwitterUrl() {
    return twitterUrl;
  }

  public void setTwitterUrl(String twitterUrl) {
    this.twitterUrl = twitterUrl;
  }

  public String getYoutubeUrl() {
    return youtubeUrl;
  }

  public void setYoutubeUrl(String youtubeUrl) {
    this.youtubeUrl = youtubeUrl;
  }
}
