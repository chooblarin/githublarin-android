package com.chooblarin.githublarin.ui.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.chooblarin.githublarin.R;
import com.chooblarin.githublarin.model.Entry;

public class EventAdapter extends ArrayRecyclerAdapter<Entry, EventAdapter.ViewHolder> {

    public EventAdapter(@NonNull Context context) {
        super(context);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(ViewHolder.LAYOUT_ID, parent, false);
        final ViewHolder holder = new ViewHolder(view);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = holder.getAdapterPosition();
                dispatchItemClickEvent(v, position);
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Entry entry = getItem(position);
        holder.textView.setText(entry.title);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        static final int LAYOUT_ID = R.layout.list_item_event;

        TextView textView;

        public ViewHolder(View itemView) {
            super(itemView);
            textView = (TextView) itemView.findViewById(R.id.text_title);
        }
    }
}
