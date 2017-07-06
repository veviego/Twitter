package com.codepath.apps.restclienttemplate.fragments;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

/**
 * Created by veviego on 7/3/17.
 */

public class PeoplePagerAdapter extends FragmentPagerAdapter {

    // TODO create fragments and add them in

    private int NUMBER_OF_FRAGMENTS = 2; // change to 3
    private String[] tabTitle = new String[] {"Tweets", "Followers", "Following"};
    private Context context;
    public String userName;
    public UserTimelineFragment userTimelineFragment;
    public FollowersFragment followersFragment;

    public PeoplePagerAdapter(FragmentManager fm, Context context, String userName) {
        super(fm);
        this.context = context;
        this.userName = userName;
    }

    // Return the total number of fragments
    @Override
    public int getCount() {
        return NUMBER_OF_FRAGMENTS;
    }

    // Return the fragment to use based on position
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                userTimelineFragment = UserTimelineFragment.newInstance(userName);
                return userTimelineFragment;

            case 1:
                followersFragment = FollowersFragment.newInstance(userName);
                return followersFragment;

            default:
                return null;
        }
    }

    // Return the fragment title for each tab
    @Override
    public CharSequence getPageTitle(int position) {
        // Generate title based on position
        return tabTitle[position];
    }
}
