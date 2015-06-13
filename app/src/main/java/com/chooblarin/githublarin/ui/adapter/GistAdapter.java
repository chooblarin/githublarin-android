package com.chooblarin.githublarin.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.chooblarin.githublarin.R;
import com.chooblarin.githublarin.model.Gist;

import java.util.ArrayList;
import java.util.List;

public class GistAdapter extends RecyclerView.Adapter<GistAdapter.ViewHolder> {

    static class ViewHolder extends RecyclerView.ViewHolder {

        TextView gistId;
        TextView description;

        public ViewHolder(View itemView) {
            super(itemView);
            gistId = (TextView) itemView.findViewById(R.id.text_gist_id);
            description = (TextView) itemView.findViewById(R.id.text_gist_description);
        }
    }

    private LayoutInflater inflater;
    private List<Gist> gists;

    public GistAdapter(Context context) {
        inflater = LayoutInflater.from(context);
        gists = new ArrayList<>();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.list_item_gist, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Gist gist = gists.get(position);
        holder.gistId.setText(gist.id);
        holder.description.setText(gist.description);
    }

    @Override
    public int getItemCount() {
        return gists.size();
    }

    public void addAll(List<Gist> gists) {
        this.gists.addAll(gists);
        notifyDataSetChanged();
    }

    public void setData(List<Gist> gists) {
        this.gists.clear();
        addAll(gists);
    }
}
