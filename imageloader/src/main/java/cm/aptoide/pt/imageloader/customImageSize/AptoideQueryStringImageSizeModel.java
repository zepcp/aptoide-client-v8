package cm.aptoide.pt.imageloader.customImageSize;

public class AptoideQueryStringImageSizeModel implements CustomImageSizeModel {

  private String baseImageUrl;

  public AptoideQueryStringImageSizeModel(String baseImageUrl) {
    this.baseImageUrl = baseImageUrl;
  }

  @Override
  public String requestCustomSizeUrl(int width, int height) {
    // previous way: we directly accessed the images
    // http://pool.img.aptoide.com/apps/84e57642751f02e71cb0eb150275f9b1_icon.png

    // new way, server could handle additional parameter and provide the image in a specific size
    // in this case, the server would serve the image in 400x300 pixel size
    // http://pool.img.aptoide.com/apps/84e57642751f02e71cb0eb150275f9b1_icon.png?w=192&h=192
    return baseImageUrl + "?w=" + width + "&h=" + height;
  }
}
