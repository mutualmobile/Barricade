package com.mutualmobile.barricade.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.mutualmobile.barricade.R;
import com.mutualmobile.barricade.response.BarricadeResponse;
import java.util.List;

/**
 * RecyclerView Adapter for displaying responses for an endpoint.
 */
public class BarricadeResponsesRVAdapter
    extends RecyclerView.Adapter<BarricadeResponsesRVAdapter.BarricadeResponseViewHolder> {

  private List<BarricadeResponse> barricadeResponses;
  private EndpointResponseClickedListener clickListener;
  private String endpoint;
  private int defaultIndex;

  public BarricadeResponsesRVAdapter(String endpoint, List<BarricadeResponse> endpointResponses,
      int defaultIndex, EndpointResponseClickedListener clickListener) {
    this.barricadeResponses = endpointResponses;
    this.clickListener = clickListener;
    this.endpoint = endpoint;
    this.defaultIndex = defaultIndex;
  }

  @Override public BarricadeResponseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    View view = LayoutInflater.from(parent.getContext())
        .inflate(R.layout.layout_barricade_response_list_item, parent, false);
    return new BarricadeResponseViewHolder(view);
  }

  @Override public void onBindViewHolder(final BarricadeResponseViewHolder holder, int position) {
    holder.responseFileText.setText(barricadeResponses.get(position).responseFileName);
    holder.responseCodeText.setText(String.valueOf(barricadeResponses.get(position).statusCode));
    holder.itemView.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View view) {
        clickListener.onResponseClicked(endpoint, holder.getAdapterPosition());
      }
    });

    if (position == defaultIndex) {
      holder.selectedIcon.setVisibility(View.VISIBLE);
    } else {
      holder.selectedIcon.setVisibility(View.INVISIBLE);
    }
  }

  @Override public int getItemCount() {
    return barricadeResponses.size();
  }

  class BarricadeResponseViewHolder extends RecyclerView.ViewHolder {
    TextView responseCodeText;
    TextView responseFileText;
    ImageView selectedIcon;

    BarricadeResponseViewHolder(View itemView) {
      super(itemView);
      responseCodeText = (TextView) itemView.findViewById(R.id.response_code);
      responseFileText = (TextView) itemView.findViewById(R.id.response_file);
      selectedIcon = (ImageView) itemView.findViewById(R.id.response_selected_checkbox);
    }
  }

  public interface EndpointResponseClickedListener {
    void onResponseClicked(String endpoint, int index);
  }
}
