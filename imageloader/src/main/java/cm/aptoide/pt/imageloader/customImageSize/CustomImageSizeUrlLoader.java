package cm.aptoide.pt.imageloader.customImageSize;

import android.content.Context;
import com.bumptech.glide.load.model.stream.BaseGlideUrlLoader;

class CustomImageSizeUrlLoader extends BaseGlideUrlLoader<CustomImageSizeModel> {
  CustomImageSizeUrlLoader(Context context) {
    super(context);
  }

  @Override protected String getUrl(CustomImageSizeModel model, int width, int height) {
    return model.requestCustomSizeUrl(width, height);
  }
}
