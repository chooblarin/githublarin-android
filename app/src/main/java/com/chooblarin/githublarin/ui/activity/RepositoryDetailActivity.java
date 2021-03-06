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
import com.chooblarin.githublarin.model.Repository;
import com.chooblarin.githublarin.service.StarRepositoryService;
import com.chooblarin.githublarin.ui.widget.likebutton.LikeButtonView;

public class RepositoryDetailActivity extends BaseActivity
        implements LikeButtonView.OnLikeClickListener {

    public static final String EXTRA_REPOSITORY = "extra_repository";

    public static Intent createIntent(Context context, Repository repository) {
        Intent intent = new Intent(context, RepositoryDetailActivity.class);
        intent.putExtra(EXTRA_REPOSITORY, repository);
        return intent;
    }

    private Repository repository;
    private ActivityRepositoryDetailBinding binding;

    @Override
    protected void setupComponent() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_repository_detail);

        this.repository = getIntent().getParcelableExtra(EXTRA_REPOSITORY);
        setupToolbar(binding.toolbarRepositoryDetail, repository.fullName);
        setupWebView(binding.webViewRepositoryDetail, repository.getWebUrl());
        setupLikeButton(binding.viewLikeButton);
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

    @Override
    public void onClick(boolean checked) {
        Intent intent = StarRepositoryService.createIntent(this, repository, checked);
        startService(intent);
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

    private void setupLikeButton(LikeButtonView likeButton) {
        likeButton.setOnLikeClickListener(this);
    }
}
