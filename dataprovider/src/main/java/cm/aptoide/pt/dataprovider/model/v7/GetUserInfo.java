package cm.aptoide.pt.dataprovider.model.v7;

/**
 * Created by pedroribeiro on 01/06/17.
 *
 * This pojo is returned by a new request to user/get with two of it's nodes (meta and settings)
 * It is called GetUserInfo because this is replacing the old getUserInfo request and pojo and a
 * GetUserRequest already existed.
 */

public class GetUserInfo extends BaseV7Response {

  private Nodes nodes;

  public Nodes getNodes() {
    return nodes;
  }

  public void setNodes(Nodes nodes) {
    this.nodes = nodes;
  }

  public static class Nodes {
    private GetUserMeta meta;
    private GetUserSettings settings;

    public GetUserMeta getMeta() {
      return meta;
    }

    public void setMeta(GetUserMeta meta) {
      this.meta = meta;
    }

    public GetUserSettings getSettings() {
      return settings;
    }

    public void setSettings(GetUserSettings settings) {
      this.settings = settings;
    }
  }
}
