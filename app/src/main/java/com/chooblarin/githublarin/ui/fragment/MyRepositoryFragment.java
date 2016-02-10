package com.chooblarin.githublarin.ui.fragment;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.chooblarin.githublarin.Application;
import com.chooblarin.githublarin.R;
import com.chooblarin.githublarin.api.client.GitHubApiClient;
import com.chooblarin.githublarin.databinding.FragmentMyRepositoryBinding;
import com.chooblarin.githublarin.di.AppComponent;
import com.chooblarin.githublarin.model.Repository;
import com.chooblarin.githublarin.ui.activity.RepositoryDetailActivity;
import com.chooblarin.githublarin.ui.adapter.RepositoryAdapter;
import com.chooblarin.githublarin.ui.listener.OnItemClickListener;
import com.trello.rxlifecycle.FragmentEvent;

public class MyRepositoryFragment extends BaseFragment implements OnItemClickListener {

    public static MyRepositoryFragment newInstance() {
        return new MyRepositoryFragment();
    }

    RepositoryAdapter repositoryAdapter;
    FragmentMyRepositoryBinding binding;
    GitHubApiClient apiClient;

    @Override
    protected void setupComponent() {
        AppComponent appComponent = Application.get(getContext()).getAppComponent();
        apiClient = appComponent.apiClient();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_my_repository, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupMyRepositoryListView(binding.recyclerviewMyRepository);
    }

    @Override
    public void onResume() {
        super.onResume();
        loadRepositories();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        binding.unbind();
    }

    @Override
    public void onItemClick(View view, int position) {
        Repository repository = repositoryAdapter.getItem(position);
        startActivity(RepositoryDetailActivity.createIntent(getActivity(), repository));
    }

    private void setupMyRepositoryListView(RecyclerView recyclerView) {
        repositoryAdapter = new RepositoryAdapter(getActivity());
        repositoryAdapter.setOnItemClickListener(this);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(repositoryAdapter);
    }

    private void loadRepositories() {
        binding.progressLoadingMyRepository.setVisibility(View.VISIBLE);

        apiClient.repositories()
                .compose(this.bindUntilEvent(FragmentEvent.STOP))
                .subscribe(_repositories -> {
                    binding.progressLoadingMyRepository.setVisibility(View.GONE);
                    repositoryAdapter.clear();
                    repositoryAdapter.addAll(_repositories);
                    repositoryAdapter.notifyDataSetChanged();
                }, throwable -> {
                    binding.progressLoadingMyRepository.setVisibility(View.GONE);
                    throwable.printStackTrace();
                });
    }
}
