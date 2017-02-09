package com.example.campusmap.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.example.campusmap.R;
import com.example.campusmap.form.InfoLocation;
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
    private SimpleCursorAdapter mAdapter;

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
//        searchView.setOnSuggestionListener(new SearchView.OnSuggestionListener() {
//            @Override
//            public boolean onSuggestionSelect(int position) {
//                return false;
//            }
//
//            @Override
//            public boolean onSuggestionClick(int position) {
//                Cursor cursor = (Cursor) mAdapter.getItem(position);
//                String select_query = cursor.getString(cursor.getColumnIndex("name"));
//                searchView.setQuery(select_query, true);
//                return true;
//            }
//        });
//        mAdapter = new SimpleCursorAdapter(
//                this,
//                android.R.layout.simple_list_item_1,
//                null,
//                new String[]{SQLiteHelperCampusInfo.BuildingEntry.COLUMN_NAME_NAME},
//                new int[]{android.R.id.text1},
//                CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER
//        );
//        searchView.setSuggestionsAdapter(mAdapter);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case  R.id.action_settings:
                startActivity(new Intent(this, SettingsActivity.class));
                return true;
//            case  R.id.action_only_test:
////                startActivity(new Intent(this, InfoUpdaterActivity.class));
//                SQLiteHelperObstacle helper = SQLiteHelperObstacle.getInstance(this);
//                helper.removeObstacle(helper.getWritableDatabase());
//                return true;
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
//        Log.i(TAG, "onQueryTextChange: new text : \"" + newText + "\"");
//        if (newText.isEmpty()) return false;
//        SQLiteHelperCampusInfo helper = SQLiteHelperCampusInfo.getInstance(this);
//        Cursor cursor = helper.searchBuildingInfoName(newText);
//        Log.i(TAG, "onQueryTextChange: cursor size : " + cursor.getColumnCount());
//        mAdapter.changeCursor(cursor);
        return true;
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
            InfoLocation infoLocation = (InfoLocation) extras.get(BuildingActivity.KEY_INFO_LOCATION);

            Log.d(TAG, "Serializable InfoLocation Data : " + infoLocation);
            if (infoLocation != null) {

                Intent intent = new Intent(this, BuildingActivity.class);
                intent.putExtra(BuildingActivity.KEY_INFO_LOCATION, infoLocation);
                startActivity(intent);
            }
        }
    }

    private class MainContentsPagerAdapter extends FragmentPagerAdapter {

        MainContentsPagerAdapter(FragmentManager fm) {
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
            return 2;
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
