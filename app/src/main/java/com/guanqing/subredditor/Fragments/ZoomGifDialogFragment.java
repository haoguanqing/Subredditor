package com.guanqing.subredditor.Fragments;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.guanqing.subredditor.R;
import com.guanqing.subredditor.UI.GifView;
import com.guanqing.subredditor.UI.UpvoteTextSwitcher;
import com.guanqing.subredditor.Util.Constants;

import fr.castorflex.android.smoothprogressbar.SmoothProgressBar;

/**
 * Created by Guanqing on 2015/12/4.
 */
public class ZoomGifDialogFragment extends android.app.DialogFragment {

    static int[] screenSize;
    UpvoteTextSwitcher tsUpvote;
    GifView gifView;
    SmoothProgressBar progressBar;
    TextView tvComments;
    ImageView ivUpvotes;

    public static ZoomGifDialogFragment newInstance(){
        //TODO: new instance
        ZoomGifDialogFragment fragment = new ZoomGifDialogFragment();
        Bundle bundle = new Bundle();
        return fragment;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        screenSize = Constants.getScreenSizeInPixels(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_gif_dialog, container, false);
        gifView = (GifView) view.findViewById(R.id.ivGifThumbnail_detail);
        progressBar = (SmoothProgressBar) view.findViewById(R.id.pbSmooth_detail);
        tvComments = (TextView) view.findViewById(R.id.tvComments_detail);
        tsUpvote = (UpvoteTextSwitcher) view.findViewById(R.id.tsUpvotesCounter_detail);
        ivUpvotes = (ImageView) view.findViewById(R.id.ivUpvotes);

        ivUpvotes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ivUpvotes.setImageDrawable(
                        ivUpvotes.getDrawable()==getResources().getDrawable(R.drawable.ic_arrow_up) ?
                                getResources().getDrawable(R.drawable.ic_arrow_up_blue) : getResources().getDrawable(R.drawable.ic_arrow_up));
                tsUpvote.performClick();
            }
        });

        gifView.setMovieResource(R.drawable.imgur_example);
        tsUpvote.setListener(1989);
        return view;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        return dialog;
    }
    @Override
    public void onResume() {
        super.onResume();
        int width = screenSize[0] * 10/11;
        int height = screenSize[1] * 10/11;

        if (getDialog() == null)
            return;
        if (getResources().getConfiguration().orientation==1){
            getDialog().getWindow().setLayout(width, WindowManager.LayoutParams.WRAP_CONTENT);
        }else{
            getDialog().getWindow().setLayout(height, width);
        }
    }
}
