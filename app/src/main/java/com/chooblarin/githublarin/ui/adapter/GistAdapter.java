package com.chooblarin.githublarin.ui.adapter;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.chooblarin.githublarin.R;
import com.chooblarin.githublarin.databinding.ListItemGistBinding;
import com.chooblarin.githublarin.model.Gist;
import com.chooblarin.githublarin.util.DateTimeUtils;

import org.threeten.bp.Clock;
import org.threeten.bp.LocalDateTime;

public class GistAdapter extends ArrayRecyclerAdapter<Gist, GistAdapter.ViewHolder> {

    final LocalDateTime now;

    public GistAdapter(@NonNull Context context) {
        super(context);
        this.now = LocalDateTime.now(Clock.systemUTC());
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
        ListItemGistBinding binding = holder.binding;
        binding.imageOwnerAvatarGist.setImageURI(Uri.parse(gist.owner.avatarUrl));
        String title = String.format("%s/%s",
                gist.owner.login,
                !gist.files.isEmpty() ? gist.files.get(0).filename : "");
        binding.textTitleGist.setText(title);
        binding.textDescriptionGist.setText(gist.description);

        Context context = holder.itemView.getContext();
        String lastActive = DateTimeUtils.pastTimeString(context, now, gist.updatedAt);
        binding.textLastActiveAt.setText(lastActive);

        binding.textTitleGist.setCompoundDrawablesWithIntrinsicBounds(
                0,
                0,
                gist.isPublic ? 0 : R.drawable.ic_lock_white_18dp,
                0);
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
