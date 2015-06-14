package com.chooblarin.githublarin.ui.activity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

import com.chooblarin.githublarin.R;
import com.chooblarin.githublarin.service.GitHubApiService;
import com.chooblarin.githublarin.ui.adapter.RepositoryAdapter;

import butterknife.ButterKnife;
import butterknife.InjectView;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class SearchResultActivity extends AppCompatActivity
        implements ServiceConnection {

    private static final String EXTRA_SEARCH_KEY = "extra_search_key";

    public static Intent createIntent(Context context, String searchKey) {
        Intent intent = new Intent(context, SearchResultActivity.class);
        intent.putExtra(EXTRA_SEARCH_KEY, searchKey);
        return intent;
    }

    private GitHubApiService service;
    private CompositeSubscription subscriptions;
    private String searchKey;
    private RepositoryAdapter repositoryAdapter;

    @InjectView(R.id.recyclerview_search_result)
    RecyclerView recyclerView;

    @InjectView(R.id.progress_search_result)
    ProgressBar loadingProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        searchKey = intent.getStringExtra(EXTRA_SEARCH_KEY);

        setContentView(R.layout.activity_search_result);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_search_result);
        setSupportActionBar(toolbar);
        setupToolbar();

        ButterKnife.inject(this);

        repositoryAdapter = new RepositoryAdapter(this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(repositoryAdapter);
    }

    @Override
    public void onStart() {
        super.onStart();
        subscriptions = new CompositeSubscription();

        Context context = getApplicationContext();
        Intent intent = new Intent(context, GitHubApiService.class);
        context.bindService(intent, this, Context.BIND_AUTO_CREATE);
    }

    @Override
    public void onStop() {
        super.onStop();
        subscriptions.unsubscribe();
        getApplicationContext().unbindService(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        ButterKnife.reset(this);
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

        loadingProgress.setVisibility(View.VISIBLE);

        Subscription subscription = service.searchRepository(searchKey, true)
                .map(searchResponse -> searchResponse.items)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(_repositories -> {
                    loadingProgress.setVisibility(View.GONE);
                    repositoryAdapter.setData(_repositories);
                }, throwable -> {
                    loadingProgress.setVisibility(View.GONE);
                    throwable.printStackTrace();
                });
        subscriptions.add(subscription);
    }

    private void setupToolbar() {
        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;

        if (null != searchKey) {
            actionBar.setTitle(searchKey);
        }
        actionBar.setDisplayHomeAsUpEnabled(true);
    }
}
