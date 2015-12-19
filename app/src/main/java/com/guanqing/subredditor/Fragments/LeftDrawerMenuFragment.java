package com.guanqing.subredditor.Fragments;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.guanqing.subredditor.Activities.LoginActivity;
import com.guanqing.subredditor.Events.LoginEvent;
import com.guanqing.subredditor.R;
import com.guanqing.subredditor.UI.AnimatedExpandableListView.AnimatedExpandableListView;
import com.guanqing.subredditor.UI.AnimatedExpandableListView.ChildItem;
import com.guanqing.subredditor.UI.AnimatedExpandableListView.ExpandableListAdapter;
import com.guanqing.subredditor.UI.AnimatedExpandableListView.GroupItem;
import com.guanqing.subredditor.UI.CircleTransformation;
import com.guanqing.subredditor.Util.ImageUtil;
import com.guanqing.subredditor.Util.SharedPrefUtil;
import com.guanqing.subredditor.Util.ToastUtil;
import com.mxn.soul.flowingdrawer_core.MenuFragment;

import java.util.ArrayList;
import java.util.List;

import de.greenrobot.event.EventBus;


public class LeftDrawerMenuFragment extends MenuFragment {

    private Context context;
    private ImageView ivMenuUserProfilePhoto;
    private TextView tvUsername;
    private AnimatedExpandableListView expandableListView;
    private ExpandableListAdapter adapter;
    private ImageButton btnLogout;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getActivity();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_drawer, container, false);
        ivMenuUserProfilePhoto = (ImageView) view.findViewById(R.id.ivMenuUserProfilePhoto);
        tvUsername = (TextView) view.findViewById(R.id.ivUserProfileName);
        btnLogout = (ImageButton)view.findViewById(R.id.btnLogout);

        //create a listener to launch login activity
        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, LoginActivity.class);
                startActivity(intent);
            }
        };
        ivMenuUserProfilePhoto.setOnClickListener(listener);
        tvUsername.setOnClickListener(listener);
        setupProfileIcon();
        //logout
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //EventBus.getDefault().post(new LogoutEvent());
                logout();
            }
        });

        //set contents for expandable listview in the drawer
        expandableListView = (AnimatedExpandableListView)view.findViewById(R.id.lvExpandable);
        adapter = new ExpandableListAdapter(context);
        setAdapterData();
        expandableListView.setAdapter(adapter);
        //set on subreddit click listener
        expandableListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                final String selected = (adapter.getChild(
                        groupPosition, childPosition)).getTitle();
                ToastUtil.show(selected);
                return true;
            }
        });

        return setupReveal(view);
    }

    private void setupProfileIcon() {
        int avatarSize = getResources().getDimensionPixelSize(R.dimen.global_menu_avatar_size);
        Glide.with(getActivity())
                .load(R.drawable.avatar)
                .placeholder(R.drawable.img_circle_placeholder)
                .override(avatarSize, avatarSize)
                .error(R.drawable.avatar)
                .transform(new CircleTransformation(getActivity()))
                .into(ivMenuUserProfilePhoto);
    }

    private void setupProfileIcon(Uri uri) {
        int avatarSize = getResources().getDimensionPixelSize(R.dimen.global_menu_avatar_size);
        Drawable avatar = ImageUtil.getAvatarImage(context, uri);
        Glide.with(getActivity())
                .load(avatar)
                .placeholder(R.drawable.img_circle_placeholder)
                .override(avatarSize, avatarSize)
                .error(R.drawable.avatar)
                .transform(new CircleTransformation(getActivity()))
                .into(ivMenuUserProfilePhoto);
    }


    private void setAdapterData(){
        List<GroupItem> items = new ArrayList<GroupItem>();
        GroupItem item = new GroupItem("Subreddits");

        item.addChild(new ChildItem("GAMING"));
        item.addChild(new ChildItem("GIFS"));
        item.addChild(new ChildItem("AWW"));
        item.addChild(new ChildItem("SPORTS"));
        item.addChild(new ChildItem("PICS"));
        item.addChild(new ChildItem("PHILOSOPHY"));
        item.addChild(new ChildItem("60FPSPORN"));

        adapter.setData(items);
    }

    public void onEventMainThread(LoginEvent event){
        String username = SharedPrefUtil.getUserName();
        Uri uri = SharedPrefUtil.getAvatarFilePath();
        setupProfileIcon(uri);
        ivMenuUserProfilePhoto.invalidate();
        tvUsername.setText(username);
        tvUsername.invalidate();
    }

    private void logout(){
        SharedPrefUtil.logout();
        setupProfileIcon();
        ivMenuUserProfilePhoto.invalidate();
        tvUsername.setText("Login");
        tvUsername.invalidate();
        ToastUtil.show("Logout");
    }
}
