package com.chooblarin.githublarin.ui.activity;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.chooblarin.githublarin.Application;
import com.chooblarin.githublarin.R;
import com.chooblarin.githublarin.api.RetryWithConnectivityIncremental;
import com.chooblarin.githublarin.api.client.GitHubApiClient;
import com.chooblarin.githublarin.databinding.ActivitySearchBinding;
import com.chooblarin.githublarin.model.Repository;
import com.chooblarin.githublarin.ui.adapter.RepositoryAdapter;
import com.jakewharton.rxbinding.support.v7.widget.RxSearchView;
import com.trello.rxlifecycle.ActivityEvent;

import java.util.List;
import java.util.concurrent.TimeUnit;

import timber.log.Timber;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class SearchActivity extends BaseActivity implements SearchView.OnQueryTextListener {

    public static final String TAG = SearchActivity.class.getSimpleName();

    public static Intent createIntent(Context context) {
        return new Intent(context, SearchActivity.class);
    }

    private RepositoryAdapter repositoryAdapter;
    private GitHubApiClient apiClient;
    private ActivitySearchBinding binding;

    @Override
    protected void setupComponent() {
        apiClient = Application.get(this).getAppComponent().apiClient();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_search);

        setupToolbar(binding.toolbarSearch);
        setupSearchResultListView(binding.recyclerviewSearchResult);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        binding.unbind();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_search, menu);

        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);

        MenuItem menuItem = menu.findItem(R.id.search_menu_search_view);
        MenuItemCompat.setOnActionExpandListener(menuItem, new MenuItemCompat.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                // todo
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                // todo
                return true;
            }
        });
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(menuItem);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setOnQueryTextListener(this);
        searchView.onActionViewExpanded();
        Context context = getApplicationContext();
        RxSearchView.queryTextChanges(searchView)
                .debounce(1000L, TimeUnit.MILLISECONDS)
                .filter(_searchKey -> !TextUtils.isEmpty(_searchKey))
                .switchMap(_searchKey -> apiClient.searchRepository(_searchKey.toString(), true))
                .compose(bindUntilEvent(ActivityEvent.STOP))
                .retryWhen(new RetryWithConnectivityIncremental(context, 15L, 30L, TimeUnit.SECONDS))
                .subscribe(this::bindSearchResult,
                        throwable -> {
                            Timber.tag(TAG).e(throwable, null);
                        });
        return true;
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
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        handleIntent(intent);
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        return false;
    }

    private void setupToolbar(Toolbar toolbar) {
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setDisplayHomeAsUpEnabled(true);
    }

    private void setupSearchResultListView(RecyclerView recyclerView) {
        repositoryAdapter = new RepositoryAdapter(this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(repositoryAdapter);
    }

    private void searchFor(String searchKey) {
        binding.progressSearchResult.setVisibility(View.VISIBLE);
        apiClient.searchRepository(searchKey, true)
                .compose(this.<List<Repository>>bindUntilEvent(ActivityEvent.STOP))
                .subscribe(this::bindSearchResult,
                        throwable -> {
                            binding.progressSearchResult.setVisibility(View.GONE);
                            Timber.tag(TAG).e(throwable, null);
                        });
    }

    private void bindSearchResult(List<Repository> repositories) {
        binding.progressSearchResult.setVisibility(View.GONE);
        repositoryAdapter.setData(repositories);
    }

    private void handleIntent(@NonNull Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            searchFor(query);
        }
    }
}
