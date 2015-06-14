package com.chooblarin.githublarin.ui.activity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.chooblarin.githublarin.R;
import com.chooblarin.githublarin.service.GitHubApiService;

import butterknife.ButterKnife;
import butterknife.InjectView;
import rx.subscriptions.CompositeSubscription;

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

    @InjectView(R.id.recyclerview_search_result)
    public RecyclerView recyclerView;

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
    public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
        service = ((GitHubApiService.GitHubApiBinder) iBinder).getService();
        setup();
    }

    @Override
    public void onServiceDisconnected(ComponentName componentName) {
        service = null;
    }

    private void setup() {
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
