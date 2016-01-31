package com.chooblarin.githublarin.ui.adapter;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.chooblarin.githublarin.R;
import com.chooblarin.githublarin.databinding.ListItemGistBinding;
import com.chooblarin.githublarin.model.Gist;

public class GistAdapter extends ArrayRecyclerAdapter<Gist, GistAdapter.ViewHolder> {

    public GistAdapter(@NonNull Context context) {
        super(context);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(ViewHolder.LAYOUT_ID, parent, false);
        final ViewHolder holder = new ViewHolder(view);
        view.setOnClickListener(v -> {
            int position = holder.getAdapterPosition();
            dispatchItemClickEvent(v, position);
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Gist gist = getItem(position);
        holder.binding.textGistId.setText(gist.id);
        holder.binding.textGistDescription.setText(gist.description);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        final static int LAYOUT_ID = R.layout.list_item_gist;

        final private ListItemGistBinding binding;

        public ViewHolder(View itemView) {
            super(itemView);
            binding = DataBindingUtil.bind(itemView);
        }
    }
}
