package com.chooblarin.githublarin.ui.activity;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import com.chooblarin.githublarin.Application;
import com.chooblarin.githublarin.R;
import com.chooblarin.githublarin.api.client.GitHubApiClient;
import com.chooblarin.githublarin.databinding.ActivityMyPageBinding;
import com.chooblarin.githublarin.model.User;
import com.chooblarin.githublarin.ui.fragment.MyRepositoryFragment;
import com.facebook.drawee.view.SimpleDraweeView;
import com.trello.rxlifecycle.ActivityEvent;

import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class MyPageActivity extends BaseActivity {

    SimpleDraweeView avatarImage;
    ActivityMyPageBinding binding;
    GitHubApiClient apiClient;

    @Override
    protected void setupComponent() {
        apiClient = Application.get(this).getAppComponent().apiClient();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_my_page);
        setupToolbar(binding.toolbar);
        setupCollapsingToolbar(binding.containerCollapsingMyPage);
        setupViewPager(binding.tabLayoutMyPage, binding.viewPagerMyPage);
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

    private void bindUser(User user) {
        if (null != user.avatarUrl) {
            avatarImage.setImageURI(Uri.parse(user.avatarUrl));
        }
        binding.setUser(user);
    }

    private void setup() {
        apiClient.user()
                .compose(this.<User>bindUntilEvent(ActivityEvent.STOP))
                .subscribe(_user -> {
                    bindUser(_user);
                }, throwable -> {
                    Timber.e(throwable, null);
                });
    }

    private void setupViewPager(TabLayout tabLayout, ViewPager viewPager) {
        PagerAdapter pagerAdapter = new PagerAdapter(getSupportFragmentManager());
        pagerAdapter.addFragment(MyRepositoryFragment.newInstance(), getString(R.string.my_repository));
        viewPager.setAdapter(pagerAdapter);
        tabLayout.setupWithViewPager(viewPager);
    }

    static class PagerAdapter extends FragmentPagerAdapter {

        final List<String> titles;
        final List<Fragment> fragments;

        public PagerAdapter(FragmentManager fm) {
            super(fm);
            titles = new ArrayList<>();
            fragments = new ArrayList<>();
        }

        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return titles.get(position);
        }

        public void addFragment(Fragment fragment, String title) {
            fragments.add(fragment);
            titles.add(title);
        }
    }
}
