package com.mutualmobile.barricade.sample;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.mutualmobile.barricade.sample.api.model.Repo;
import java.util.ArrayList;
import java.util.List;

class RepoListAdapter extends RecyclerView.Adapter<RepoListAdapter.ViewHolder> {

  private List<Repo> repoList;

  void setRepoList(List<Repo> repoList) {
    this.repoList = repoList;
    notifyDataSetChanged();
  }

  @Override public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    return new ViewHolder(
        LayoutInflater.from(parent.getContext()).inflate(R.layout.item_repo, parent, false));
  }

  @Override public void onBindViewHolder(final ViewHolder holder, int position) {
    holder.repoTextView.setText(repoList.get(position).name);
    holder.itemView.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View view) {
        Repo repo = repoList.get(holder.getAdapterPosition());
        openGithubRepo(view.getContext(), "http://www.github.com/" + repo.full_name);
      }
    });
  }

  private void openGithubRepo(Context context, String url) {
    Intent i = new Intent(Intent.ACTION_VIEW);
    i.setData(Uri.parse(url));
    context.startActivity(i);
  }

  @Override public int getItemCount() {
    return repoList == null ? 0 : repoList.size();
  }

  static class ViewHolder extends RecyclerView.ViewHolder {

    TextView repoTextView;

    ViewHolder(View itemView) {
      super(itemView);
      repoTextView = (TextView) itemView.findViewById(R.id.repo_name);
    }
  }
}
