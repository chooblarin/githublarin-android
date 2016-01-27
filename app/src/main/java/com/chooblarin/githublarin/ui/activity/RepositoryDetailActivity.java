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

import com.chooblarin.githublarin.R;
import com.chooblarin.githublarin.databinding.ActivityRepositoryDetailBinding;

public class RepositoryDetailActivity extends BaseActivity {

    public static final String EXTRA_URL = "extra_url";

    public static Intent createIntent(Context context, String url) {
        Intent intent = new Intent(context, RepositoryDetailActivity.class);
        intent.putExtra(EXTRA_URL, url);
        return intent;
    }

    ActivityRepositoryDetailBinding binding;

    @Override
    protected void setupComponent() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String url = getIntent().getStringExtra(EXTRA_URL);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_repository_detail);
        setupToolbar(binding.toolbarRepositoryDetail);
        setupWebView(binding.webView, url);
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

    private void setupWebView(WebView webView, String url) {
        WebSettings settings = webView.getSettings();
        webView.setHorizontalScrollBarEnabled(false);
        // webView.setOverScrollMode(View.OVER_SCROLL_NEVER);
        settings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        webView.loadUrl(url);
    }
}
