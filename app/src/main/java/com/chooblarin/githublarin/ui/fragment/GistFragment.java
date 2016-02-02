package com.chooblarin.githublarin.ui.fragment;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.chooblarin.githublarin.R;
import com.chooblarin.githublarin.databinding.FragmentGistBinding;

import java.util.ArrayList;
import java.util.List;

public class GistFragment extends BaseFragment {

    private FragmentGistBinding binding;

    @Override
    protected void setupComponent() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_gist, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupViewPager(binding.viewPagerGist);
        setupTabLayoutWithViewPager(binding.tabLayoutGist, binding.viewPagerGist);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        binding.unbind();
    }

    private void setupViewPager(ViewPager viewPager) {
        PagerAdapter pagerAdapter = new PagerAdapter(getChildFragmentManager());
        pagerAdapter.addFragment(new MyGistFragment(), getString(R.string.my_gist));
        pagerAdapter.addFragment(new StarredGistFragment(), getString(R.string.starred_gist));
        viewPager.setAdapter(pagerAdapter);
    }

    private void setupTabLayoutWithViewPager(TabLayout tabLayout, ViewPager viewPager) {
        tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.ic_star_white_18dp));
        tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.ic_star_white_18dp));
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
