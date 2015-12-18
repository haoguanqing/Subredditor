package com.guanqing.subredditor.Fragments;


import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.guanqing.subredditor.Events.FinishLoginActivityEvent;
import com.guanqing.subredditor.FrontPageAdapter;
import com.guanqing.subredditor.R;
import com.guanqing.subredditor.Util.NetworkUtil;
import com.guanqing.subredditor.Util.ToastUtil;

import net.dean.jraw.RedditClient;
import net.dean.jraw.models.Listing;
import net.dean.jraw.models.Submission;
import net.dean.jraw.paginators.Sorting;
import net.dean.jraw.paginators.SubredditPaginator;
import net.dean.jraw.paginators.TimePeriod;

import cn.bingoogolapple.refreshlayout.BGARefreshLayout;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MainFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MainFragment extends BaseFragment implements BGARefreshLayout.BGARefreshLayoutDelegate{

    private BGARefreshLayout mRefreshLayout;
    private RecyclerView rvFeed;
    private boolean mIsNetworkEnabled;

    public static MainFragment newInstance() {
        MainFragment fragment = new MainFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mIsNetworkEnabled = NetworkUtil.isInternetConnected(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_main, container, false);
        mRefreshLayout = (BGARefreshLayout) view.findViewById(R.id.rlBGARefresh);
        rvFeed = (RecyclerView)view.findViewById(R.id.rvFeed);
        setupFeed();
        return view;
    }

    private void setupFeed() {
        //set adapter
        FrontPageAdapter feedAdapter = new FrontPageAdapter(getActivity());
        rvFeed.setAdapter(feedAdapter);
        feedAdapter.updateItems();
        //set layout to be staggeredGridLayout
        int columnCount = getResources().getInteger(R.integer.list_column_count);
        StaggeredGridLayoutManager sglm =
                new StaggeredGridLayoutManager(columnCount, StaggeredGridLayoutManager.VERTICAL);
        rvFeed.setLayoutManager(sglm);
    }

    @Override
    public void onBGARefreshLayoutBeginRefreshing(BGARefreshLayout refreshLayout) {
        // refresh and load more data

        if (mIsNetworkEnabled) {
            // if network is available, load data
            new AsyncTask<Void, Void, Void>() {

                @Override
                protected Void doInBackground(Void... params) {
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    return null;
                }

                @Override
                protected void onPostExecute(Void aVoid) {
                    mRefreshLayout.endRefreshing();
                    //mDatas.addAll(0, DataEngine.loadNewData());
                    //mAdapter.setDatas(mDatas);
                }
            }.execute();
        } else {
            // network unavailable, finish drag down refreshing
            ToastUtil.show(getActivity(), "Network unavailable");
            mRefreshLayout.endRefreshing();
        }
    }

    @Override
    public boolean onBGARefreshLayoutBeginLoadingMore(BGARefreshLayout refreshLayout) {
        // load more data

        if (mIsNetworkEnabled) {
            new AsyncTask<Void, Void, Void>() {

                @Override
                protected Void doInBackground(Void... params) {
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    return null;
                }

                @Override
                protected void onPostExecute(Void aVoid) {
                    // 加载完毕后在UI线程结束加载更多
                    mRefreshLayout.endLoadingMore();
                    //mAdapter.addDatas(DataEngine.loadMoreData());
                }
            }.execute();

            return true;
        } else {
            // network unavailable, return false
            ToastUtil.show(getActivity(), "Network unavailable");
            return false;
        }
    }

    // code access to begin refreshing
    public void beginRefreshing() {
        mRefreshLayout.beginRefreshing();
    }

    // code access to automatically load more
    public void beginLoadingMore() {
        mRefreshLayout.beginLoadingMore();
    }



    private static final class FrontPageRetrieveTask extends AsyncTask<Void, Void, Void> {
        RedditClient redditClient;
        public FrontPageRetrieveTask(RedditClient client){
            redditClient = client;
        }

        @Override
        protected Void doInBackground(Void... params) {
            try{
                SubredditPaginator frontPage = new SubredditPaginator(redditClient);

                // Adjust the request parameters
                frontPage.setLimit(50);                    // Default is 25 (Paginator.DEFAULT_LIMIT)
                frontPage.setTimePeriod(TimePeriod.MONTH); // Default is DAY (Paginator.DEFAULT_TIME_PERIOD)
                frontPage.setSorting(Sorting.HOT);         // Default is HOT (Paginator.DEFAULT_SORTING)
                // This Paginator is now set up to retrieve the highest-scoring links submitted within the past
                // month, 50 at a time

                // Since Paginator implements Iterator, you can use it just how you would expect to, using next() and hasNext()
                Listing<Submission> submissions = frontPage.next();
                for (Submission s : submissions) {
                    // Print some basic stats about the posts
                    Log.e("HGQ", "[/r/" + s.getSubredditName() + " - " + s.getScore() + " karma] " + s.getTitle() + "\n" + "https://www.reddit.com" + s.getPermalink() + "\n" + s.getThumbnail());
                    Log.e("HGQ", redditClient.getSubmission(s.getId()).getPermalink());
                }

            }catch (Exception e){
                e.printStackTrace();
                Log.e("HGQ", "retrieve task failed ");
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void v) {
        }
    }

    public void onEvent(FinishLoginActivityEvent event){
        new FrontPageRetrieveTask(event.getRedditClient()).execute();
    }
}
