package cm.aptoide.pt.home;

import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import cm.aptoide.pt.R;
import cm.aptoide.pt.networking.image.ImageLoader;
import cm.aptoide.pt.view.Translator;
import rx.subjects.PublishSubject;

class WalletAdsOfferViewHolder extends AppBundleViewHolder {

  private final PublishSubject<HomeEvent> uiEventsListener;
  private final Button dismissButton;
  private final Button installWalletButton;
  private final ImageView icon;
  private final TextView title;
  private final TextView message;

  public WalletAdsOfferViewHolder(View view, PublishSubject<HomeEvent> uiEventsListener) {
    super(view);
    this.uiEventsListener = uiEventsListener;
    this.installWalletButton = view.findViewById(R.id.dismiss_button);
    this.dismissButton = view.findViewById(R.id.know_more_button);
    this.icon = view.findViewById(R.id.icon);
    this.title = view.findViewById(R.id.title);
    this.message = view.findViewById(R.id.message);
  }

  @Override public void setBundle(HomeBundle homeBundle, int position) {
    ActionBundle actionBundle = (ActionBundle) homeBundle;
    ActionItem actionItem = actionBundle.getActionItem();
    ImageLoader.with(itemView.getContext())
        .load(actionItem.getIcon(), icon);
    title.setText(Translator.translate(actionItem.getTitle(), itemView.getContext(), ""));
    message.setText(Translator.translate(actionItem.getSubTitle(), itemView.getContext(), ""));
    installWalletButton.setOnClickListener(view -> uiEventsListener.onNext(
        new HomeEvent(homeBundle, position, HomeEvent.Type.INSTALL_WALLET)));
    dismissButton.setOnClickListener(itemView -> uiEventsListener.onNext(
        new HomeEvent(homeBundle, position, HomeEvent.Type.DISMISS_BUNDLE)));
  }
}