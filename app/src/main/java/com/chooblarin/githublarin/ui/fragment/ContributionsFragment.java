package com.chooblarin.githublarin.ui.fragment;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.chooblarin.githublarin.Application;
import com.chooblarin.githublarin.R;
import com.chooblarin.githublarin.api.client.GitHubApiClient;
import com.chooblarin.githublarin.databinding.FragmentContributionsBinding;
import com.chooblarin.githublarin.di.AppComponent;

public class ContributionsFragment extends BaseFragment {

    public static ContributionsFragment newInstance() {
        return new ContributionsFragment();
    }

    GitHubApiClient apiClient;
    FragmentContributionsBinding binding;

    @Override
    protected void setupComponent() {
        AppComponent appComponent = Application.get(getContext()).getAppComponent();
        apiClient = appComponent.apiClient();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil
                .inflate(inflater, R.layout.fragment_contributions, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        binding.unbind();
    }
}
