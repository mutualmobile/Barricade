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

class RepoListAdapter extends RecyclerView.Adapter<RepoListAdapter.ViewHolder> {

  private ArrayList<Repo> repoList;

  RepoListAdapter(ArrayList<Repo> repoList) {
    this.repoList = repoList;
  }

  void setRepoList(ArrayList<Repo> repoList) {
    this.repoList = repoList;
  }

  @Override public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_repo, parent, false);
    return new ViewHolder(v);
  }

  @Override public void onBindViewHolder(ViewHolder holder, int position) {
    holder.repoTextView.setText(repoList.get(position).name);
  }

  @Override public int getItemCount() {
    return repoList == null ? 0 : repoList.size();
  }

  class ViewHolder extends RecyclerView.ViewHolder {

    TextView repoTextView;

    ViewHolder(View itemView) {
      super(itemView);

      repoTextView = (TextView) itemView.findViewById(R.id.repo_name);

      itemView.setOnClickListener(new View.OnClickListener() {
        @Override public void onClick(View view) {
          Repo repo = repoList.get(getAdapterPosition());
          openGithubRepo(view.getContext(), "http://www.github.com/" + repo.full_name);
        }
      });
    }
  }

  private void openGithubRepo(Context context, String url) {
    Intent i = new Intent(Intent.ACTION_VIEW);
    i.setData(Uri.parse(url));
    context.startActivity(i);
  }
}
