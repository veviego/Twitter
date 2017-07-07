package com.codepath.apps.restclienttemplate.fragments;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.codepath.apps.restclienttemplate.SearchTweetsFragment;

/**
 * Created by veviego on 7/7/17.
 */

public class SearchPagerAdapter extends FragmentPagerAdapter {
    private int NUMBER_OF_FRAGMENTS = 2;
    private String[] tabTitle = new String[] {"Tweets", "Users"};
    Context context;
    public SearchTweetsFragment searchTweetsFragment;
    public MentionsTimelineFragment mentionsTimelineFragment;
    public String query;

    public SearchPagerAdapter(FragmentManager fm, String query, Context context) {
        super(fm);
        this.context = context;
        this.query = query;
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
                searchTweetsFragment = SearchTweetsFragment.newInstance(query);
                return searchTweetsFragment;

            case 1:
                mentionsTimelineFragment = new MentionsTimelineFragment();
                return mentionsTimelineFragment;

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
