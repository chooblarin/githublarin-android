package com.chooblarin.githublarin.ui.fragment;

import android.app.Activity;
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
import com.chooblarin.githublarin.databinding.FragmentGistBinding;
import com.chooblarin.githublarin.model.Gist;
import com.chooblarin.githublarin.ui.activity.GistDetailActivity;
import com.chooblarin.githublarin.ui.adapter.GistAdapter;
import com.chooblarin.githublarin.ui.listener.OnItemClickListener;
import com.trello.rxlifecycle.FragmentEvent;

import java.util.List;

import timber.log.Timber;

public class GistFragment extends BaseFragment implements OnItemClickListener {

    private GitHubApiClient apiClient;
    private GistAdapter gistAdapter;

    FragmentGistBinding binding;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        gistAdapter = new GistAdapter(activity);
        gistAdapter.setOnItemClickListener(this);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_gist, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupGistListView(binding.recyclerviewGist);
        setup();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        binding.unbind();
    }

    @Override
    protected void setupComponent() {
        apiClient = Application.get(getContext()).getAppComponent().apiClient();
    }

    private void setup() {
        binding.progressLoadingGist.setVisibility(View.VISIBLE);

        apiClient.gists()
                .compose(this.<List<Gist>>bindUntilEvent(FragmentEvent.STOP))
                .subscribe(_gists -> {
                    binding.progressLoadingGist.setVisibility(View.GONE);
                    gistAdapter.clear();
                    gistAdapter.addAll(_gists);
                    gistAdapter.notifyDataSetChanged();
                }, throwable -> {
                    binding.progressLoadingGist.setVisibility(View.GONE);
                    Timber.e(throwable, null);
                });
    }

    private void setupGistListView(RecyclerView recyclerView) {
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(gistAdapter);
    }

    @Override
    public void onItemClick(View view, int position) {
        Gist gist = gistAdapter.getItem(position);
        startActivity(GistDetailActivity.createIntent(getActivity(), gist));
    }
}
