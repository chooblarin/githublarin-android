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
import android.webkit.WebViewClient;

import com.chooblarin.githublarin.R;
import com.chooblarin.githublarin.databinding.ActivityGistDetailBinding;
import com.chooblarin.githublarin.model.Gist;

public class GistDetailActivity extends BaseActivity {

    private static final String EXTRA_GIST = "extra_gist";

    public static Intent createIntent(Context context, Gist gist) {
        Intent intent = new Intent(context, GistDetailActivity.class);
        intent.putExtra(EXTRA_GIST, gist);
        return intent;
    }

    ActivityGistDetailBinding binding;

    @Override
    protected void setupComponent() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_gist_detail);
        Gist gist = getIntent().getParcelableExtra(EXTRA_GIST);
        setupToolbar(binding.toolbarGistDetail, gist.getTitle());
        setupWebView(binding.webViewGistDetail, gist.htmlUrl);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding.unbind();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        if (android.R.id.home == itemId) {
            finish();
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
        webView.setWebViewClient(new WebViewClient()); // todo: workaround
        webView.loadUrl(url);
    }
}
