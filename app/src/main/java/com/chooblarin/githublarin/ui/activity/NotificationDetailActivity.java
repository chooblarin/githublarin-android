package com.chooblarin.githublarin.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.webkit.WebSettings;
import android.webkit.WebView;

import com.chooblarin.githublarin.Application;
import com.chooblarin.githublarin.R;
import com.chooblarin.githublarin.api.client.GitHubApiClient;
import com.chooblarin.githublarin.databinding.ActivityNotificationDetailBinding;
import com.chooblarin.githublarin.model.Notification;

/* [WIP] */
public class NotificationDetailActivity extends BaseActivity {

    private static final String EXTRA_NOTIFICATION = "extra_notification";

    public static Intent createIntent(Context context, Notification notification) {
        Intent intent = new Intent(context, NotificationDetailActivity.class);
        intent.putExtra(EXTRA_NOTIFICATION, notification);
        return intent;
    }

    private ActivityNotificationDetailBinding binding;
    private Notification notification;
    private GitHubApiClient apiClient;

    @Override
    protected void setupComponent() {
        apiClient = Application.get(this).getAppComponent().apiClient();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        notification = getIntent().getParcelableExtra(EXTRA_NOTIFICATION);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_notification_detail);
        setupToolbar(binding.toolbarNotificationDetail);
        setupWebView(binding.webviewNotificationDetail);
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadPage();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        if (android.R.id.home == itemId) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    private void setupToolbar(Toolbar toolbar) {
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setDisplayHomeAsUpEnabled(true);
    }

    private void setupWebView(WebView webView) {
        WebSettings settings = webView.getSettings();
        webView.setHorizontalScrollBarEnabled(false);
        settings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
    }

    private void loadPage() {
        // binding.webviewNotificationDetail.loadUrl(notification.url);
    }
}
