package com.example.campusmap.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.example.campusmap.R;
import com.example.campusmap.database.SearchResultItem;
import com.example.campusmap.fragment.CampusMapFragment;
import com.example.campusmap.fragment.MenuPlannerFragment;
import com.example.campusmap.fragment.PathFindingFragment;

public class MainActivity extends AppCompatActivity
        implements SearchView.OnQueryTextListener, MenuItemCompat.OnActionExpandListener {
    private static final String TAG = "MainActivity";
//    private static final boolean
    public static final int SEARCH_RESULT_ACTIVITY_REQUEST_CODE = 1;

    private SearchView searchView;
    private MenuItem searchItem;

    private String previousQuery = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        // ## Layout ##
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // ## Toolbar ##
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Set up the ViewPager with the sections adapter.
        ViewPager mViewPager = (ViewPager) findViewById(R.id.container);
        if (mViewPager != null) {
            MainContentsPagerAdapter mainContentsPagerAdapter = new MainContentsPagerAdapter(getSupportFragmentManager());
            mViewPager.setAdapter(mainContentsPagerAdapter);
        }

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        if (tabLayout != null) {
            tabLayout.setupWithViewPager(mViewPager);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        searchItem = menu.findItem(R.id.action_search);

        MenuItemCompat.setOnActionExpandListener(searchItem, this);

        searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setOnQueryTextListener(this);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case  R.id.action_settings:
                startActivity(new Intent(this, SettingsActivity.class));
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        Intent intent = new Intent(MainActivity.this, SearchResultActivity.class);
        intent.putExtra("query", query);
        startActivityForResult(intent, SEARCH_RESULT_ACTIVITY_REQUEST_CODE);

        searchItem.collapseActionView();
        searchView.clearFocus();
        return true;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        return false;
    }

    @Override
    public boolean onMenuItemActionExpand(MenuItem item) {

        searchView.setQuery(previousQuery, false);
        Log.i(TAG, "onMenuItemActionExpand: item:" + item);
        return true;
    }

    @Override
    public boolean onMenuItemActionCollapse(MenuItem item) {
        previousQuery = searchView.getQuery().toString();
        Log.i(TAG, "onMenuItemActionCollapse: item:" + item);
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SEARCH_RESULT_ACTIVITY_REQUEST_CODE && resultCode == SearchResultActivity.RESULT_OK) {
            Bundle extras = data.getExtras();
            SearchResultItem searchResultItem = (SearchResultItem) extras.get("SearchResultItem");

            Log.d(TAG, "Serializable SearchResultItem Data : " + searchResultItem);
            if (searchResultItem != null) {

                Intent intent = new Intent(this, DrawerTestActivity.class);
                intent.putExtra(DrawerTestActivity.KEY_SEACH_ITEM, searchResultItem);
                startActivity(intent);
            }
        }
    }

    private class MainContentsPagerAdapter extends FragmentPagerAdapter {

        public MainContentsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case CampusMapFragment.TAP_INDEX: return CampusMapFragment.newInstance();
                case MenuPlannerFragment.TAP_INDEX: return MenuPlannerFragment.newInstance();
                case PathFindingFragment.TAP_INDEX: return PathFindingFragment.newInstance();
            }
            return Fragment.instantiate(getApplicationContext(),"out of index:" + position);
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case CampusMapFragment.TAP_INDEX: return "캠퍼스 맵";
                case MenuPlannerFragment.TAP_INDEX: return "이번주 식단표"; //"건물정보";
                case PathFindingFragment.TAP_INDEX: return "길찾기";
            }
            return null;
        }
    }
}
