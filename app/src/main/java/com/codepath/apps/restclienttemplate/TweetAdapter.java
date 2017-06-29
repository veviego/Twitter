package com.codepath.apps.restclienttemplate;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.codepath.apps.restclienttemplate.models.Tweet;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONObject;
import org.parceler.Parcels;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

import cz.msebera.android.httpclient.Header;
import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

/**
 * Created by veviego on 6/26/17.
 */

public class TweetAdapter extends RecyclerView.Adapter<TweetAdapter.ViewHolder> {

    TwitterClient client;
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
        client = TwitterApplication.getRestClient();
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
        final String userName = "@" + tweet.user.screenName;

        holder.tvUserName.setText(userName);
        holder.tvName.setText(tweet.user.name);
        holder.tvTime.setText(getRelativeTimeAgo(tweet.createdAt));


        holder.tvBody.setText(tweet.body);

        // load profile image with glide
        Glide.with(context)
                .load(tweet.user.profileImageUrl)
                .bitmapTransform(new RoundedCornersTransformation(context, 15, 0))

                .into(holder.ivProfileImage);

        // load media using glide
        Glide.with(context)
                .load(tweet.entity.media_url)
                .bitmapTransform(new RoundedCornersTransformation(context, 25, 0))
                .into(holder.ivMedia);

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
            }
        });

        // retweet and unretweet
        holder.ibReTweet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get tweet id and call retweet
                client.reTweet(tweet.uid, tweet.retweeted, new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {

                        // Change status of tweet without refreshing
                        tweet.setRetweeted(!tweet.retweeted);

                        // Just log a success, no need to send a new tweet to top of feed
                        // TODO -- change the view so it says you've retweeted that individual tweet
                        Log.i("Retweet/Unretweet", "success");

                        if (tweet.retweeted) {
                            Toast.makeText(context, "Retweeted", Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(context, "Unretweeted", Toast.LENGTH_LONG).show();
                        }

                        // Notify the adapter that a new tweet has been inserted and scroll to top
                        // mTweets.add(0, retweeted);
                        // notifyItemInserted(0);
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                        Log.d("TwitterClient", errorResponse.toString());
                        throwable.printStackTrace();
                    }
                });
            }
        });

        // favorite and unfavorite
        holder.ibFavorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get tweet id and call favorite
                client.favorite(tweet.uid, tweet.favorited, new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {

                        // Change status of tweet without refreshing
                        tweet.setFavorited(!tweet.favorited);

                        // Just log a success, no need to send a new tweet to top of feed
                        // TODO -- change the view so it says you've retweeted that individual tweet
                        Log.i("Favorite/Unfavorite", "success");

                        if (tweet.retweeted) {
                            Toast.makeText(context, "Favorited", Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(context, "Unfavorited", Toast.LENGTH_LONG).show();
                        }

                        // Notify the adapter that a new tweet has been inserted and scroll to top
                        // mTweets.add(0, retweeted);
                        // notifyItemInserted(0);
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                        Log.d("TwitterClient", errorResponse.toString());
                        throwable.printStackTrace();
                    }
                });
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
        public TextView tvName;
        public TextView tvBody;
        public TextView tvTime;
        public ImageButton ibReply;
        public ImageButton ibReTweet;
        public ImageButton ibFavorite;
        public ImageView ivMedia;

        public ViewHolder(final View itemView) {
            super(itemView);
            final Context cxt = itemView.getContext();

            // perform findViewByID lookups
            ivProfileImage = (ImageView) itemView.findViewById(R.id.ivProfileImage);
            tvUserName = (TextView) itemView.findViewById(R.id.tvUserName);
            tvName = (TextView) itemView.findViewById(R.id.tvName);
            tvBody = (TextView) itemView.findViewById(R.id.tvBody);
            tvTime = (TextView) itemView.findViewById(R.id.tvTime);
            ibReply = (ImageButton) itemView.findViewById(R.id.ibReply);
            ibReTweet = (ImageButton) itemView.findViewById(R.id.ibReTweet);
            ibFavorite = (ImageButton) itemView.findViewById(R.id.ibFavorite);
            ivMedia = (ImageView) itemView.findViewById(R.id.ivMedia);

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

    // Time method for tweet labels
    public String getRelativeTimeAgo(String rawJsonDate) {
        String twitterFormat = "EEE MMM dd HH:mm:ss ZZZZZ yyyy";
        SimpleDateFormat sf = new SimpleDateFormat(twitterFormat, Locale.ENGLISH);
        sf.setLenient(true);

        String relativeDate = "";
        try {
            long dateMillis = sf.parse(rawJsonDate).getTime();
            relativeDate = DateUtils.getRelativeTimeSpanString(dateMillis,
                    System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS).toString();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        // tokenize string to get number
        String[] components = relativeDate.split(" ");
        String number = components[0];

        // search to see if it contains minutes, days, hours, or seconds
        for (int i = 0; i < components.length; i++) {
            switch (components[i]) {
                case "second":
                    relativeDate = number + "s";
                    break;
                case "seconds":
                    relativeDate = number + "s";
                    break;
                case "minute":
                    relativeDate = number + "m";
                    break;
                case "minutes":
                    relativeDate = number + "m";
                    break;
                case "day":
                    relativeDate = number + "d";
                    break;
                case "days":
                    relativeDate = number + "d";
                    break;
            }
        }


        return relativeDate;
    }
}
