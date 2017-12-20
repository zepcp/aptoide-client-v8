package cm.aptoide.accountmanager;

import java.util.Collections;
import java.util.List;

public class Store {
  private final long id;
  private final String name;
  private final String avatar;
  private final long downloadCount;
  private final String theme;
  private final String username;
  private final String password;
  private final boolean publicAccess;
  private final List<SocialLink> socialLinkList;

  public Store(long downloadCount, String avatar, long id, String name, String theme,
      String username, String password, boolean publicAccess, List<SocialLink> socialLinkList) {
    this.downloadCount = downloadCount;
    this.avatar = avatar;
    this.id = id;
    this.name = name;
    this.theme = theme;
    this.username = username;
    this.password = password;
    this.publicAccess = publicAccess;
    this.socialLinkList = socialLinkList;
  }

  private Store() {
    this.downloadCount = 0;
    this.avatar = "";
    this.id = 0;
    this.name = "";
    this.theme = "DEFAULT";
    this.username = "";
    this.password = "";
    this.publicAccess = true;
    this.socialLinkList = Collections.emptyList();
  }

  public static Store emptyStore() {
    return new Store();
  }

  public String getUsername() {
    return username;
  }

  public String getPassword() {
    return password;
  }

  public long getDownloadCount() {
    return downloadCount;
  }

  public String getAvatar() {
    return avatar;
  }

  public long getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public String getTheme() {
    return theme;
  }

  public boolean hasPublicAccess() {
    return publicAccess;
  }

  public List<SocialLink> getSocialLinkList() {
    return socialLinkList;
  }
}
