package com.example.campusmap.activity;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
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
import android.widget.Toast;

import com.example.campusmap.R;
import com.example.campusmap.database.SQLiteHelperCampusInfo;
import com.example.campusmap.database.SearchResultItem;
import com.example.campusmap.fragment.BuildingInfoFragment;
import com.example.campusmap.fragment.CampusMapFragment;
import com.example.campusmap.fragment.PathFindingFragment;

public class MainActivity extends AppCompatActivity
        implements SearchView.OnQueryTextListener, MenuItemCompat.OnActionExpandListener {
    private static final String TAG = "MainActivity";
//    private static final boolean
    public static final int SEARCH_RESULT_ACTIVITY_REQUEST_CODE = 1;
    private boolean isStart = false;

    private SectionsPagerAdapter mSectionsPagerAdapter;

    private ViewPager mViewPager;

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

        // ## PagerAdapter... ##
        if (mSectionsPagerAdapter == null) {
            mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        }

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        if (mViewPager != null) {
            mViewPager.setAdapter(mSectionsPagerAdapter);
        }
//        mViewPager.setOffscreenPageLimit(0);

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

        // 밑을 하면 검색뷰가 안열림.
        //MenuItemCompat.setOnActionExpandListener(searchItem, this);

        searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setOnQueryTextListener(this);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        switch (id) {
            case  R.id.action_settings:
//                startActivity(new Intent(this, ParallaxActiviry.class));
                return true;
            case R.id.action_reset_db:
                SQLiteHelperCampusInfo sqLiteHelperCampusInfo = SQLiteHelperCampusInfo.getInstance(this);
                SQLiteDatabase db = sqLiteHelperCampusInfo.getWritableDatabase();

                sqLiteHelperCampusInfo.deleteTable(db, SQLiteHelperCampusInfo.BuildingEntry.TABLE_NAME);
                sqLiteHelperCampusInfo.deleteTable(db, SQLiteHelperCampusInfo.FloorEntry.TABLE_NAME);
                sqLiteHelperCampusInfo.deleteTable(db, SQLiteHelperCampusInfo.RoomEntry.TABLE_NAME);

                Toast.makeText(getApplicationContext(), "데이터를 모두 삭제하였습니다.", Toast.LENGTH_SHORT).
                        show();

                Log.i("MainActivity", "onOptionsItemSelected: Delete Data (building, floor, room");
                return true;
            case R.id.action_only_test:
                Intent intent = new Intent(this, DrawerTestActivity.class);
                intent.putExtra(DrawerTestActivity.KEY_BUILDING_ID, 1);
                startActivity(intent);
                return true;
            case R.id.action_parallax:
                startActivity(new Intent(this, ScrollingActivity.class));
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (mViewPager.getCurrentItem() == 1 && BuildingInfoFragment.getInstance().getIndex() > 0)
            BuildingInfoFragment.getInstance().onBackPressed();
        else
            super.onBackPressed();
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
//        final String[] from = new String[] {"str"};
//        final int[] to = new int[] {android.R.id.text1};
//        CursorAdapter cursorAdapter = new SimpleCursorAdapter(
//                this,
//                android.R.layout.simple_list_item_1,
//                BuildingInfoParser.searchSuggestions(this, newText),
//                from,
//                to,
//                CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER
//        );
//        searchView.setSuggestionsAdapter(cursorAdapter);
        // 속도가 느려서 사용하지 않음... 나중에 db로 대체..
        //System.out.println(newText);
        return false;
    }

    @Override
    public boolean onMenuItemActionExpand(MenuItem item) {
        searchView.setQuery(previousQuery, false);
        return false;
    }

    @Override
    public boolean onMenuItemActionCollapse(MenuItem item) {
        previousQuery = searchView.getQuery().toString();
        return false;
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

    // ------------------------------- SectionsPagerAdapter -------------------------------------//

    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case CampusMapFragment.TAP_INDEX: return CampusMapFragment.newInstance();
                case BuildingInfoFragment.TAP_INDEX: return BuildingInfoFragment.getInstance();
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
                case CampusMapFragment.TAP_INDEX:
                    return "캠퍼스 맵";
                case BuildingInfoFragment.TAP_INDEX:
                    return "건물정보";
                case PathFindingFragment.TAP_INDEX:
                    return "길찾기";
            }
            return null;
        }
    }
}
