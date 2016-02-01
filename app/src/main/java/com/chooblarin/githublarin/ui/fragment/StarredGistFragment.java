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
import com.chooblarin.githublarin.databinding.FragmentStarredGistFragmentBinding;
import com.chooblarin.githublarin.model.Gist;
import com.chooblarin.githublarin.ui.activity.GistDetailActivity;
import com.chooblarin.githublarin.ui.adapter.GistAdapter;
import com.chooblarin.githublarin.ui.listener.OnItemClickListener;
import com.trello.rxlifecycle.FragmentEvent;

import java.util.List;

import timber.log.Timber;

public class StarredGistFragment extends BaseFragment implements OnItemClickListener {

    private GitHubApiClient apiClient;
    private GistAdapter gistAdapter;

    FragmentStarredGistFragmentBinding binding;

    @Override
    protected void setupComponent() {
        apiClient = Application.get(getContext()).getAppComponent().apiClient();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil
                .inflate(inflater, R.layout.fragment_starred_gist_fragment, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupGistListView(binding.recyclerviewStarredGist);
    }

    @Override
    public void onResume() {
        super.onResume();
        loadStarredGist();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        binding.unbind();
    }

    private void setupGistListView(RecyclerView recyclerView) {
        gistAdapter = new GistAdapter(getActivity());
        gistAdapter.setOnItemClickListener(this);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(gistAdapter);
    }

    private void loadStarredGist() {
        binding.progressLoadingStarredGist.setVisibility(View.VISIBLE);
        apiClient.starredGists()
                .compose(this.<List<Gist>>bindUntilEvent(FragmentEvent.STOP))
                .subscribe(_gists -> {
                    binding.progressLoadingStarredGist.setVisibility(View.GONE);
                    gistAdapter.clear();
                    gistAdapter.addAll(_gists);
                    gistAdapter.notifyDataSetChanged();
                }, throwable -> {
                    binding.progressLoadingStarredGist.setVisibility(View.GONE);
                    Timber.e(throwable, null);
                });
    }

    @Override
    public void onItemClick(View view, int position) {
        Gist gist = gistAdapter.getItem(position);
        startActivity(GistDetailActivity.createIntent(getActivity(), gist));
    }
}
