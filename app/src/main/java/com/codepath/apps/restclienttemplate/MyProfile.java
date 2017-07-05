package com.codepath.apps.restclienttemplate;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.codepath.apps.restclienttemplate.fragments.UserTimelineFragment;
import com.codepath.apps.restclienttemplate.models.Profile;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;
import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

/**
 * Created by veviego on 7/3/17.
 */

public class MyProfile extends AppCompatActivity {
    // Constans and public variables
    TwitterClient client;
    ImageView ivProfileBanner;
    ImageView ivDetailProfileImage;
    TextView tvName;
    TextView tvTagline;
    TextView tvFollowers;
    TextView tvFollowing;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_profile);

        // Attach layout elements to variables
        ivProfileBanner = (ImageView) findViewById(R.id.ivProfileBanner);
        ivDetailProfileImage = (ImageView) findViewById(R.id.ivDetailProfileImage);
        tvName = (TextView) findViewById(R.id.tvName);
        tvTagline = (TextView) findViewById(R.id.tvTagline);
        tvFollowers = (TextView) findViewById(R.id.tvFollowers);
        tvFollowing = (TextView) findViewById(R.id.tvFollowing);


        // Set up an instance of the client
        client = TwitterApplication.getRestClient();

        String screenName = getIntent().getStringExtra("screen_name");

        // Create the user fragment
        UserTimelineFragment userTimelineFragment = UserTimelineFragment.newInstance(screenName);

        // Display the user timeline fragment inside the container (dynamically)
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();

        // Make changes
        ft.replace(R.id.flContainer, userTimelineFragment);

        // Commit
        ft.commit();

        // Find the toolbar view inside the activity layout
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        // Sets the Toolbar to act as the ActionBar for this Activity window.
        // Make sure the toolbar exists in the activity and is not null
        setSupportActionBar(toolbar);

        getSupportActionBar().setTitle(getIntent().getStringExtra("user_name"));

        client.getMyProfile(new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    Profile profile = Profile.fromJSON(response);

                    // Populate the user headline
                    populateUserHeadline(profile);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Log.e("MyProfile", errorResponse.toString());
            }
        });
    }

    public void populateUserHeadline(Profile profile) {
        tvName.setText(profile.name);
        tvTagline.setText(profile.tagline);
        tvFollowers.setText(String.valueOf(profile.followerCount) + " Followers");
        tvFollowing.setText(String.valueOf(profile.followingCount) + " Following");

        // Load profile and background images using glide
        Glide.with(this)
                .load(profile.backgroundUrl)
                .bitmapTransform(new RoundedCornersTransformation(this, 25, 0))
                .into(ivProfileBanner);

        Glide.with(this)
                .load(profile.profileImageUrl)
                .bitmapTransform(new RoundedCornersTransformation(this, 25, 0))
                .into(ivDetailProfileImage);

    }
}
