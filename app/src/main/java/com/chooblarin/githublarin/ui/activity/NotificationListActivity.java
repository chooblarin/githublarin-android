package com.chooblarin.githublarin.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.chooblarin.githublarin.Application;
import com.chooblarin.githublarin.R;
import com.chooblarin.githublarin.api.client.GitHubApiClient;
import com.chooblarin.githublarin.databinding.ActivityNotificationListBinding;
import com.chooblarin.githublarin.databinding.ListItemNotificationBinding;
import com.chooblarin.githublarin.di.AppComponent;
import com.chooblarin.githublarin.model.Notification;
import com.chooblarin.githublarin.ui.adapter.ArrayRecyclerAdapter;
import com.chooblarin.githublarin.ui.listener.OnItemClickListener;
import com.chooblarin.githublarin.util.DateTimeUtils;

import org.threeten.bp.Clock;
import org.threeten.bp.LocalDateTime;

import java.util.List;

import retrofit.HttpException;
import timber.log.Timber;

public class NotificationListActivity extends BaseActivity implements OnItemClickListener {

    public static Intent createIntent(Context context) {
        return new Intent(context, NotificationListActivity.class);
    }

    private ActivityNotificationListBinding binding;
    private GitHubApiClient apiClient;
    private Adapter adapter;

    @Override
    protected void setupComponent() {
        AppComponent appComponent = Application.get(this).getAppComponent();
        apiClient = appComponent.apiClient();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_notification_list);
        setupToolbar(binding.toolbarNotificationList);
        setupNotificationListView(binding.recyclerviewNotification);
        setupNotificationList();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemClick(View view, int position) {
        // todo: WIP
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse("https://github.com/notifications"));
        startActivity(intent);
        // Notification notification = adapter.getItem(position);
        // startActivity(NotificationDetailActivity.createIntent(this, notification));
    }

    private void setupToolbar(Toolbar toolbar) {
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setDisplayHomeAsUpEnabled(true);
    }

    private void setupNotificationListView(RecyclerView recyclerView) {
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new Adapter(this);
        adapter.setOnItemClickListener(this);
        recyclerView.setAdapter(adapter);
    }

    private void setupNotificationList() {
        apiClient.notifications()
                .subscribe(notifications -> {
                    bindNotifications(notifications);
                }, throwable -> {
                    Timber.e(throwable, null);
                    if (throwable instanceof HttpException) {
                        HttpException exception = (HttpException) throwable;
                        int statusCode = exception.code();
                        if (304 == statusCode) {
                            // todo: not modified
                        }
                    }
                });
    }

    private void bindNotifications(List<Notification> notificationList) {
        if (notificationList.isEmpty()) {
            binding.viewEmptyNotification.setVisibility(View.VISIBLE);
            binding.recyclerviewNotification.setVisibility(View.GONE);
        } else {
            binding.viewEmptyNotification.setVisibility(View.GONE);
            binding.recyclerviewNotification.setVisibility(View.VISIBLE);
            adapter.clear();
            adapter.addAll(notificationList);
            adapter.notifyDataSetChanged();
        }
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        static final int LAYOUT_ID = R.layout.list_item_notification;
        final ListItemNotificationBinding binding;

        public ViewHolder(View itemView) {
            super(itemView);
            binding = DataBindingUtil.bind(itemView);
        }
    }

    static class Adapter extends ArrayRecyclerAdapter<Notification, ViewHolder> {

        final private LocalDateTime now;

        public Adapter(@NonNull Context context) {
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
            Notification notification = getItem(position);
            holder.binding.textNotificationTitle.setText(notification.subject.title);
            Context context = holder.itemView.getContext();
            holder.binding.textUpdatedAt
                    .setText(DateTimeUtils.pastTimeString(context, now, notification.updatedAt));
        }
    }
}
