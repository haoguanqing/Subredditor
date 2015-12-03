package com.guanqing.subredditor;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.guanqing.subredditor.UI.GifView;
import com.guanqing.subredditor.UI.UpvoteTextSwitcher;


public class FrontPageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private int itemsCount = 0;

    public FrontPageAdapter(Context context) {
        this.context = context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(context).inflate(R.layout.list_item_feed, parent, false);

        return new FrontPageFeedViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        final FrontPageFeedViewHolder holder = (FrontPageFeedViewHolder) viewHolder;
        bindDefaultFeedItem(position, holder);
    }

    private void bindDefaultFeedItem(int position, final FrontPageFeedViewHolder holder) {
        if (position % 3 == 0) {
            setVIewVisibility(holder, false);
            holder.ivThumbnail.setImageUrl("http://i.imgur.com/6c0N9I3.jpg",
                    ImageLoaderHelper.getInstance(context).getImageLoader());

        } else if (position % 3 == 1) {
            setVIewVisibility(holder, false);
            holder.ivThumbnail.setImageUrl("http://i.imgur.com/UbeMyiy.jpg?1",
                    ImageLoaderHelper.getInstance(context).getImageLoader());

        }else{
            setVIewVisibility(holder, true);
            //Movie movie = (Movie) context.getResources().getDrawable(R.drawable.loading);
            holder.gifView.setMovieResource(R.drawable.imgur_example);
        }

        holder.btnComments.setTag(position);
        holder.ivThumbnail.setTag(holder);
        holder.tsUpvotesCounter.setTag(holder);
        holder.tsUpvotesCounter.setListener(978);
    }

    private void setVIewVisibility(final FrontPageFeedViewHolder holder, boolean isGif){
        holder.gifView.setVisibility(isGif ? View.VISIBLE : View.GONE);
        holder.ivThumbnail.setVisibility(isGif ? View.GONE: View.VISIBLE);
        holder.ivGifIcon.setVisibility(isGif ? View.VISIBLE : View.GONE);
    }

    public void updateItems() {
        itemsCount += 10;
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        return 1;
    }

    @Override
    public int getItemCount() {
        return itemsCount;
    }

    public static class FrontPageFeedViewHolder extends RecyclerView.ViewHolder {
        com.guanqing.subredditor.UI.DynamicHeightNetworkImageView ivThumbnail;
        TextView ivFeedBottom;
        ImageButton btnComments;
        ImageButton btnSave;
        UpvoteTextSwitcher tsUpvotesCounter;
        GifView gifView;
        ImageView ivGifIcon;

        public FrontPageFeedViewHolder(View view) {
            super(view);

            ivThumbnail = (com.guanqing.subredditor.UI.DynamicHeightNetworkImageView) view.findViewById(R.id.ivFeedThumbnail);
            ivFeedBottom = (TextView) view.findViewById(R.id.tvFeedTitle);
            btnComments = (ImageButton) view.findViewById(R.id.btnComments);
            btnSave = (ImageButton) view.findViewById(R.id.btnSave);
            tsUpvotesCounter = (UpvoteTextSwitcher) view.findViewById(R.id.tsUpvotesCounter);
            gifView = (GifView) view.findViewById(R.id.ivGifThumbnail);
            ivGifIcon = (ImageView)view.findViewById(R.id.ivGifIcon);
        }
    }
}
