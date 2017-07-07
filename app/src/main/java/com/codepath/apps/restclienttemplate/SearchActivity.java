package com.codepath.apps.restclienttemplate;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.codepath.apps.restclienttemplate.fragments.SearchPagerAdapter;

/**
 * Created by veviego on 7/7/17.
 */

public class SearchActivity extends AppCompatActivity {
    // Setup variables needed for the page
    String query;
    TwitterClient client;
    MenuItem miActionProgressItem;
    ViewPager vpPager;
    SearchPagerAdapter spAdapter;




    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        query = (String) getIntent().getStringExtra("query");

        // Get the ViewPager
        vpPager = (ViewPager) findViewById(R.id.searchViewpager);
        // Set the Pager Adapter
        spAdapter = new SearchPagerAdapter(getSupportFragmentManager(), query, this);
        vpPager.setAdapter(spAdapter);
        // Set the TabLayout to use the ViewPager
        TabLayout tabLayout = (TabLayout) findViewById(R.id.searchSliding_tabs);
        tabLayout.setBackgroundColor(getResources().getColor(R.color.twitter_blue_30));
        tabLayout.setTabTextColors(getResources().getColor(R.color.white), getResources().getColor(R.color.white));
        tabLayout.setupWithViewPager(vpPager);

        // Find the toolbar view inside the activity layout
        final Toolbar toolbar = (Toolbar) findViewById(R.id.searchToolbar);
        // Sets the Toolbar to act as the ActionBar for this Activity window.
        // Make sure the toolbar exists in the activity and is not null
        setSupportActionBar(toolbar);

    }

    // Menu icons are inflated just as they were with actionbar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_search, menu);
        MenuItem sv = (MenuItem) menu.findItem(R.id.action_search);
        final SearchView mSearchView = (SearchView) MenuItemCompat.getActionView(sv);
        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Toast.makeText(SearchActivity.this, "hi", Toast.LENGTH_LONG);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                return false;
            }
        });

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

    public void onHome(MenuItem mi) {
        finish();
    }

}
