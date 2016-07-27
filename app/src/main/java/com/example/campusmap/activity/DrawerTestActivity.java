package com.example.campusmap.activity;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
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
import com.example.campusmap.database.SQLiteHelperCampusInfo;
import com.example.campusmap.database.SearchResultItem;
import com.example.campusmap.fragment.RoomListFragment;
import com.example.campusmap.fragment.pager.FloorPagerAdapter;
import com.example.campusmap.tree.branch.Floor;

import nl.codesoup.cubicbezier.CubicBezierInterpolator;

public class DrawerTestActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener {

    private static final String TAG = "DrawerTestActivity";
    public static final String KEY_BUILDING_ID = "building_id";
    public static final String KEY_SEACH_ITEM = "search_item";
    private static final boolean DEBUG = false;

    private DrawerLayout mDrawer;
    private ArrayAdapter<Pair<String,Integer[]>> mAdapter;
    private ViewPager mFloorPager;
    private FloorPagerAdapter mFloorAdapter;
    private boolean isTried = false;
    private SearchResultItem mResultItem;
    private TextView mDescTextView;
//    private Animation mShowAnimation;
//    private Animation mHideAnimation;

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
        int buildingID = intent.getIntExtra(KEY_BUILDING_ID, -1);
        mResultItem = (SearchResultItem)intent.getSerializableExtra(KEY_SEACH_ITEM);

        if (buildingID == -1 && mResultItem == null) {
            finish();
            Log.e(TAG, "onCreate: 입력한 값이 없습니다. (ex Building ID or SearchItem");
            return;
        }
        if (buildingID == -1) {
            Log.i(TAG, "onCreate: getResultItem" + mResultItem.mBuildingID + "," + mResultItem.mFloorID + "," + mResultItem.mRoomID);
            buildingID = mResultItem.mBuildingID;
        }

//        mShowAnimation = new ScaleAnimation(1.0f, 1.0f, 0.0f, 1.0f);
//        mShowAnimation.setInterpolator(new DecelerateInterpolator());
//        mShowAnimation.setDuration(1000);
//        mHideAnimation = new ScaleAnimation(1.0f, 1.0f, 1.0f, 0.0f);
//        mHideAnimation.setInterpolator(new AccelerateInterpolator());
//        mHideAnimation.setDuration(1000);
//        mHideAnimation.setAnimationListener(new Animation.AnimationListener() {
//            @Override
//            public void onAnimationStart(Animation animation) {}
//            @Override
//            public void onAnimationEnd(Animation animation) {
//                if (mDescTextView != null) {
//                    mDescTextView.setVisibility(View.GONE);
//                }
//            }
//            @Override
//            public void onAnimationRepeat(Animation animation) {}
//        });

        // ## Get DataBase ##
        SQLiteHelperCampusInfo helper = SQLiteHelperCampusInfo.getInstance(this);
        SQLiteDatabase db = helper.getReadableDatabase();

        // ## Get Building Detail ##
        ContentValues buildingDetailValues = helper.getBuildingDetail(db, buildingID /* 100주년기념관 */);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(
                    buildingDetailValues.getAsString(SQLiteHelperCampusInfo.BuildingEntry.COLUMN_NAME_NAME)
            );
            if (DEBUG) Log.i(TAG, "onCreate: ToolBar Title : " + getSupportActionBar().getTitle());
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
            View headerLayout = mNavigationView.getHeaderView(0);

            // ## Building Description ##
            mDescTextView = (TextView) headerLayout.findViewById(R.id.description);
            if (mDescTextView != null) {
                String desc = buildingDetailValues.getAsString(SQLiteHelperCampusInfo.BuildingEntry.COLUMN_NAME_DESCRIPTION);
                desc += "\n여분으로 보여질 텍스트1";
                desc += "\n여분으로 보여질 텍스트2";
                desc += "\n여분으로 보여질 텍스트3";
                desc += "\n여분으로 보여질 텍스트4";
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
            mFloorAdapter = new FloorPagerAdapter(getSupportFragmentManager(), this, buildingID);
            mFloorPager.setAdapter(mFloorAdapter);
        }
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        if (tabLayout != null) {
            tabLayout.setupWithViewPager(mFloorPager);
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

    @Override
    protected void onStart() {
        super.onStart();
        if (DEBUG) Log.i(TAG, "onStart: called!!");

        if (mResultItem != null && !isTried) {
            if (DEBUG) Log.i(TAG, "onStart: focus! : " + mResultItem.mFloorID + ", " + mResultItem.mRoomID);
            focusRoom(mResultItem.mFloorID, mResultItem.mRoomID);
            isTried = true;
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

        menuItem.setChecked(true);

        if (groupID == Menu.FIRST) {
            Pair<String, Integer[]> item = mAdapter.getItem(id);
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
}
