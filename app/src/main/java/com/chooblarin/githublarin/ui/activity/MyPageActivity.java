package com.chooblarin.githublarin.ui.activity;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import com.chooblarin.githublarin.Application;
import com.chooblarin.githublarin.R;
import com.chooblarin.githublarin.api.client.GitHubApiClient;
import com.chooblarin.githublarin.databinding.ActivityMyPageBinding;
import com.chooblarin.githublarin.model.Repository;
import com.chooblarin.githublarin.model.User;
import com.chooblarin.githublarin.ui.adapter.RepositoryAdapter;
import com.facebook.drawee.view.SimpleDraweeView;
import com.trello.rxlifecycle.ActivityEvent;

import java.util.List;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class MyPageActivity extends BaseActivity {

    private RepositoryAdapter repositoryAdapter;

    SimpleDraweeView avatarImage;

    ActivityMyPageBinding binding;

    GitHubApiClient apiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_my_page);
        setupToolbar(binding.toolbar);
        setupCollapsingToolbar(binding.containerCollapsingMyPage);
        setupRepositoryListView(binding.recyclerview);
        setup();
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
    protected void setupComponent() {
        apiClient = Application.get(this).getAppComponent().apiClient();
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

    private void setup() {
        apiClient.user()
                .compose(this.<User>bindUntilEvent(ActivityEvent.STOP))
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(_user -> {
                    bindUser(_user);
                }, throwable -> throwable.printStackTrace());

        binding.progressLoadingMyRepo.setVisibility(View.VISIBLE);

        apiClient.repositories()
                .compose(this.<List<Repository>>bindUntilEvent(ActivityEvent.STOP))
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
}
