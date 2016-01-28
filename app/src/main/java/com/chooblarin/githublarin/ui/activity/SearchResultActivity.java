package com.chooblarin.githublarin.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.provider.SearchRecentSuggestions;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import com.chooblarin.githublarin.Application;
import com.chooblarin.githublarin.R;
import com.chooblarin.githublarin.api.client.GitHubApiClient;
import com.chooblarin.githublarin.databinding.ActivitySearchResultBinding;
import com.chooblarin.githublarin.model.Repository;
import com.chooblarin.githublarin.provider.SuggestionsProvider;
import com.chooblarin.githublarin.ui.adapter.RepositoryAdapter;
import com.trello.rxlifecycle.ActivityEvent;

import java.util.List;

import timber.log.Timber;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class SearchResultActivity extends BaseActivity {

    private static final String EXTRA_SEARCH_KEY = "extra_search_key";

    public static Intent createIntent(Context context, String searchKey) {
        Intent intent = new Intent(context, SearchResultActivity.class);
        intent.putExtra(EXTRA_SEARCH_KEY, searchKey);
        return intent;
    }

    private String searchKey;
    private RepositoryAdapter repositoryAdapter;
    GitHubApiClient apiClient;
    ActivitySearchResultBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        searchKey = intent.getStringExtra(EXTRA_SEARCH_KEY);
        new SearchRecentSuggestions(this, SuggestionsProvider.AUTHORITY, SuggestionsProvider.MODE)
                .saveRecentQuery(searchKey, null);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_search_result);

        setupToolbar(binding.toolbarSearchResult);
        setupSearchResultListView(binding.recyclerviewSearchResult);
        setup();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        binding.unbind();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void setupComponent() {
        apiClient = Application.get(this).getAppComponent().apiClient();
    }

    private void setup() {
        if (null == searchKey) {
            return;
        }

        binding.progressSearchResult.setVisibility(View.VISIBLE);

        apiClient.searchRepository(searchKey, true)
                .compose(this.<List<Repository>>bindUntilEvent(ActivityEvent.STOP))
                .subscribe(_repositories -> {
                    binding.progressSearchResult.setVisibility(View.GONE);
                    repositoryAdapter.setData(_repositories);
                }, throwable -> {
                    binding.progressSearchResult.setVisibility(View.GONE);
                    Timber.e(throwable, null);
                });
    }

    private void setupToolbar(Toolbar toolbar) {
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;

        if (null != searchKey) {
            actionBar.setTitle(searchKey);
        }
        actionBar.setDisplayHomeAsUpEnabled(true);
    }

    private void setupSearchResultListView(RecyclerView recyclerView) {
        repositoryAdapter = new RepositoryAdapter(this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(repositoryAdapter);
    }
}
