package cm.aptoide.pt.billing.customer;

public class User {

  private final String id;
  private final boolean authenticated;

  public User(String id, boolean authenticated) {
    this.id = id;
    this.authenticated = authenticated;
  }

  public String getId() {
    return id;
  }

  public boolean isAuthenticated() {
    return authenticated;
  }
}
