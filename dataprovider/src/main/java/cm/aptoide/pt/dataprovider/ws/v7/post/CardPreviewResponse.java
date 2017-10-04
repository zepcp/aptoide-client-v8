package cm.aptoide.pt.dataprovider.ws.v7.post;

import cm.aptoide.pt.dataprovider.model.v7.BaseV7Response;

public final class CardPreviewResponse extends BaseV7Response {
  private CardPreview data;

  public CardPreview getData() {
    return data;
  }

  public void setData(CardPreview data) {
    this.data = data;
  }

  public static class CardPreview {
    private String type;
    private TitleAndThumbnail data;

    public String getType() {
      return type;
    }

    public void setType(String type) {
      this.type = type;
    }

    public TitleAndThumbnail getData() {
      return data;
    }

    public void setData(TitleAndThumbnail data) {
      this.data = data;
    }
  }

  public static class TitleAndThumbnail {
    private String title;
    private String thumbnail;

    public String getTitle() {
      return title;
    }

    public void setTitle(String title) {
      this.title = title;
    }

    public String getThumbnail() {
      return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
      this.thumbnail = thumbnail;
    }
  }
}
