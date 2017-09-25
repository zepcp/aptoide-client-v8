package cm.aptoide.pt.view.search.demo2;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import cm.aptoide.pt.dataprovider.model.v7.search.SearchApp;
import com.jakewharton.rxrelay.PublishRelay;
import java.util.List;

public class SearchResultAdapter2 extends RecyclerView.Adapter<SearchResultViewHolder2> {

  private final List<SearchApp> results;
  private final ViewItemBinder binder;
  private final PublishRelay<SearchViewItemEvent> bus;

  public SearchResultAdapter2(List<SearchApp> results, ViewItemBinder binder, PublishRelay<SearchViewItemEvent> bus) {
    this.results = results;
    this.binder = binder;
    this.bus = bus;
  }

  @Override public SearchResultViewHolder2 onCreateViewHolder(ViewGroup parent, int viewType) {
    View view = LayoutInflater.from(parent.getContext()).inflate(viewType, parent, false);
    return new SearchResultViewHolder2(bus, view);
  }

  @Override public void onBindViewHolder(SearchResultViewHolder2 holder, int position) {
    final SearchApp item = getItem(position);
    holder.update(item);
    binder.bind(item);
  }

  private SearchApp getItem(int position) {
    return results.get(position);
  }

  @Override public int getItemCount() {
    return results.size();
  }
}
