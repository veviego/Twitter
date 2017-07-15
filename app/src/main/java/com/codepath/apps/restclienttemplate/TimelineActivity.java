package com.codepath.apps.restclienttemplate;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.codepath.apps.restclienttemplate.fragments.TweetsPagerAdapter;
import com.codepath.apps.restclienttemplate.models.Tweet;
import com.codepath.apps.restclienttemplate.models.User;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

public class TimelineActivity extends AppCompatActivity {

    // Constants
    private final int NT_REQUEST_CODE = 20;

    private TwitterClient client;
    MenuItem miActionProgressItem;
    EditText message;
    AlertDialog composeAlertDialog;
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
        final MenuItem sv = (MenuItem) menu.findItem(R.id.action_search);
        final SearchView mSearchView = (SearchView) MenuItemCompat.getActionView(sv);
        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Intent i = new Intent(TimelineActivity.this, SearchActivity.class);
                i.putExtra("query", mSearchView.getQuery().toString());
                startActivity(i);

                // Close searchbar
                mSearchView.setIconified(true);
                MenuItemCompat.collapseActionView(sv);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        EditText searchEditText = (EditText) mSearchView.findViewById(android.support.v7.appcompat.R.id.search_src_text);
        searchEditText.setTextColor(getResources().getColor(R.color.white));
        searchEditText.setHintTextColor(getResources().getColor(R.color.white));

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

        // Connect to my server
        client.connectToServer(new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Toast.makeText(TimelineActivity.this, "Success", Toast.LENGTH_LONG).show();
                Log.e("help1", response.toString());
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Toast.makeText(TimelineActivity.this, "Failure", Toast.LENGTH_LONG).show();
                Log.e("help1", errorResponse.toString());

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                Toast.makeText(TimelineActivity.this, "Failure", Toast.LENGTH_LONG).show();
                Log.e("help2", errorResponse.toString());

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Toast.makeText(TimelineActivity.this, "Failure", Toast.LENGTH_LONG).show();
                Log.e("help3", throwable.toString());
            }
        });
    }


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
