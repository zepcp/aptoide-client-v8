package cm.aptoide.pt.view.search.demo2;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import cm.aptoide.pt.dataprovider.model.v7.search.SearchApp;
import com.jakewharton.rxrelay.PublishRelay;

public class SearchResultViewHolder2 extends RecyclerView.ViewHolder {

  private final PublishRelay<SearchViewItemEvent> bus;

  public SearchResultViewHolder2(PublishRelay<SearchViewItemEvent> bus, View itemView) {
    super(itemView);
    this.bus = bus;
  }

  public void update(SearchApp app){

  }

}
