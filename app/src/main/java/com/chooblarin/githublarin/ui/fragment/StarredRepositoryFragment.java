package com.chooblarin.githublarin.ui.fragment;

import android.content.Context;
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
import com.chooblarin.githublarin.databinding.FragmentStarredRepositoryBinding;
import com.chooblarin.githublarin.model.Repository;
import com.chooblarin.githublarin.ui.activity.RepositoryDetailActivity;
import com.chooblarin.githublarin.ui.adapter.RepositoryAdapter;
import com.chooblarin.githublarin.ui.listener.OnItemClickListener;
import com.trello.rxlifecycle.FragmentEvent;

import java.util.List;

import timber.log.Timber;

public class StarredRepositoryFragment extends BaseFragment implements OnItemClickListener {

    public static final String TAG = StarredRepositoryFragment.class.getSimpleName();

    private GitHubApiClient apiClient;
    private RepositoryAdapter repositoryAdapter;

    FragmentStarredRepositoryBinding binding;

    @Override
    protected void setupComponent() {
        apiClient = Application.get(getContext()).getAppComponent().apiClient();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        repositoryAdapter = new RepositoryAdapter(context);
        repositoryAdapter.setOnItemClickListener(this);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_starred_repository, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupStarredList(binding.recyclerviewStarredRepository);
        setup();
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

    private void setup() {
        binding.progressLoadingStarredRepository.setVisibility(View.VISIBLE);

        apiClient.starredRepositories()
                .compose(this.<List<Repository>>bindUntilEvent(FragmentEvent.STOP))
                .subscribe(_repositories -> {
                    binding.progressLoadingStarredRepository.setVisibility(View.GONE);
                    repositoryAdapter.clear();
                    repositoryAdapter.addAll(_repositories);
                    repositoryAdapter.notifyDataSetChanged();
                }, throwable -> {
                    binding.progressLoadingStarredRepository.setVisibility(View.GONE);
                    Timber.tag(TAG).e(throwable, null);
                });
    }

    private void setupStarredList(RecyclerView recyclerView) {
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(repositoryAdapter);
    }
}
