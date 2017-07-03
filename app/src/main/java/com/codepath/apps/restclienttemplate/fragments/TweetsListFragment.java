package com.codepath.apps.restclienttemplate.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.codepath.apps.restclienttemplate.DividerItemDecoration;
import com.codepath.apps.restclienttemplate.R;
import com.codepath.apps.restclienttemplate.TweetAdapter;
import com.codepath.apps.restclienttemplate.TwitterApplication;
import com.codepath.apps.restclienttemplate.TwitterClient;
import com.codepath.apps.restclienttemplate.models.Tweet;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

/**
 * Created by veviego on 7/3/17.
 */

public class TweetsListFragment extends Fragment {

    private TwitterClient client;
    private TweetAdapter tweetAdapter;
    private ArrayList<Tweet> tweets;
    private RecyclerView rvTweets;
    public SwipeRefreshLayout swipeContainer;
    public View v;

    // Inflation happens in onCreateView
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout
        View v = inflater.inflate(R.layout.fragments_tweet_list, container, false);

        client = TwitterApplication.getRestClient();

        // find the RecyclerView
        rvTweets = (RecyclerView) v.findViewById(R.id.rvTweet);
        rvTweets.addItemDecoration(new DividerItemDecoration(getContext()));


        // init the ArrayList (data source)
        tweets = new ArrayList<Tweet>();

        // construct the adapter from this data source
        tweetAdapter = new TweetAdapter(tweets);

        // RecyclerView setup (layout manager, use adapter)
        rvTweets.setLayoutManager(new LinearLayoutManager(getContext()));
        rvTweets.setAdapter(tweetAdapter);


        return v;
    }

    public void refreshTimeline(JSONArray response) {
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

    public void addItems(JSONArray response) {
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



}
