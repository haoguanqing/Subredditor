package com.guanqing.subredditor.activities;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import com.guanqing.subredditor.FeedAdapter;
import com.guanqing.subredditor.MyMenuFragment;
import com.guanqing.subredditor.R;
import com.mxn.soul.flowingdrawer_core.FlowingView;
import com.mxn.soul.flowingdrawer_core.LeftDrawerLayout;

import net.dean.jraw.models.Listing;
import net.dean.jraw.models.Submission;
import net.dean.jraw.paginators.Sorting;
import net.dean.jraw.paginators.SubredditPaginator;
import net.dean.jraw.paginators.TimePeriod;


public class MainActivity extends BaseActivity {

    private RecyclerView rvFeed;
    private LeftDrawerLayout mLeftDrawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setupToolbar();

        mLeftDrawerLayout = (LeftDrawerLayout) findViewById(R.id.id_drawerlayout);
        rvFeed = (RecyclerView) findViewById(R.id.rvFeed);

        FragmentManager fm = getSupportFragmentManager();
        MyMenuFragment mMenuFragment = (MyMenuFragment) fm.findFragmentById(R.id.id_container_menu);
        FlowingView mFlowingView = (FlowingView) findViewById(R.id.sv);
        if (mMenuFragment == null) {
            fm.beginTransaction().add(R.id.id_container_menu, mMenuFragment = new MyMenuFragment()).commit();
        }
        mLeftDrawerLayout.setFluidView(mFlowingView);
        mLeftDrawerLayout.setMenuFragment(mMenuFragment);
        setupFeed();
        findViewById(R.id.myFAB).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AccountRetrieveTask().execute();
            }
        });
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

        FeedAdapter feedAdapter = new FeedAdapter(this);
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

    private static final class AccountRetrieveTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            try{
                SubredditPaginator frontPage = new SubredditPaginator(LoginActivity.redditClient);
                // Adjust the request parameters
                frontPage.setLimit(50);                    // Default is 25 (Paginator.DEFAULT_LIMIT)
                frontPage.setTimePeriod(TimePeriod.MONTH); // Default is DAY (Paginator.DEFAULT_TIME_PERIOD)
                frontPage.setSorting(Sorting.TOP);         // Default is HOT (Paginator.DEFAULT_SORTING)
                // This Paginator is now set up to retrieve the highest-scoring links submitted within the past
                // month, 50 at a time

                // Since Paginator implements Iterator, you can use it just how you would expect to, using next() and hasNext()
                Listing<Submission> submissions = frontPage.next();
                for (Submission s : submissions) {
                    // Print some basic stats about the posts
                    Log.e("HGQ", "[/r/%s - %s karma] %s\n" + s.getSubredditName() + s.getScore() + s.getTitle());
                }

            }catch (Exception e){
                e.printStackTrace();
                Log.e("HGQ", "retrieve task failed");
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void v) {
        }
    }
}
