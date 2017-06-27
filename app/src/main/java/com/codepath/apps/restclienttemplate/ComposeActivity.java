package com.codepath.apps.restclienttemplate;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compose);

        client = TwitterApplication.getRestClient();

        // Add a listener to the pending tweet to get a character count
        message = (EditText) findViewById(R.id.etMessageBox);
        charCount = (TextView) findViewById(R.id.tvCharCount);
        message.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                charCount.setText((140 - count) + " / 140");
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    public void onSubmit(View view) {
        final String tweetBody = message.getText().toString();

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

    public void onCancel(View view) {
        finish();
    }
}
