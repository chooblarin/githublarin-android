package com.chooblarin.githublarin.ui.activity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import com.chooblarin.githublarin.R;
import com.chooblarin.githublarin.databinding.ActivitySearchResultBinding;
import com.chooblarin.githublarin.model.Repository;
import com.chooblarin.githublarin.service.GitHubApiService;
import com.chooblarin.githublarin.ui.adapter.RepositoryAdapter;
import com.trello.rxlifecycle.ActivityEvent;
import com.trello.rxlifecycle.components.support.RxAppCompatActivity;

import java.util.List;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class SearchResultActivity extends RxAppCompatActivity
        implements ServiceConnection {

    private static final String EXTRA_SEARCH_KEY = "extra_search_key";

    public static Intent createIntent(Context context, String searchKey) {
        Intent intent = new Intent(context, SearchResultActivity.class);
        intent.putExtra(EXTRA_SEARCH_KEY, searchKey);
        return intent;
    }

    private GitHubApiService service;
    private String searchKey;
    private RepositoryAdapter repositoryAdapter;
    ActivitySearchResultBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        searchKey = intent.getStringExtra(EXTRA_SEARCH_KEY);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_search_result);

        setupToolbar(binding.toolbarSearchResult);
        setupSearchResultListView(binding.recyclerviewSearchResult);
    }

    @Override
    public void onStart() {
        super.onStart();
        Context context = getApplicationContext();
        Intent intent = new Intent(context, GitHubApiService.class);
        context.bindService(intent, this, Context.BIND_AUTO_CREATE);
    }

    @Override
    public void onStop() {
        super.onStop();
        getApplicationContext().unbindService(this);
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
    public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
        service = ((GitHubApiService.GitHubApiBinder) iBinder).getService();
        setup();
    }

    @Override
    public void onServiceDisconnected(ComponentName componentName) {
        service = null;
    }

    private void setup() {
        if (null == searchKey) {
            return;
        }

        binding.progressSearchResult.setVisibility(View.VISIBLE);

        service.searchRepository(searchKey, true)
                .map(searchResponse -> searchResponse.items)
                .compose(this.<List<Repository>>bindUntilEvent(ActivityEvent.STOP))
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(_repositories -> {
                    binding.progressSearchResult.setVisibility(View.GONE);
                    repositoryAdapter.setData(_repositories);
                }, throwable -> {
                    binding.progressSearchResult.setVisibility(View.GONE);
                    throwable.printStackTrace();
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
