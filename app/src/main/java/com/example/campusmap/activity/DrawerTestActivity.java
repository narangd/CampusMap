package com.example.campusmap.activity;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.Pair;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.campusmap.R;
import com.example.campusmap.adapter.MainRoomArrayAdapter;
import com.example.campusmap.database.SQLiteHelperCampusInfo;
import com.example.campusmap.fragment.pager.FloorPagerAdapter;

public class DrawerTestActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = "DrawerTestActivity";
    public static final String KEY_BUILDING = "building";

    private DrawerLayout mDrawer;
    private ArrayAdapter<Pair<String,Integer[]>> mAdapter;
    private ViewPager mFloorPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drawer_test);

        // ## Toolbar ##
        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

        // ## Drawer ##
        mDrawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, mDrawer, mToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
//        mDrawer.setDrawerListener(toggle);
        toggle.syncState();

        Intent intent = getIntent();
        int buildingID = intent.getIntExtra(BuildingActivity.KEY_BUILDING, -1);
        if (buildingID == -1) {
            finish();
        }

        // ## Get DataBase ##
        SQLiteHelperCampusInfo helper = SQLiteHelperCampusInfo.getInstance(this);
        SQLiteDatabase db = helper.getReadableDatabase();

        // ## Get Building Detail ##
        ContentValues buildingDetailValues = helper.getBuildingDetail(db, buildingID /* 100주년기념관 */);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(
                    buildingDetailValues.getAsString(SQLiteHelperCampusInfo.BuildingEntry.COLUMN_NAME_NAME)
            );
            Log.i(TAG, "onStart: ToolBar Title : " + getSupportActionBar().getTitle());
        }

        // ## Building Description ##
        TextView mDescTextView = (TextView) findViewById(R.id.description);
        if (mDescTextView != null) {
            mDescTextView.setText(
                    buildingDetailValues.getAsString(SQLiteHelperCampusInfo.BuildingEntry.COLUMN_NAME_DESCRIPTION)
            );
        }

        // ## Main Rooms ##
        mAdapter = new MainRoomArrayAdapter(
                this,
                android.R.layout.simple_list_item_1,
                helper.getMainRooms(db, buildingID /* 100주년기념관 */)
        );

        // ## Insert SubMenu Into NavigationView ##
        NavigationView mNavigationView = (NavigationView) findViewById(R.id.nav_view);
        if (mNavigationView != null) {
            mNavigationView.setNavigationItemSelectedListener(this);
            Menu menu = mNavigationView.getMenu();
            SubMenu subMenu = menu.addSubMenu(this.getResources().getString(R.string.sub_title_main_rooms));

            for (int i=0; i<mAdapter.getCount(); i++) {
                Pair<String, Integer[]> item = mAdapter.getItem(i);
                subMenu.add(Menu.FIRST, i, 0, item.first);
            }
        }

        // ## FloorPager ##
        mFloorPager = (ViewPager) findViewById(R.id.floor_pager);
        if (mFloorPager != null) {
            mFloorPager.setAdapter(new FloorPagerAdapter(getSupportFragmentManager(), this, buildingID));
        }
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        if (tabLayout != null) {
            tabLayout.setupWithViewPager(mFloorPager);
        }
    }

    @Override
    public void onBackPressed() {
        if (mDrawer != null && mDrawer.isDrawerOpen(GravityCompat.START)) {
            mDrawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem menuItem) {
        // Handle navigation view item clicks here.
        int id = menuItem.getItemId();
        int groupID = menuItem.getGroupId();
        Log.i(TAG, "onNavigationItemSelected: item id : " + id);
        Log.i(TAG, "onNavigationItemSelected: item group id : " + groupID);

        if (groupID == Menu.FIRST) {
            Log.i(TAG, "onNavigationItemSelected: index : " + id);

            Pair<String, Integer[]> item = mAdapter.getItem(id);
            Log.i(TAG, "onNavigationItemSelected: Building : " + item.first + ", [" + item.second[0] + "," + item.second[1] + "," + item.second[2] + "]");

//            mFloorPager.focusRoom(id, item.second[2]);
        }

        mDrawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
