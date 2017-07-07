package com.codepath.apps.restclienttemplate.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.codepath.apps.restclienttemplate.PeopleAdapter;
import com.codepath.apps.restclienttemplate.R;
import com.codepath.apps.restclienttemplate.TwitterApplication;
import com.codepath.apps.restclienttemplate.TwitterClient;
import com.codepath.apps.restclienttemplate.models.User;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

/**
 * Created by veviego on 7/5/17.
 */

public class SearchUsersFragment extends Fragment {

    private TwitterClient client;
    public ArrayList<User> users;
    public RecyclerView rvUsers;
    public PeopleAdapter peopleAdapter;
    public SwipeRefreshLayout swipeContainer;
    String query;
    String userID;

    public static SearchUsersFragment newInstance(String query) {
        SearchUsersFragment searchUsersFragment = new SearchUsersFragment();
        Bundle args = new Bundle();
        args.putString("query", query);
        searchUsersFragment.setArguments(args);
        return searchUsersFragment;
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout
        View v = inflater.inflate(R.layout.fragment_people_list, container, false);

        client = TwitterApplication.getRestClient();

        // find the RecyclerView
        rvUsers = (RecyclerView) v.findViewById(R.id.rvPeople);
        rvUsers.addItemDecoration(new com.codepath.apps.restclienttemplate.DividerItemDecoration(getContext()));


        // init the ArrayList (data source)
        users = new ArrayList<User>();

        // construct the adapter from this data source
        peopleAdapter = new PeopleAdapter(users);

        // RecyclerView setup (layout manager, use adapter)
        rvUsers.setLayoutManager(new LinearLayoutManager(getContext()));
        rvUsers.setAdapter(peopleAdapter);

        query = getArguments().getString("query");


        // Lookup the swipe container view
        swipeContainer = (SwipeRefreshLayout) v.findViewById(R.id.swipeContainer);

        // Setup refresh listener which triggers new data loading
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Your code to refresh the list here.
                // Make sure you call swipeContainer.setRefreshing(false)
                // once the network request has completed successfully.
                fetchFollowingAsync(0);
            }
        });

        // Configure the refreshing colors
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        populateFollowing();


        return v;
    }


    public void fetchFollowingAsync(int page) {
        // Send the network request to fetch the updated data
        // `client` here is an instance of Android Async HTTP
        // getHomeTimeline is an example endpoint.

        client.searchUsers(query, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                // Remember to CLEAR OUT old items before appending in the new ones
                peopleAdapter.clear();
                // ...the data has come back, add new items to your adapter...
                // iterate through JSON array
                // for each entry, deserialize the JSON object

                for (int i = 0 ; i < response.length(); i++) {
                    try {
                        // convert each object to a tweet model
                        User user = User.fromJSON(response.getJSONObject(i));

                        // add that tweet model to our data source
                        users.add(user);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                peopleAdapter.addAll(users);

                // Now we call setRefreshing(false) to signal refresh has finished
                swipeContainer.setRefreshing(false);
            }

            public void onFailure(Throwable e) {
                Log.d("DEBUG", "Fetch timeline error: " + e.toString());
            }
        });
    }


    private void populateFollowing() {
        client.searchUsers(query, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                // iterate through JSON array
                // for each entry, deserialize the JSON object

                for (int i = 0 ; i < response.length(); i++) {
                    try {
                        // convert each object to a tweet model
                        User user = User.fromJSON(response.getJSONObject(i));

                        // add that tweet model to our data source
                        users.add(user);

                        // notify the adapter that we've added an item
                        peopleAdapter.notifyItemInserted(users.size() - 1);
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
}
