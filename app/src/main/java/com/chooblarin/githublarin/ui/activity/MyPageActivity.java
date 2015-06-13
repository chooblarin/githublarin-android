package com.chooblarin.githublarin.ui.activity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.TextView;

import com.chooblarin.githublarin.R;
import com.chooblarin.githublarin.service.GitHubApiService;
import com.chooblarin.githublarin.ui.adapter.StarredAdapter;
import com.facebook.drawee.view.SimpleDraweeView;

import butterknife.ButterKnife;
import butterknife.InjectView;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class MyPageActivity extends AppCompatActivity
        implements ServiceConnection {

    private GitHubApiService service;
    private StarredAdapter starredAdapter;

    @InjectView(R.id.collapsing_toolbar)
    CollapsingToolbarLayout collapsingToolbarLayout;

    @InjectView(R.id.image_avatar_my_page)
    SimpleDraweeView avatarImage;

    @InjectView(R.id.text_user_name_my_page)
    TextView userNameText;

    @InjectView(R.id.text_location_my_page)
    TextView locationText;

    @InjectView(R.id.text_joined_date_my_page)
    TextView joinedDateText;

    @InjectView(R.id.text_following_count)
    TextView followingCountText;

    @InjectView(R.id.text_followers_count)
    TextView followersCountText;

    @InjectView(R.id.recyclerview)
    RecyclerView recyclerView;

    CompositeSubscription subscriptions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_page);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setupToolbar();

        ButterKnife.inject(this);

        collapsingToolbarLayout.setTitle("chooblarin");

        starredAdapter = new StarredAdapter(this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(starredAdapter);
    }

    @Override
    protected void onStart() {
        super.onStart();
        subscriptions = new CompositeSubscription();
        Intent intent = new Intent(getApplicationContext(), GitHubApiService.class);
        getApplicationContext().bindService(intent, this, BIND_AUTO_CREATE);
    }

    @Override
    protected void onStop() {
        super.onStop();
        subscriptions.unsubscribe();
        getApplicationContext().unbindService(this);
    }

    @Override
    protected void onDestroy() {
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

        Subscription user = service.user()
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(_user -> {
                    if (null != _user.avatarUrl) {
                        avatarImage.setImageURI(Uri.parse(_user.avatarUrl));
                    }
                    userNameText.setText(_user.name);
                    locationText.setText(_user.location);
                    joinedDateText.setText(_user.createdAt);
                    followingCountText.setText(String.valueOf(_user.following));
                    followersCountText.setText(String.valueOf(_user.followers));
                }, throwable -> {
                    throwable.printStackTrace();
                });

        Subscription starred = service.starredRepositories()
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(_repositories -> {
                    starredAdapter.setData(_repositories);
                }, throwable -> {
                    throwable.printStackTrace();
                });

        subscriptions.add(user);
        subscriptions.add(starred);
    }

    @Override
    public void onServiceDisconnected(ComponentName componentName) {
        service = null;
    }

    private void setupToolbar() {
        ActionBar actionBar = getSupportActionBar();

        assert actionBar != null;
        actionBar.setDisplayHomeAsUpEnabled(true);
    }
}
