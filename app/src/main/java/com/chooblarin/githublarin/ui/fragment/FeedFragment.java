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
import com.chooblarin.githublarin.ui.activity.RepositoryDetailActivity;
import com.chooblarin.githublarin.ui.adapter.FeedAdapter;
import com.chooblarin.githublarin.ui.listener.OnItemClickListener;
import com.trello.rxlifecycle.FragmentEvent;

import java.util.List;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import timber.log.Timber;

public class FeedFragment extends BaseFragment implements OnItemClickListener {

    final static private int PAGE_SIZE = 30;

    private GitHubApiClient apiClient;
    private FeedAdapter feedAdapter;
    private FragmentFeedBinding binding;
    private int currentPage = 1;
    private boolean isLoading;
    private boolean isLastPage;

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
        if (isLoading) {
            return;
        }
        Feed feed = feedAdapter.getItem(position);
        startActivity(RepositoryDetailActivity.createIntent(getActivity(), feed.link));
    }

    private void setupSwipeRefreshLayout(SwipeRefreshLayout swipeRefreshLayout) {
        swipeRefreshLayout.setOnRefreshListener(() -> {
            currentPage = 1;
            feedAdapter.clear();
            loadMoreFeeds();
        });
    }

    private void setupFeedListView(RecyclerView recyclerView) {
        recyclerView.setHasFixedSize(true);
        final LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(feedAdapter);
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                int visibleCount = layoutManager.getChildCount();
                int totalCount = layoutManager.getItemCount();
                int firstVisibilityPosition = layoutManager.findFirstVisibleItemPosition();
                if (isLoading || isLastPage) {
                    return;
                }

                if (totalCount <= (firstVisibilityPosition + visibleCount)
                        && 0 <= firstVisibilityPosition
                        && PAGE_SIZE <= totalCount) {
                    loadMoreFeeds();
                }
            }
        });
        loadMoreFeeds();
    }

    private void loadMoreFeeds() {
        isLoading = true;
        binding.swipeRefreshFeed.setRefreshing(true);

        apiClient.feeds(currentPage)
                .compose(this.<List<Feed>>bindUntilEvent(FragmentEvent.STOP))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(feeds -> {
                    isLoading = false;
                    binding.swipeRefreshFeed.setRefreshing(false);

                    int feedSize = feeds.size();
                    isLastPage = feedSize < PAGE_SIZE;
                    feedAdapter.addAll(feeds);
                    feedAdapter.notifyItemRangeInserted(feedAdapter.getItemCount(), feedSize);
                    currentPage++;

                }, throwable -> {
                    isLoading = false;
                    binding.swipeRefreshFeed.setRefreshing(false);
                    Timber.e(throwable, null);
                });
    }
}
