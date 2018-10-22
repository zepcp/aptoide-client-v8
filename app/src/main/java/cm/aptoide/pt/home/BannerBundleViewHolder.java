package cm.aptoide.pt.home;

import android.view.View;
import cm.aptoide.pt.R;
import com.appnext.banners.BannerAdRequest;
import com.appnext.banners.BannerView;

class BannerBundleViewHolder extends AppBundleViewHolder {

  private final BannerView bannerView;

  public BannerBundleViewHolder(View view) {
    super(view);
    bannerView = (BannerView) view.findViewById(R.id.banner);
  }

  @Override public void setBundle(HomeBundle homeBundle, int position) {
    BannerAdRequest bannerAdRequest = new BannerAdRequest();
    bannerAdRequest.setCreativeType(BannerAdRequest.TYPE_STATIC);
    bannerView.loadAd(bannerAdRequest);
  }
}
