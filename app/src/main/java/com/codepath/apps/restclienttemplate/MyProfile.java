package com.codepath.apps.restclienttemplate;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
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
    // Constants and public variables
    TwitterClient client;
    ImageView ivProfileBanner;
    ImageView ivDetailProfileImage;
    TextView tvName;
    TextView tvTagline;
    TextView tvFollowers;
    TextView tvFollowing;
    String userName;
    String userID;
    RelativeLayout rlUserHeader;
    MenuItem miActionProgressItem;

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
        rlUserHeader = (RelativeLayout) findViewById(R.id.rlUserHeader);
        miActionProgressItem = (MenuItem) findViewById(R.id.miActionProgress);


        // Set up an instance of the client
        client = TwitterApplication.getRestClient();

        // Create the user fragment
        userName = getIntent().getStringExtra("userName");
        userID = getIntent().getStringExtra("userID");
        UserTimelineFragment userTimelineFragment = UserTimelineFragment.newInstance(userName);

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


        if (userName != null && userID != null) {
            client.getProfile(userName, userID, new JsonHttpResponseHandler() {
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
        } else {
            client.getMyProfile(new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    try {
                        Profile profile = Profile.fromJSON(response);

                        userName = profile.screenName;

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
    }

    public void populateUserHeadline(Profile profile) {
        tvName.setText(profile.name);
        tvTagline.setText(profile.tagline);
        tvFollowers.setText(String.valueOf(profile.followerCount) + " Followers");
        tvFollowing.setText(String.valueOf(profile.followingCount) + " Following");

        getSupportActionBar().setTitle("@" + userName);


        showProgressBar();

        // Load profile and background images using glide
        if (profile.backgroundUrl != null) {
            Glide.with(this)
                    .load(profile.backgroundUrl + "/600x200")
                    .listener(new RequestListener<String, GlideDrawable>() {
                        @Override
                        public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                            ivProfileBanner.setVisibility(View.GONE);
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                            return false;
                        }
                    })
                    .into(ivProfileBanner);
        } else {
            ivProfileBanner.setVisibility(View.GONE);
        }


        Glide.with(this)
                .load(profile.profileImageUrl)
                .bitmapTransform(new RoundedCornersTransformation(this, 25, 0))
                .into(ivDetailProfileImage);

        hideProgressBar();
    }

    public void onHome(MenuItem mi) {
        finish();
    }

    // Menu icons are inflated just as they were with actionbar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_profile, menu);
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
}
