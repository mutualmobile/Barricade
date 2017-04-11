package com.mutualmobile.barricade.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.mutualmobile.barricade.response.BarricadeResponse;
import com.mutualmobile.barricade.response.BarricadeResponseSet;
import java.util.HashMap;

/**
 * RecyclerView Adapter for displaying endpoints and their selected defaults.
 */
public class BarricadeEndpointsRVAdapter
    extends RecyclerView.Adapter<BarricadeEndpointsRVAdapter.BarricadeEndpointViewHolder> {

  private HashMap<String, BarricadeResponseSet> config;
  private EndpointClickedListener clickListener;

  public BarricadeEndpointsRVAdapter(HashMap<String, BarricadeResponseSet> endpoints,
      EndpointClickedListener clickListener) {
    this.config = endpoints;
    this.clickListener = clickListener;
  }

  @Override public BarricadeEndpointViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    View view = LayoutInflater.from(parent.getContext())
        .inflate(android.R.layout.simple_list_item_2, parent, false);
    return new BarricadeEndpointViewHolder(view);
  }

  @Override public void onBindViewHolder(BarricadeEndpointViewHolder holder, int position) {
    final String endpoint = config.keySet().toArray()[position].toString();
    holder.endpointText.setText(endpoint);

    BarricadeResponseSet barricadeResponseSet = config.get(endpoint);
    BarricadeResponse response =
        barricadeResponseSet.responses.get(barricadeResponseSet.defaultIndex);
    holder.endpointSelectedResponseText.setText(response.responseFileName);
    holder.itemView.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View view) {
        onEndpointClicked(endpoint);
      }
    });
  }

  @Override public int getItemCount() {
    return config.size();
  }

  public void onEndpointClicked(String endpoint) {
    clickListener.onEndpointClicked(endpoint);
  }

  public class BarricadeEndpointViewHolder extends RecyclerView.ViewHolder {
    TextView endpointText;
    TextView endpointSelectedResponseText;

    public BarricadeEndpointViewHolder(View itemView) {
      super(itemView);
      endpointText = (TextView) itemView.findViewById(android.R.id.text1);
      endpointSelectedResponseText = (TextView) itemView.findViewById(android.R.id.text2);
    }
  }

  public interface EndpointClickedListener {
    void onEndpointClicked(String endpoint);
  }
}
