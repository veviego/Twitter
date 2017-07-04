package com.codepath.apps.restclienttemplate.fragments;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

/**
 * Created by veviego on 7/3/17.
 */

public class TweetsPagerAdapter extends FragmentPagerAdapter {

    private int NUMBER_OF_FRAGMENTS = 2;
    private String[] tabTitle = new String[] {"Home", "Mentions"};
    private Context context;

    public TweetsPagerAdapter(FragmentManager fm, Context context) {
        super(fm);
        this.context = context;
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
                return new HomeTimelineFragment();

            case 1:
                return new MentionsTimelineFragment();

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
