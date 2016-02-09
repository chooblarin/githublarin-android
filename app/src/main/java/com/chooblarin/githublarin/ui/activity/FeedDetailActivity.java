package com.chooblarin.githublarin.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebSettings;
import android.webkit.WebView;

import com.chooblarin.githublarin.R;
import com.chooblarin.githublarin.databinding.ActivityFeedDetailBinding;
import com.chooblarin.githublarin.model.Feed;

public class FeedDetailActivity extends BaseActivity {

    private static final String EXTRA_FEED = "EXTRA_FEED";

    public static Intent createIntent(Context context, Feed feed) {
        Intent intent = new Intent(context, FeedDetailActivity.class);
        intent.putExtra(EXTRA_FEED, feed);
        return intent;
    }

    private Feed feed;
    private ActivityFeedDetailBinding binding;

    @Override
    protected void setupComponent() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        feed = getIntent().getParcelableExtra(EXTRA_FEED);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_feed_detail);
        setupToolbar(binding.toolbarFeedDetail, feed.title);
        setupWebView(binding.webViewFeedDetail, feed.link);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding.unbind();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_feed_detail, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        if (android.R.id.home == itemId) {
            finish();
        } else if (R.id.share_menu == itemId) {
            share();
        }
        return super.onOptionsItemSelected(item);
    }

    private void setupToolbar(Toolbar toolbar, String title) {
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setTitle(title);
        actionBar.setDisplayHomeAsUpEnabled(true);
    }

    private void setupWebView(WebView webView, String url) {
        WebSettings settings = webView.getSettings();
        webView.setHorizontalScrollBarEnabled(false);
        // webView.setOverScrollMode(View.OVER_SCROLL_NEVER);
        settings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        webView.loadUrl(url);
    }

    private void share() {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, feed.link);
        startActivity(Intent.createChooser(shareIntent, "Share link using"));
    }
}
