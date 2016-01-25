package com.chooblarin.githublarin.ui.fragment;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.chooblarin.githublarin.Application;
import com.chooblarin.githublarin.R;
import com.chooblarin.githublarin.api.client.GitHubApiClient;
import com.chooblarin.githublarin.databinding.FragmentFeedBinding;
import com.chooblarin.githublarin.model.Feed;
import com.chooblarin.githublarin.ui.adapter.FeedAdapter;
import com.chooblarin.githublarin.ui.listener.OnItemClickListener;
import com.trello.rxlifecycle.FragmentEvent;

import java.util.List;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class FeedFragment extends BaseFragment implements OnItemClickListener {

    private GitHubApiClient apiClient;
    FeedAdapter feedAdapter;
    FragmentFeedBinding binding;

    private int currentPage = 1;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        feedAdapter = new FeedAdapter(getActivity());
        feedAdapter.setOnItemClickListener(this);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_feed, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupSwipeRefreshLayout(binding.swipeRefreshFeed);
        setupFeedListView(binding.recyclerviewFeed);
        loadFeeds();
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

    @Override
    public void onItemClick(View view, int position) {
        // todo
    }

    private void setupSwipeRefreshLayout(SwipeRefreshLayout swipeRefreshLayout) {
        swipeRefreshLayout.setOnRefreshListener(() -> {
            currentPage = 1;
            loadFeeds();
        });
    }

    private void setupFeedListView(RecyclerView recyclerView) {
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(feedAdapter);
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                // todo: show more
            }
        });
    }

    private void loadFeeds() {
        apiClient.feeds(currentPage)
                .compose(this.<List<Feed>>bindUntilEvent(FragmentEvent.STOP))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(feeds -> {
                    binding.swipeRefreshFeed.setRefreshing(false);
                    currentPage++;
                    // todo: not yet works well
                    int count = feedAdapter.getItemCount();
                    feedAdapter.addAll(feeds);
                    feedAdapter.notifyItemRangeInserted(count, feeds.size());

                }, throwable -> {
                    binding.swipeRefreshFeed.setRefreshing(false);
                    throwable.printStackTrace();
                });
    }
}
