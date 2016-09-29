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
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.campusmap.R;
import com.example.campusmap.data.branch.Building;
import com.example.campusmap.data.branch.Floor;
import com.example.campusmap.database.SQLiteHelperCampusInfo;
import com.example.campusmap.form.InfoLocation;
import com.example.campusmap.fragment.RoomListFragment;
import com.example.campusmap.fragment.pager.FloorPagerAdapter;

import java.util.ArrayList;

import nl.codesoup.cubicbezier.CubicBezierInterpolator;

public class DrawerTestActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, DrawerLayout.DrawerListener {

    private static final String TAG = "DrawerTestActivity";
    public static final String KEY_INFO_LOCATION = "InfoLocation";
    private static final boolean DEBUG = false;

    private ArrayList<InfoLocation> mMainRoomList;
    private InfoLocation mInfoLocation;
    private Building mBuilding;
    private MenuItem mPrevMenuItem;

    private DrawerLayout mDrawer;
    private ViewPager mFloorPager;
    private FloorPagerAdapter mFloorAdapter;
    private TextView mBuildingTitle;
    private TextView mBuildingDescription;
    private ImageView mHeaderImageView;
    private ImageView mToggleButton;
    private TabLayout mTabLayout;
    private NavigationView mNavigationView;
    private Toolbar mToolbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drawer_test);

        Intent intent = getIntent();
        mInfoLocation = (InfoLocation) intent.getSerializableExtra(KEY_INFO_LOCATION);
        if (mInfoLocation == null) {
            finish(); // Activity Finish
            Log.e(TAG, "onCreate: 입력한 값이 없습니다. (InfoLocation");
            return; // Method Return
        }

        // ## Get DataBase ##
        SQLiteHelperCampusInfo helper = SQLiteHelperCampusInfo.getInstance(this);

        mBuilding = helper.getBuildingDetail(mInfoLocation.getBuildingID());
        mMainRoomList = helper.getMainRooms(mInfoLocation.getBuildingID());

        mDrawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mFloorPager = (ViewPager) findViewById(R.id.floor_pager);
        mTabLayout = (TabLayout) findViewById(R.id.tabs);
        mNavigationView = (NavigationView) findViewById(R.id.nav_view);

        // ## NavigationView ##
        assert mNavigationView != null;
        mNavigationView.setNavigationItemSelectedListener(this);
        View headerView = mNavigationView.getHeaderView(0);

        mBuildingTitle = (TextView) headerView.findViewById(R.id.building_title);
        mBuildingDescription = (TextView) headerView.findViewById(R.id.description);
        mHeaderImageView = (ImageView) headerView.findViewById(R.id.imageView);
        mToggleButton = (ImageView) headerView.findViewById(R.id.toggle_description);

        setDataFromInfoLocation();
        createNavigationMenu();

        selectForeground();
        selectFromSearch();
    }

    private void setDataFromInfoLocation() {
        // ## Toolbar ##
        setSupportActionBar(mToolbar);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, mDrawer, mToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        toggle.syncState();

        // ## Drawer ##
        mDrawer.addDrawerListener(this);

        // ## Building Title ##
        String title = mBuilding.getName() + " - " + mBuilding.getNumber() + "번건물";
        mBuildingTitle.setText(title);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(mBuilding.getName());

            if (DEBUG) Log.i(TAG, "onCreate: ToolBar Title : " + getSupportActionBar().getTitle());
        }

        // ## Building Description ##
        String desc = mBuilding.getDescription();
        if (desc == null || desc.length() <= 0) {
            desc = "여분으로 보여질 텍스트1";
            desc += "\n여분으로 보여질 텍스트2";
            desc += "\n여분으로 보여질 텍스트3";
            desc += "\n여분으로 보여질 텍스트4";
        }
        mBuildingDescription.setText(desc);

        // ## Set Building Image ##
        String name = "building_" + mInfoLocation.getBuildingID();
        int resourceID = getResources().getIdentifier(name, "drawable", getPackageName());
        if (resourceID != 0) {
            mHeaderImageView.setImageResource(resourceID);
        } else {
            mHeaderImageView.setImageResource(R.drawable.image_not_found);
        }

        // ## Description Toggle Button ##
        mToggleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mBuildingDescription.getVisibility() == View.VISIBLE) {
                    collapse(mBuildingDescription);
                } else {
                    mBuildingDescription.setVisibility(View.VISIBLE);
                    expand(mBuildingDescription);
                }
            }
        });

        // ## FloorPager ##
        mFloorAdapter = new FloorPagerAdapter(getSupportFragmentManager(), this, mInfoLocation.getBuildingID());
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

        // TabLayout
        mTabLayout.setupWithViewPager(mFloorPager);
    }

    private void createNavigationMenu() {
        // ## Create Menu ##
        Menu menu = mNavigationView.getMenu();
        SubMenu subMenu = menu.addSubMenu(getResources().getString(R.string.sub_title_main_rooms));
        subMenu.setGroupCheckable(0, true, true);

        for (int i = 0; i < mMainRoomList.size(); i++) {
            InfoLocation item = mMainRoomList.get(i);
            subMenu.add(R.id.nav_group, i, 0, item.getName())
                    .setIcon(R.drawable.side_nav_bar_list_item);
        }
    }

    private void selectForeground() {
        for (int i = 0; i < mFloorAdapter.getCount(); i++) {
            Floor floor = mFloorAdapter.getFloor(i);
            if (floor.getFloor() > 0) {
                mFloorPager.setCurrentItem(i, true);
                break;
            }
        }
    }

    private void selectFromSearch() {
        if ( !mInfoLocation.getTag().equals(InfoLocation.TAG_BUILDING) ) {
            focusRoom(mInfoLocation.getFloorID(), mInfoLocation.getRoomID());
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

        if (mPrevMenuItem != null) {
            mPrevMenuItem.setChecked(false);
        }
        menuItem.setChecked(true);
        mPrevMenuItem = menuItem;

        if (groupID == R.id.nav_group) {
            InfoLocation infoLocation = mMainRoomList.get(id);

            if (infoLocation != null) {
                if (DEBUG)
                    Log.i(TAG, "onNavigationItemSelected: Building : " + infoLocation.getName() + ", [" + infoLocation.getBuildingID() + "," + infoLocation.getFloorID() + "," + infoLocation.getRoomID() + "]");

                focusRoom(infoLocation.getFloorID(), infoLocation.getRoomID());
            }
        }

        mDrawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void focusRoom(final int floorID, final int roomID) {
        if (DEBUG) Log.i(TAG, "focusRoom: called");

        int floorIndex = -1;

        for (int i = 0; i < mFloorAdapter.getCount(); i++) {
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
                public void OnFragmentDestroy(int position) {
                }
            });
        }
    }

    public void expand(final View v) {
        v.measure(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        final int targetHeight = v.getMeasuredHeight();

        // Older versions of android (pre API 21) cancel animations for views with a height of 0.
        v.getLayoutParams().height = 1;
        v.setVisibility(View.VISIBLE);
        Animation a = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                v.getLayoutParams().height = interpolatedTime == 1
                        ? ViewGroup.LayoutParams.WRAP_CONTENT
                        : (int) (targetHeight * interpolatedTime);
                v.requestLayout();
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };

        // 1dp/ms
        a.setDuration((int) (targetHeight / v.getContext().getResources().getDisplayMetrics().density));
        a.setInterpolator(new CubicBezierInterpolator(0.4, 0, 0.2, 1));
        v.startAnimation(a);
    }

    public void collapse(final View v) {
        final int initialHeight = v.getMeasuredHeight();

        Animation a = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                if (interpolatedTime == 1) {
                    v.setVisibility(View.GONE);
                } else {
                    v.getLayoutParams().height = initialHeight - (int) (initialHeight * interpolatedTime);
                    v.requestLayout();
                }
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };

        // 1dp/ms
        a.setDuration((int) (initialHeight / v.getContext().getResources().getDisplayMetrics().density));
        a.setInterpolator(new CubicBezierInterpolator(0.4, 0, 0.2, 1));
        v.startAnimation(a);
    }

    //  ## For Drawer (( DrawerLayout.DrawerListener )) ##
    @Override
    public void onDrawerOpened(View drawerView) {

    }

    @Override
    public void onDrawerClosed(View drawerView) {
        mBuildingDescription.setVisibility(View.GONE);
    }

    @Override
    public void onDrawerStateChanged(int newState) {

    }

    @Override
    public void onDrawerSlide(View drawerView, float slideOffset) {

    }
}
