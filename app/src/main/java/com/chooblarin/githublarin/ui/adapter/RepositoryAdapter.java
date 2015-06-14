package com.chooblarin.githublarin.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.chooblarin.githublarin.R;
import com.chooblarin.githublarin.model.Repository;

import java.util.ArrayList;
import java.util.List;

public class RepositoryAdapter extends RecyclerView.Adapter<RepositoryAdapter.ViewHolder> {

    static class ViewHolder extends RecyclerView.ViewHolder {

        TextView fullName;
        TextView description;

        public ViewHolder(View itemView) {
            super(itemView);
            fullName = (TextView) itemView.findViewById(R.id.text_full_name_repository);
            description = (TextView) itemView.findViewById(R.id.text_description_repository);
        }
    }

    private LayoutInflater inflater;
    private List<Repository> repositories;

    public RepositoryAdapter(Context context) {
        this.inflater = LayoutInflater.from(context);
        this.repositories = new ArrayList<>();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.list_item_repository, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Repository repository = repositories.get(position);
        holder.fullName.setText(repository.fullName);
        holder.description.setText(repository.description);
    }

    @Override
    public int getItemCount() {
        return repositories.size();
    }

    public void addAll(List<Repository> repositories) {
        this.repositories.addAll(repositories);
        notifyDataSetChanged();
    }

    public void setData(List<Repository> repositories) {
        this.repositories.clear();
        addAll(repositories);
    }

    public Repository getDataAt(int position) {
        return this.repositories.get(position);
    }
}
