package com.codepath.apps.restclienttemplate;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

public class ComposeActivity extends AppCompatActivity {

    TwitterClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compose);

        client = TwitterApplication.getRestClient();
    }

    public void onSubmit(View view) {
        EditText message = (EditText) findViewById(R.id.etMessageBox);
        final String tweetBody = message.getText().toString();

        client.sendTweet(tweetBody, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                // handle JSON Object Here
                // Use the Parecer thing to send it back to the other activity and assemble as a tweet
                Log.d("Compose Message", String.format("Tweet tweeted: %s", tweetBody));
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Log.d("Compose Message", errorResponse.toString());
            }
        });



        // Create a new intent to place the message data in
        Intent data = new Intent();

        // Pass back the relevant data
        data.putExtra("tweetBody", message.getText().toString());

        // Activity finished ok, return the data
        setResult(RESULT_OK, data); // set result code and bundle data for response
        finish(); // closes the activity, pass data to parent
    }
}