package com.guanqing.subredditor.Activities;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import com.google.common.eventbus.EventBus;
import com.guanqing.subredditor.Events.FinishAuthenticateEvent;
import com.guanqing.subredditor.FrontPageAdapter;
import com.guanqing.subredditor.MyMenuFragment;
import com.guanqing.subredditor.R;
import com.mxn.soul.flowingdrawer_core.FlowingView;
import com.mxn.soul.flowingdrawer_core.LeftDrawerLayout;

import net.dean.jraw.RedditClient;
import net.dean.jraw.models.Listing;
import net.dean.jraw.models.Submission;
import net.dean.jraw.paginators.Sorting;
import net.dean.jraw.paginators.SubredditPaginator;
import net.dean.jraw.paginators.TimePeriod;


public class MainActivity extends BaseActivity {

    private RecyclerView rvFeed;
    private LeftDrawerLayout mLeftDrawerLayout;
    private NavigationView mNavigationView;
    private EventBus eventBus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setupToolbar();

        mLeftDrawerLayout = (LeftDrawerLayout) findViewById(R.id.id_drawerlayout);
        rvFeed = (RecyclerView) findViewById(R.id.rvFeed);

        FragmentManager fm = getSupportFragmentManager();
        MyMenuFragment mMenuFragment = (MyMenuFragment) fm.findFragmentById(R.id.id_container_menu);
        mNavigationView = (NavigationView) findViewById(R.id.vNavigation);
        FlowingView mFlowingView = (FlowingView) findViewById(R.id.sv);
        if (mMenuFragment == null) {
            fm.beginTransaction().add(R.id.id_container_menu, mMenuFragment = new MyMenuFragment()).commit();
        }
        mLeftDrawerLayout.setFluidView(mFlowingView);
        mLeftDrawerLayout.setMenuFragment(mMenuFragment);
        setupFeed();
    }

    protected void setupToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_menu_white);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mLeftDrawerLayout.toggle();
            }
        });

    }

    private void setupFeed() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this) {
            @Override
            protected int getExtraLayoutSpace(RecyclerView.State state) {
                return 300;
            }
        };
        rvFeed.setLayoutManager(linearLayoutManager);

        FrontPageAdapter feedAdapter = new FrontPageAdapter(this);
        rvFeed.setAdapter(feedAdapter);
        feedAdapter.updateItems();
    }

    @Override
    public void onBackPressed() {
        if (mLeftDrawerLayout.isShownMenu()) {
            mLeftDrawerLayout.closeDrawer();
        } else {
            super.onBackPressed();
        }
    }


    public void onEvent(FinishAuthenticateEvent event){
        new FrontPageRetrieveTask(event.getRedditClient()).execute();
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
                }

            }catch (Exception e){
                Log.e("HGQ", "retrieve task failed");
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void v) {
        }
    }
}
