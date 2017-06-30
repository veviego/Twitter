package com.codepath.apps.restclienttemplate;

import android.app.Activity;
import android.content.Context;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.Selection;
import android.text.TextWatcher;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.codepath.apps.restclienttemplate.models.Tweet;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;

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


        if (!(tweet.entity.media_url == null)) {
            // load media using glide
            Glide.with(context)
                    .load(tweet.entity.media_url)
                    .bitmapTransform(new RoundedCornersTransformation(context, 25, 0))
                    .into(holder.ivMedia);
        }

        holder.ibReply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Inflate the compose dialog
                View composeView = LayoutInflater.from(context).inflate(R.layout.activity_compose, null);
                // Create the Alert Dialog Builder
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
                // Set the view that the alert dialog builder should create
                alertDialogBuilder.setView(composeView);
                final AlertDialog replyAlertDialog = alertDialogBuilder.create();

                // Get EditText for tweet body and set listener
                final EditText message = (EditText) composeView.findViewById(R.id.etMessageBox);
                final TextView charCount = (TextView) composeView.findViewById(R.id.tvCharCount);
                message.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {

                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        int available = 140 - message.getText().toString().length();
                        String num = available + " / 140";
                        charCount.setText(num);
                    }
                });

                String userName = "@" + tweet.user.screenName + " ";
                message.setText(userName);

                // set cursor to after username
                int position = message.length();
                Editable mtext = message.getText();
                Selection.setSelection(mtext, position);

                // Set userID for later
                final long statusID = tweet.uid;

                // Cancel button to close modal
                Button btCancel = (Button) composeView.findViewById(R.id.btCancelButton);
                btCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        replyAlertDialog.cancel();
                    }
                });

                // Reply retweet button
                Button btTweetButton = (Button) composeView.findViewById(R.id.btTweetButton);
                btTweetButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final String tweetBody = message.getText().toString();

                        // Post a new tweet
                        client.sendTweet(tweetBody, context.getString(R.string.reply_param_key), statusID, new JsonHttpResponseHandler() {
                            @Override
                            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                                try {

                                    Tweet posted = Tweet.fromJSON(response);

                                    // Notify the adapter that a new tweet has been inserted and scroll to top
                                    mTweets.add(0, posted);
                                    notifyItemInserted(0);

                                    RecyclerView temp = (RecyclerView) ((Activity) context).findViewById(R.id.rvTweet);
                                    temp.scrollToPosition(0);

                                    replyAlertDialog.cancel();

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }

                            @Override
                            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                                Log.d("Compose Message", errorResponse.toString());
                            }
                        });
                    }
                });


                // Open the dialog and hide the progress bar
                replyAlertDialog.show();
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

                        if (tweet.favorited) {
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
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
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

            // Set the onclick listener for the recycler
            itemView.setOnClickListener(this);

        }

        // when the user clicks on a row, show MovieDetailsActivity for the selected movie
        @Override
        public void onClick(View v) {
            // gets item position
            int position = getAdapterPosition();
            // make sure the position is valid, i.e. actually exists in the view
            if (position != RecyclerView.NO_POSITION) {
                // get the movie at the position, this won't work if the class is static
                Tweet tweet = mTweets.get(position);

                // Tweet
                showTweetDetailsDialog(tweet);

            }
        }

        private void showTweetDetailsDialog(final Tweet dialogTweet) {
            // Inflate the tweet details fragment
            View tweetDetails = LayoutInflater.from(context).inflate(R.layout.fragment_tweet_info, null);

            ImageView ivProfileImage;
            TextView tvUserName;
            TextView tvName;
            TextView tvBody;
            TextView tvTime;
            ImageButton ibReply;
            ImageButton ibReTweet;
            ImageButton ibFavorite;
            ImageView ivMedia;

            // perform findViewByID lookups
            ivProfileImage = (ImageView) tweetDetails.findViewById(R.id.ivProfileImage);
            tvUserName = (TextView) tweetDetails.findViewById(R.id.tvUserName);
            tvName = (TextView) tweetDetails.findViewById(R.id.tvName);
            tvBody = (TextView) tweetDetails.findViewById(R.id.tvBody);
            tvTime = (TextView) tweetDetails.findViewById(R.id.tvTime);
            ibReply = (ImageButton) tweetDetails.findViewById(R.id.ibReply);
            ibReTweet = (ImageButton) tweetDetails.findViewById(R.id.ibReTweet);
            ibFavorite = (ImageButton) tweetDetails.findViewById(R.id.ibFavorite);
            ivMedia = (ImageView) tweetDetails.findViewById(R.id.ivMedia);


            // populate the views according to this data
            final String userName = "@" + dialogTweet.user.screenName;

            tvUserName.setText(userName);
            tvName.setText(dialogTweet.user.name);
            tvTime.setText(getRelativeTimeAgo(dialogTweet.createdAt));
            tvBody.setText(dialogTweet.body);

            // load profile image with glide
            Glide.with(context)
                    .load(dialogTweet.user.profileImageUrl)
                    .bitmapTransform(new RoundedCornersTransformation(context, 15, 0))

                    .into(ivProfileImage);

            // load media using glide
            Glide.with(context)
                    .load(dialogTweet.entity.media_url)
                    .bitmapTransform(new RoundedCornersTransformation(context, 25, 0))
                    .into(ivMedia);


            // Create an alert dialog builder
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);

            // Set the view of the alert Dialog
            alertDialogBuilder.setView(tweetDetails);



            // Create the alert dialog
            final AlertDialog alertDialog = alertDialogBuilder.create();


            ibReply.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    // Inflate the compose dialog
                    View composeView = LayoutInflater.from(context).inflate(R.layout.activity_compose, null);
                    // Create the Alert Dialog Builder
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
                    // Set the view that the alert dialog builder should create
                    alertDialogBuilder.setView(composeView);
                    final AlertDialog replyAlertDialog = alertDialogBuilder.create();

                    // Get EditText for tweet body and set listener
                    final EditText message = (EditText) composeView.findViewById(R.id.etMessageBox);
                    final TextView charCount = (TextView) composeView.findViewById(R.id.tvCharCount);
                    message.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                        }

                        @Override
                        public void onTextChanged(CharSequence s, int start, int before, int count) {

                        }

                        @Override
                        public void afterTextChanged(Editable s) {
                            int available = 140 - message.getText().toString().length();
                            String num = available + " / 140";
                            charCount.setText(num);
                        }
                    });

                    String userName = "@" + dialogTweet.user.screenName + " ";
                    message.setText(userName);

                    // set cursor to after username
                    int position = message.length();
                    Editable mtext = message.getText();
                    Selection.setSelection(mtext, position);

                    // Set userID for later
                    final long statusID = dialogTweet.uid;


                    // Cancel button to close modal
                    Button btCancel = (Button) composeView.findViewById(R.id.btCancelButton);
                    btCancel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            replyAlertDialog.cancel();
                        }
                    });

                    // Reply tweet button
                    Button btTweetButton = (Button) composeView.findViewById(R.id.btTweetButton);
                    btTweetButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            alertDialog.cancel();
                            final String tweetBody = message.getText().toString();

                            // Post a new tweet
                            client.sendTweet(tweetBody, context.getString(R.string.reply_param_key), statusID, new JsonHttpResponseHandler() {
                                @Override
                                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                                    try {

                                        Tweet posted = Tweet.fromJSON(response);

                                        // Notify the adapter that a new tweet has been inserted and scroll to top
                                        mTweets.add(0, posted);
                                        notifyItemInserted(0);

                                        RecyclerView temp = (RecyclerView) ((Activity) context).findViewById(R.id.rvTweet);
                                        temp.scrollToPosition(0);

                                        replyAlertDialog.cancel();

                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }

                                @Override
                                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                                    Log.d("Compose Message", errorResponse.toString());
                                }
                            });
                        }
                    });


                    // Open the dialog and hide the progress bar
                    replyAlertDialog.show();
                }
            });

            // retweet and unretweet
            ibReTweet.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Get tweet id and call retweet
                    client.reTweet(dialogTweet.uid, dialogTweet.retweeted, new JsonHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {

                            // Change status of tweet without refreshing
                            dialogTweet.setRetweeted(!dialogTweet.retweeted);

                            // Just log a success, no need to send a new tweet to top of feed
                            // TODO -- change the view so it says you've retweeted that individual tweet
                            Log.i("Retweet/Unretweet", "success");

                            if (dialogTweet.retweeted) {
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
            ibFavorite.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Get tweet id and call favorite
                    client.favorite(dialogTweet.uid, dialogTweet.favorited, new JsonHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {

                            // Change status of tweet without refreshing
                            dialogTweet.setFavorited(!dialogTweet.favorited);

                            // Just log a success, no need to send a new tweet to top of feed
                            // TODO -- change the view so it says you've retweeted that individual tweet
                            Log.i("Favorite/Unfavorite", "success");

                            if (dialogTweet.favorited) {
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




            // Display the tweet details
            alertDialog.show();


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
