package com.chooblarin.githublarin.ui.activity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import com.chooblarin.githublarin.R;
import com.chooblarin.githublarin.databinding.ActivityMyPageBinding;
import com.chooblarin.githublarin.model.User;
import com.chooblarin.githublarin.service.GitHubApiService;
import com.chooblarin.githublarin.ui.adapter.RepositoryAdapter;
import com.facebook.drawee.view.SimpleDraweeView;
import com.trello.rxlifecycle.ActivityEvent;
import com.trello.rxlifecycle.components.support.RxAppCompatActivity;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class MyPageActivity extends RxAppCompatActivity
        implements ServiceConnection {

    private GitHubApiService service;
    private RepositoryAdapter repositoryAdapter;

    SimpleDraweeView avatarImage;

    ActivityMyPageBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_my_page);
        setupToolbar(binding.toolbar);
        setupCollapsingToolbar(binding.containerCollapsingMyPage);
        setupRepositoryListView(binding.recyclerview);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Intent intent = new Intent(getApplicationContext(), GitHubApiService.class);
        getApplicationContext().bindService(intent, this, BIND_AUTO_CREATE);
    }

    @Override
    protected void onStop() {
        super.onStop();
        getApplicationContext().unbindService(this);
    }

    @Override
    protected void onDestroy() {
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

        service.user()
                .compose(bindUntilEvent(ActivityEvent.STOP))
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(_user -> {
                    bindUser(_user);
                }, throwable -> throwable.printStackTrace());

        binding.progressLoadingMyRepo.setVisibility(View.VISIBLE);

        service.repositories()
                .compose(bindUntilEvent(ActivityEvent.STOP))
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(_repositories -> {
                    binding.progressLoadingMyRepo.setVisibility(View.GONE);
                    repositoryAdapter.setData(_repositories);
                }, throwable -> {
                    binding.progressLoadingMyRepo.setVisibility(View.GONE);
                    throwable.printStackTrace();
                });
    }

    @Override
    public void onServiceDisconnected(ComponentName componentName) {
        service = null;
    }

    private void setupToolbar(Toolbar toolbar) {
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();

        assert actionBar != null;
        actionBar.setDisplayHomeAsUpEnabled(true);
    }

    private void setupCollapsingToolbar(View layout) {
        avatarImage = (SimpleDraweeView) layout.findViewById(R.id.image_avatar_my_page);
        binding.collapsingToolbar.setTitle("chooblarin");
    }

    private void setupRepositoryListView(RecyclerView recyclerView) {
        repositoryAdapter = new RepositoryAdapter(this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(repositoryAdapter);
    }

    private void bindUser(User user) {
        if (null != user.avatarUrl) {
            avatarImage.setImageURI(Uri.parse(user.avatarUrl));
        }
        binding.setUser(user);
    }
}
