package com.example.campusmap.fragment.pager;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.SparseArray;
import android.view.ViewGroup;

import com.example.campusmap.database.SQLiteHelperCampusInfo;
import com.example.campusmap.fragment.RoomListFragment;
import com.example.campusmap.tree.branch.Floor;

import java.util.ArrayList;

public class FloorPagerAdapter extends FragmentPagerAdapter {

    private final ArrayList<Floor> mFloorList;
    private SparseArray<Fragment> registeredFragments = new SparseArray<Fragment>();

    public FloorPagerAdapter(FragmentManager fm, Context context, int buildingID) {
        super(fm);

        // ## Get DataBase ##
        SQLiteHelperCampusInfo helper = SQLiteHelperCampusInfo.getInstance(context);
        SQLiteDatabase db = helper.getReadableDatabase();
        mFloorList = helper.getFloorList(db, buildingID);
    }

    @Override
    public Fragment getItem(int position) {
        return RoomListFragment.newInstance(mFloorList.get(position));
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mFloorList.get(position).getFloor() + "층";
    }

    @Override
    public int getCount() {
        return mFloorList.size();
    }

    public Floor getFloor(int position) {
        return mFloorList.get(position);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        Fragment fragment = (Fragment) super.instantiateItem(container, position);
        registeredFragments.put(position, fragment);
        return fragment;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        registeredFragments.remove(position);
        super.destroyItem(container, position, object);
    }

    public Fragment getRegisteredFragment(int position) {
        return registeredFragments.get(position);
    }
}
