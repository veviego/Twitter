package com.codepath.apps.restclienttemplate;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
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

import com.codepath.apps.restclienttemplate.fragments.HomeTimelineFragment;
import com.codepath.apps.restclienttemplate.fragments.TweetsPagerAdapter;
import com.codepath.apps.restclienttemplate.models.Tweet;
import com.codepath.apps.restclienttemplate.models.User;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;

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
    HomeTimelineFragment homeTimelineFragment;
    ViewPager vpPager;
    TweetsPagerAdapter tpAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline);

        client = TwitterApplication.getRestClient();

        // Get the ViewPager
        vpPager = (ViewPager) findViewById(R.id.viewpager);
        // Set the Pager Adapter
        tpAdapter = new TweetsPagerAdapter(getSupportFragmentManager(), this);
        vpPager.setAdapter(tpAdapter);
        // Set the TabLayout to use the ViewPager
        TabLayout tabLayout = (TabLayout) findViewById(R.id.sliding_tabs);
        tabLayout.setBackgroundColor(getResources().getColor(R.color.twitter_blue_30));
        tabLayout.setTabTextColors(getResources().getColor(R.color.white), getResources().getColor(R.color.white));
        tabLayout.setupWithViewPager(vpPager);


        // Find the toolbar view inside the activity layout
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        // Sets the Toolbar to act as the ActionBar for this Activity window.
        // Make sure the toolbar exists in the activity and is not null
        setSupportActionBar(toolbar);
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

    // idea, make onsubmit just call a helper which would then do fragment.tweets.add ...


    public void onSubmit(View v) {
        final String tweetBody = message.getText().toString();
        showProgressBar();

        Fragment page = getSupportFragmentManager().findFragmentByTag("android:switcher:" + R.id.viewpager + ":" + vpPager.getCurrentItem());

        if (tweetBody.length() > 0) {
            // Post a new tweet
            client.sendTweet(tweetBody, new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    try {

                        Tweet posted = Tweet.fromJSON(response);
                        posted.entity.setMedia_url(null);

                        // Notify the adapter that a new tweet has been inserted and scroll to top
                        tpAdapter.homeTimelineFragment.addTweet(posted);

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
        } else {
            hideProgressBar();
        }
    }

    public void onViewProfile(MenuItem mi) {
        final Intent i = new Intent(this, MyProfile.class);

        client.getMyProfile(new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                // Deserialize the user object
                try {
                    User user = User.fromJSON(response);
                    i.putExtra("user_name", "@" + user.screenName);
                    startActivity(i);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                startActivity(i);

            }
        });
    }

    public void onCancel(View view) {
        composeAlertDialog.cancel();
    }
}
