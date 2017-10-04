package cm.aptoide.pt.dataprovider.model.v7;

/**
 * Created by pedroribeiro on 01/06/17.
 */

public class GetUserSettings extends BaseV7Response {

  private Data data;

  public Data getData() {
    return data;
  }

  public void setData(Data data) {
    this.data = data;
  }

  public static class Data {
    private boolean mature;
    private Access access;

    public boolean isMature() {
      return mature;
    }

    public void setMature(boolean mature) {
      this.mature = mature;
    }

    public Access getAccess() {
      return access;
    }

    public void setAccess(Access access) {
      this.access = access;
    }
  }

  public static class Access {
    private boolean confirmed;

    public boolean isConfirmed() {
      return confirmed;
    }

    public void setConfirmed(boolean confirmed) {
      this.confirmed = confirmed;
    }
  }
}
