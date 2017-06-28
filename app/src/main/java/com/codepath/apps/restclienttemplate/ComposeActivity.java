package com.codepath.apps.restclienttemplate;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.codepath.apps.restclienttemplate.models.Tweet;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcels;

import cz.msebera.android.httpclient.Header;

public class ComposeActivity extends AppCompatActivity {

    TwitterClient client;
    EditText message;
    TextView charCount;
    MenuItem miActionProgressItem;

    int request_code;
    Tweet replyTo;
    long statusID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compose);

        request_code = (int) getIntent().getIntExtra("request_code", 0);

        client = TwitterApplication.getRestClient();

        // Find the toolbar view inside the activity layout
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        // Sets the Toolbar to act as the ActionBar for this Activity window.
        // Make sure the toolbar exists in the activity and is not null
        setSupportActionBar(toolbar);

        miActionProgressItem = (MenuItem) findViewById(R.id.miActionProgress);

        // Add a listener to the pending tweet to get a character count
        message = (EditText) findViewById(R.id.etMessageBox);
        if (Integer.toString(request_code).equals(getResources().getString(R.string.reply_request))) {
            replyTo = (Tweet) Parcels.unwrap(getIntent().getParcelableExtra("currentTweet"));
            String userName = "@" + replyTo.user.screenName;
            message.setText(userName);

            // Set userID for later
            statusID = replyTo.uid;
        }

        charCount = (TextView) findViewById(R.id.tvCharCount);
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
    }



    public void onSubmit(View view) {
        final String tweetBody = message.getText().toString();
        showProgressBar();

        // Reply to a tweet
        if (Integer.toString(request_code).equals(getResources().getString(R.string.reply_request))) {
            client.sendTweet(tweetBody, getResources().getString(R.string.reply_param_key), statusID, new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    try {

                        Tweet posted = Tweet.fromJSON(response);

                        // Create a new intent to place the message data in
                        Intent data = new Intent();

                        // Pass back the relevant data
                        data.putExtra("justTweeted", Parcels.wrap(posted));

                        // Activity finished ok, return the data
                        setResult(RESULT_OK, data); // set result code and bundle data for response
                        hideProgressBar();
                        finish(); // closes the activity, pass data to parent

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                    Log.d("Compose Message", errorResponse.toString());
                }
            });
        } else
        {
            // Post a new tweet
            client.sendTweet(tweetBody, new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    try {

                        Tweet posted = Tweet.fromJSON(response);

                        // Create a new intent to place the message data in
                        Intent data = new Intent();

                        // Pass back the relevant data
                        data.putExtra("justTweeted", Parcels.wrap(posted));

                        // Activity finished ok, return the data
                        setResult(RESULT_OK, data); // set result code and bundle data for response
                        hideProgressBar();
                        finish(); // closes the activity, pass data to parent

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
    }

    // Menu icons are inflated just as they were with actionbar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_compose, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu)
    {
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


    public void onCancel(View view) {
        finish();
    }
}
