package com.example.campusmap.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
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
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.campusmap.R;
import com.example.campusmap.adapter.MainRoomArrayAdapter;
import com.example.campusmap.data.branch.Building;
import com.example.campusmap.data.branch.Floor;
import com.example.campusmap.database.InfoLocation;
import com.example.campusmap.database.SQLiteHelperCampusInfo;
import com.example.campusmap.fragment.RoomListFragment;
import com.example.campusmap.fragment.pager.FloorPagerAdapter;

import nl.codesoup.cubicbezier.CubicBezierInterpolator;

public class DrawerTestActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener, DrawerLayout.DrawerListener {

    private static final String TAG = "DrawerTestActivity";
    public static final String KEY_BUILDING_ID = "building_id";
    public static final String KEY_INFO_LOCATION = "InfoLocation";
    private static final boolean DEBUG = false;

    private DrawerLayout mDrawer;
    private ArrayAdapter<Pair<String,Integer[]>> mMainRoomAdapter;
    private ViewPager mFloorPager;
    private FloorPagerAdapter mFloorAdapter;
    private boolean isTried = false;
    private InfoLocation mInfoLocation;
    private TextView mDescTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drawer_test);

        // ## Toolbar ##
        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

        // ## Drawer ##
        mDrawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (mDrawer != null) {
            mDrawer.addDrawerListener(this);
        }
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, mDrawer, mToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
//        mDrawer.setDrawerListener(toggle);
        toggle.syncState();

        Intent intent = getIntent();
        int buildingID = intent.getIntExtra(KEY_BUILDING_ID, -1);
        mInfoLocation = (InfoLocation)intent.getSerializableExtra(KEY_INFO_LOCATION);

        // Check Intent
        if (buildingID == -1 && mInfoLocation == null) {
            finish();
            Log.e(TAG, "onCreate: 입력한 값이 없습니다. (ex Building ID or InfoLocation");
            return;
        }
        if (buildingID == -1) {
            Log.i(TAG, "onCreate: getResultItem" + mInfoLocation.getBuildingID() + "," + mInfoLocation.getFloorID() + "," + mInfoLocation.getRoomID());
            buildingID = mInfoLocation.getBuildingID();
        }

        // ## Get DataBase ##
        SQLiteHelperCampusInfo helper = SQLiteHelperCampusInfo.getInstance(this);

        // ## Get Building Detail ##
        Building building = helper.getBuildingDetail(buildingID);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(building.getName());

            if (DEBUG) Log.i(TAG, "onCreate: ToolBar Title : " + getSupportActionBar().getTitle());
        }

        // ## Main Rooms ##
        mMainRoomAdapter = new MainRoomArrayAdapter(
                this,
                android.R.layout.simple_list_item_1,
                helper.getMainRooms(buildingID)
        );

        // ## Insert SubMenu Into NavigationView ##
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        if (navigationView != null) {
            navigationView.setNavigationItemSelectedListener(this);
            View headerLayout = navigationView.getHeaderView(0);

            // ## Building Title ##
            TextView buildingTitle = (TextView) headerLayout.findViewById(R.id.building_title);
            if (buildingTitle != null) {
                String title = building.getName();
                int number = building.getNumber();
                title += " - " + number + "번건물";
                buildingTitle.setText(title);
            }

            // ## Building Description ##
            mDescTextView = (TextView) headerLayout.findViewById(R.id.description);
            if (mDescTextView != null) {
                String desc = building.getDescription();
                if (desc == null || desc.length() <= 0) {
                    desc = "여분으로 보여질 텍스트1";
                    desc += "\n여분으로 보여질 텍스트2";
                    desc += "\n여분으로 보여질 텍스트3";
                    desc += "\n여분으로 보여질 텍스트4";
                }
                mDescTextView.setText(desc);
            }

            // ## Set Building Image ##
            ImageView imageView = (ImageView) headerLayout.findViewById(R.id.imageView);
            if (imageView != null) {
                String name = "building_" + buildingID;
                int resourceID = getResources().getIdentifier(name, "drawable", getPackageName());
                if (resourceID != 0) {
                    imageView.setImageResource(resourceID);
                } else {
                    imageView.setImageResource(R.drawable.image_not_found);
                }
            }

            // ## Description Toogle Button ##
            ImageView toggleButton = (ImageView) headerLayout.findViewById(R.id.toggle_description);
            if (toggleButton !=null){
                toggleButton.setOnClickListener(this);
            }

            // ## Create Menu ##
            Menu menu = navigationView.getMenu();
            SubMenu subMenu = menu.addSubMenu(getResources().getString(R.string.sub_title_main_rooms));
            subMenu.setGroupCheckable(0, true, true);

            for (int i = 0; i< mMainRoomAdapter.getCount(); i++) {
                Pair<String, Integer[]> item = mMainRoomAdapter.getItem(i);
                subMenu.add(R.id.nav_group, i, 0, item.first)
                        .setIcon(R.drawable.side_nav_bar_list_item);
            }
        }

        // ## FloorPager ##
        mFloorPager = (ViewPager) findViewById(R.id.floor_pager);
        if (mFloorPager != null) {
            mFloorAdapter = new FloorPagerAdapter(getSupportFragmentManager(), this, buildingID);
            mFloorPager.setAdapter(mFloorAdapter);
            mFloorPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                }
                @Override
                public void onPageSelected(int position) {
                    // TODO: 2016-07-28 층이 선택되면 -> 이 층에 포함된 층들이 MainRoom의 모든 Menu가 선택되게 한다.
                }
                @Override
                public void onPageScrollStateChanged(int state) {

                }
            });
        }
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        if (tabLayout != null) {
            tabLayout.setupWithViewPager(mFloorPager);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (DEBUG) Log.i(TAG, "onStart: called!!");

        if (mInfoLocation != null && !isTried) {
            // ## 검색한 결과를 통해 들어오는 경우 ##
            if (DEBUG) Log.i(TAG, "onStart: focus! : " + mInfoLocation.getFloorID() + ", " + mInfoLocation.getRoomID());
            focusRoom(mInfoLocation.getFloorID(), mInfoLocation.getRoomID());
            isTried = true;
        } else {
            // ## 정상적인 경로로 들어온 경우 ##

            // ## 지상을 먼저 선택되게 한다 ##
            for (int i = 0; i < mFloorAdapter.getCount(); i++) {
                Floor floor = mFloorAdapter.getFloor(i);
                if (floor.getFloor() > 0) {
                    mFloorPager.setCurrentItem(i, true);
                    break;
                }
            }
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

    MenuItem mPrevMenuItem;
    @Override
    public boolean onNavigationItemSelected(MenuItem menuItem) {
        // Handle navigation view item clicks here.
        int id = menuItem.getItemId();
        int groupID = menuItem.getGroupId();

        if (mPrevMenuItem != null) {
            mPrevMenuItem.setChecked(false);
        }
        menuItem.setChecked(true);
        mPrevMenuItem = menuItem;

        if (groupID == R.id.nav_group) {
            Pair<String, Integer[]> item = mMainRoomAdapter.getItem(id);
            if (DEBUG) Log.i(TAG, "onNavigationItemSelected: Building : " + item.first + ", [" + item.second[0] + "," + item.second[1] + "," + item.second[2] + "]");

            int floorID = item.second[1];
            int roomID = item.second[2];

            focusRoom(floorID, roomID);
        }

        mDrawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void focusRoom(final int floorID, final int roomID) {
        if (DEBUG) Log.i(TAG, "focusRoom: called");

        int floorIndex = -1;

        for (int i=0; i<mFloorAdapter.getCount(); i++) {
            Floor floor = mFloorAdapter.getFloor(i);
            if (floorID == floor.getID()) {
                if (DEBUG) Log.i(TAG, "focusRoom: floor index : " + i);
                floorIndex = i;
                break;
            }
        }

        if (floorIndex >= 0) {
            mFloorPager.setCurrentItem(floorIndex);

            final int findFloorIndex = floorIndex;
            mFloorAdapter.setOnFragmentListener(new FloorPagerAdapter.OnFragmentListener() {
                boolean mTryOnce = false;

                @Override
                public void OnFragmentInstantiate(Fragment fragment, int position) {
                    if (!mTryOnce && position == findFloorIndex && fragment instanceof RoomListFragment) {
                        mTryOnce = true;
                        RoomListFragment roomListFragment = (RoomListFragment) fragment;
                        roomListFragment.reserveRoom(roomID);

                    }
                }
                @Override
                public void OnFragmentDestroy(int position) {}
            });
        }
    }

    // ## Description Text ToggleButton ##
    @Override
    public void onClick(View v) {
        if (mDescTextView != null) {
            if (mDescTextView.getVisibility() == View.VISIBLE) {
                collapse(mDescTextView);
            } else {
                mDescTextView.setVisibility(View.VISIBLE);
                expand(mDescTextView);
            }
        }
    }

    public void expand(final View v) {
        v.measure(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        final int targetHeight = v.getMeasuredHeight();

        // Older versions of android (pre API 21) cancel animations for views with a height of 0.
        v.getLayoutParams().height = 1;
        v.setVisibility(View.VISIBLE);
        Animation a = new Animation()
        {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                v.getLayoutParams().height = interpolatedTime == 1
                        ? ViewGroup.LayoutParams.WRAP_CONTENT
                        : (int)(targetHeight * interpolatedTime);
                v.requestLayout();
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };

        // 1dp/ms
        a.setDuration((int)(targetHeight / v.getContext().getResources().getDisplayMetrics().density));
        a.setInterpolator(new CubicBezierInterpolator(0.4, 0, 0.2, 1));
        v.startAnimation(a);
    }

    public void collapse(final View v) {
        final int initialHeight = v.getMeasuredHeight();

        Animation a = new Animation()
        {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                if(interpolatedTime == 1){
                    v.setVisibility(View.GONE);
                }else{
                    v.getLayoutParams().height = initialHeight - (int)(initialHeight * interpolatedTime);
                    v.requestLayout();
                }
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };

        // 1dp/ms
        a.setDuration((int)(initialHeight / v.getContext().getResources().getDisplayMetrics().density));
        a.setInterpolator(new CubicBezierInterpolator(0.4, 0, 0.2, 1));
        v.startAnimation(a);
    }

    //  ## For Drawer (( DrawerLayout.DrawerListener )) ##
    @Override
    public void onDrawerOpened(View drawerView) {

    }
    @Override
    public void onDrawerClosed(View drawerView) {
        mDescTextView.setVisibility(View.GONE);
    }
    @Override
    public void onDrawerStateChanged(int newState) {

    }
    @Override
    public void onDrawerSlide(View drawerView, float slideOffset) {

    }
}
