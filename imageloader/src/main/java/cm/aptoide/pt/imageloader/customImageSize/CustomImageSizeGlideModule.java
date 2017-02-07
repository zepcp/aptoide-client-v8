package cm.aptoide.pt.imageloader.customImageSize;

import android.content.Context;
import com.bumptech.glide.Glide;
import com.bumptech.glide.GlideBuilder;
import com.bumptech.glide.module.GlideModule;
import java.io.InputStream;

/**
 * code from https://futurestud.io/tutorials/glide-module-example-optimizing-by-loading-images-in-custom-sizes
 */
public class CustomImageSizeGlideModule implements GlideModule {

  @Override public void applyOptions(Context context, GlideBuilder builder) {
    // does nothing
  }

  @Override public void registerComponents(Context context, Glide glide) {
    glide.register(CustomImageSizeModel.class, InputStream.class, new CustomImageSizeModelFactory());
  }
}
