package com.chooblarin.githublarin.ui.adapter;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.chooblarin.githublarin.R;
import com.chooblarin.githublarin.databinding.ListItemRepositoryBinding;
import com.chooblarin.githublarin.model.Repository;

public class RepositoryAdapter
        extends ArrayRecyclerAdapter<Repository, RepositoryAdapter.ViewHolder> {

    public RepositoryAdapter(@NonNull Context context) {
        super(context);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(ViewHolder.LAYOUT_ID, parent, false);
        final ViewHolder holder = new ViewHolder(view);
        holder.itemView.setOnClickListener(v -> {
            int position = holder.getAdapterPosition();
            dispatchItemClickEvent(v, position);
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Repository repository = getItem(position);
        holder.binding.textFullNameRepository.setText(repository.fullName);
        holder.binding.textDescriptionRepository.setText(repository.description);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        final static int LAYOUT_ID = R.layout.list_item_repository;
        final ListItemRepositoryBinding binding;

        public ViewHolder(View itemView) {
            super(itemView);
            this.binding = DataBindingUtil.bind(itemView);
        }
    }
}
