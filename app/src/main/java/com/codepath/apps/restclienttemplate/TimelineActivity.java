package com.codepath.apps.restclienttemplate;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.codepath.apps.restclienttemplate.models.Tweet;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcels;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

public class TimelineActivity extends AppCompatActivity {

    // Constants
    private final int NT_REQUEST_CODE = 20;

    private TwitterClient client;
    private TweetAdapter tweetAdapter;
    private ArrayList<Tweet> tweets;
    private RecyclerView rvTweets;
    private SwipeRefreshLayout swipeContainer;
    MenuItem miActionProgressItem;
    EditText message;
    AlertDialog composeAlertDialog;

    private boolean dialogOpen = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline);

        // Find the toolbar view inside the activity layout
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        // Sets the Toolbar to act as the ActionBar for this Activity window.
        // Make sure the toolbar exists in the activity and is not null
        setSupportActionBar(toolbar);

        client = TwitterApplication.getRestClient();

        // find the RecyclerView
        rvTweets = (RecyclerView) findViewById(R.id.rvTweet);

        // init the ArrayList (data source)
        tweets = new ArrayList<Tweet>();

        // construct the adapter from this data source
        tweetAdapter = new TweetAdapter(tweets);

        // RecyclerView setup (layout manager, use adapter)
        rvTweets.setLayoutManager(new LinearLayoutManager(this));
        rvTweets.setAdapter(tweetAdapter);

        populateTimeline();


        // Lookup the swipe container view
        swipeContainer = (SwipeRefreshLayout) findViewById(R.id.swipeContainer);

        // Setup refresh listener which triggers new data loading
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Your code to refresh the list here.
                // Make sure you call swipeContainer.setRefreshing(false)
                // once the network request has completed successfully.
                fetchTimelineAsync(0);
            }
        });

        // Configure the refreshing colors
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
    }

    public void fetchTimelineAsync(int page) {
        // Send the network request to fetch the updated data
        // `client` here is an instance of Android Async HTTP
        // getHomeTimeline is an example endpoint.

        client.getHomeTimeline(new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                // Remember to CLEAR OUT old items before appending in the new ones
                tweetAdapter.clear();
                // ...the data has come back, add new items to your adapter...
                // iterate through JSON array
                // for each entry, deserialize the JSON object
                for (int i = 0 ; i < response.length(); i++) {
                    try {
                        // convert each object to a tweet model
                        Tweet tweet = Tweet.fromJSON(response.getJSONObject(i));

                        // add that tweet model to our data source
                        tweets.add(tweet);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                tweetAdapter.addAll(tweets);

                // Now we call setRefreshing(false) to signal refresh has finished
                swipeContainer.setRefreshing(false);
            }

            public void onFailure(Throwable e) {
                Log.d("DEBUG", "Fetch timeline error: " + e.toString());
            }
        });
    }


    private void populateTimeline() {
        client.getHomeTimeline(new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.d("TwitterClient", response.toString());
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                // iterate through JSON array
                // for each entry, deserialize the JSON object
                for (int i = 0 ; i < response.length(); i++) {
                    try {
                        // convert each object to a tweet model
                        Tweet tweet = Tweet.fromJSON(response.getJSONObject(i));

                        // add that tweet model to our data source
                        tweets.add(tweet);

                        // notify the adapter that we've added an item
                        tweetAdapter.notifyItemInserted(tweets.size() - 1);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Log.d("TwitterClient", errorResponse.toString());
                throwable.printStackTrace();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                Log.d("TwitterClient", errorResponse.toString());
                throwable.printStackTrace();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Log.d("TwitterClient", responseString);
                throwable.printStackTrace();
            }
        });

    }

    // Menu icons are inflated just as they were with actionbar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // Store instance of the menu item containing progress
        miActionProgressItem = menu.findItem(R.id.miActionProgress);
        // Extract the action-view from the menu item
        ProgressBar v =  (ProgressBar) MenuItemCompat.getActionView(miActionProgressItem);
        // Return to finish
        return super.onPrepareOptionsMenu(menu);
    }

    public void showProgressBar() {
        // Show progress item
        miActionProgressItem.setVisible(true);
    }

    public void hideProgressBar() {
        // Hide progress item
        miActionProgressItem.setVisible(false);
    }

    // Establish event handler for compose icon
    public void onComposeAction (MenuItem mi) {

        // Show the progress bar until the modal loads
        showProgressBar();

        // Inflate the compose dialog
        View composeView = LayoutInflater.from(this).inflate(R.layout.activity_compose, null);
        // Create the Alert Dialog Builder
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        // Set the view that the alert dialog builder should create
        alertDialogBuilder.setView(composeView);
        composeAlertDialog = alertDialogBuilder.create();

        // Get EditText for tweet body and set listener
        message = (EditText) composeView.findViewById(R.id.etMessageBox);
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


        // Open the dialog and hide the progress bar
        composeAlertDialog.show();
        hideProgressBar();
    }

    public void onSubmit(View view) {
        final String tweetBody = message.getText().toString();
        showProgressBar();

        // Post a new tweet
        client.sendTweet(tweetBody, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {

                    Tweet posted = Tweet.fromJSON(response);

                    // Notify the adapter that a new tweet has been inserted and scroll to top
                    tweets.add(0, posted);
                    tweetAdapter.notifyItemInserted(0);
                    rvTweets.scrollToPosition(0);

                    hideProgressBar();
                    composeAlertDialog.cancel();


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


    // Handler for return of data
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // REQUEST_CODE is defined above
        if (resultCode == RESULT_OK && (requestCode == NT_REQUEST_CODE || requestCode == tweetAdapter.getREP_REQUEST_CODE())) {
            // Extract name value from result extras
            String tweetBody = data.getExtras().getString("tweetBody");

            // Add my latest tweet to my feed
            Tweet recent = (Tweet) Parcels.unwrap(data.getParcelableExtra("justTweeted"));

            // Notify the adapter that a new tweet has been inserted and scroll to top
            tweets.add(0, recent);
            tweetAdapter.notifyItemInserted(0);
            rvTweets.scrollToPosition(0);
        }
    }

    // Retweet from Timeline
    protected void onReTweet() {
        rvTweets.scrollToPosition(0);
    }

    void showTweetDialog() {
        FragmentManager fm = getSupportFragmentManager();
        TweetDialog tweetDialog = TweetDialog.newInstance("Some Title");
        tweetDialog.show(fm, "fragment_edit_name");
    }



}
