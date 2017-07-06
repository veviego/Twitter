package com.codepath.apps.restclienttemplate;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by veviego on 6/29/17.
 */

public class TweetDialog extends DialogFragment {

    public ImageView ivProfileImage;
    public TextView tvUserName;
    public TextView tvName;
    public TextView tvBody;
    public TextView tvTime;
    public ImageButton ibReply;
    public ImageButton ibReTweet;
    public ImageButton ibFavorite;
    public ImageView ivMedia;

    public TimelineActivity activity;

    public TweetDialog() {
        // Empty constructor is required for DialogFragment
        // Make sure not to add arguments to the constructor
        // Use `newInstance` instead as shown below
    }

    public static TweetDialog newInstance(String title) {
        TweetDialog frag = new TweetDialog();
        Bundle args = new Bundle();
        args.putString("title", title);
        frag.setArguments(args);
        return frag;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Get a reference to the activity
        activity = (TimelineActivity) getActivity();




        return inflater.inflate(R.layout.fragment_tweet_info, container);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // perform findViewByID lookups from view
        ivProfileImage = (ImageView) view.findViewById(R.id.ivUserProfileImage);
        tvUserName = (TextView) view.findViewById(R.id.tvUserScreenName);
        tvName = (TextView) view.findViewById(R.id.tvUserName);
        tvBody = (TextView) view.findViewById(R.id.tvUserTagline);
        tvTime = (TextView) view.findViewById(R.id.tvTime);
        ibReply = (ImageButton) view.findViewById(R.id.ibReply);
        ibReTweet = (ImageButton) view.findViewById(R.id.ibReTweet);
        ibFavorite = (ImageButton) view.findViewById(R.id.ibFavorite);
        ivMedia = (ImageView) view.findViewById(R.id.ivUserMedia);



        getDialog().getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
    }

}
