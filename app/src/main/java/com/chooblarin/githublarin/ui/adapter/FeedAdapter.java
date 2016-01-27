package com.chooblarin.githublarin.ui.adapter;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.chooblarin.githublarin.R;
import com.chooblarin.githublarin.model.Feed;
import com.facebook.drawee.view.SimpleDraweeView;

import org.threeten.bp.Clock;
import org.threeten.bp.Duration;
import org.threeten.bp.LocalDateTime;

public class FeedAdapter extends ArrayRecyclerAdapter<Feed, FeedAdapter.ViewHolder> {

    final private LocalDateTime now;

    public FeedAdapter(@NonNull Context context) {
        super(context);
        this.now = LocalDateTime.now(Clock.systemUTC());
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
        Feed feed = getItem(position);
        holder.thumbnail.setImageURI(Uri.parse(feed.thumbnail));
        holder.title.setText(feed.title);
        long hours = Duration.between(feed.published, now).toHours();
        holder.publishedAt.setText(String.format("%d hours ago", hours));
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        static final int LAYOUT_ID = R.layout.list_item_feed;
        SimpleDraweeView thumbnail;
        TextView title;
        TextView publishedAt;

        public ViewHolder(View itemView) {
            super(itemView);
            thumbnail = (SimpleDraweeView) itemView.findViewById(R.id.image_thumbnail);
            title = (TextView) itemView.findViewById(R.id.text_title);
            publishedAt = (TextView) itemView.findViewById(R.id.text_published_at);
        }
    }
}
