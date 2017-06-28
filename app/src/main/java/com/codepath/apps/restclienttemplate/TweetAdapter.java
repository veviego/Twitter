package com.codepath.apps.restclienttemplate;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.codepath.apps.restclienttemplate.models.Tweet;

import org.parceler.Parcels;

import java.util.List;

/**
 * Created by veviego on 6/26/17.
 */

public class TweetAdapter extends RecyclerView.Adapter<TweetAdapter.ViewHolder> {

    private List<Tweet> mTweets;
    Context context;

    // Constants
    private final int REP_REQUEST_CODE = 40;

    // pass in Tweets array in the constructor
    public TweetAdapter(List<Tweet> tweets) {
        mTweets = tweets;
    }

    // for each row, inflate the layout and cache references into ViewHolder
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View tweetView = inflater.inflate(R.layout.item_tweet, parent, false);
        ViewHolder viewHolder = new ViewHolder(tweetView);
        return viewHolder;
    }

    // bind values based on position of the element
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // get data according to position
        final Tweet tweet = mTweets.get(position);

        // populate the views according to this data
        holder.tvUserName.setText(tweet.user.name);
        holder.tvBody.setText(tweet.body);

        Glide.with(context).load(tweet.user.profileImageUrl).into(holder.ivProfileImage);

        holder.ibReply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create intent
                Intent i = new Intent(context, ComposeActivity.class);

                // Attach tweet to intent
                i.putExtra("currentTweet", Parcels.wrap(tweet));

                // Add request code to intent
                i.putExtra("request_code", REP_REQUEST_CODE);

                // Launch compose activity and expect a result
                ((Activity) context).startActivityForResult(i, REP_REQUEST_CODE);


                Toast.makeText(context,"test", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return mTweets.size();
    }

    // create ViewHolder class
    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView ivProfileImage;
        public TextView tvUserName;
        public TextView tvBody;
        public ImageButton ibReply;

        public ViewHolder(final View itemView) {
            super(itemView);
            final Context cxt = itemView.getContext();

            // perform findViewByID lookups
            ivProfileImage = (ImageView) itemView.findViewById(R.id.ivProfileImage);
            tvUserName = (TextView) itemView.findViewById(R.id.tvUserName);
            tvBody = (TextView) itemView.findViewById(R.id.tvBody);
            ibReply = (ImageButton) itemView.findViewById(R.id.ibReply);
        }
    }


    // Clean all elements of the recycler
    public void clear() {
        mTweets.clear();
        notifyDataSetChanged();
    }

    // Add a list of items -- change to type used
    public void addAll(List<Tweet> list) {
        mTweets.addAll(list);
        notifyDataSetChanged();
    }

    // Getter for reply request code

    public int getREP_REQUEST_CODE() {
        return REP_REQUEST_CODE;
    }
}
