package com.example.campusmap.fragment.pager;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.example.campusmap.database.SQLiteHelperCampusInfo;
import com.example.campusmap.fragment.RoomListFragment;
import com.example.campusmap.tree.branch.Floor;

import java.util.ArrayList;

public class FloorPagerAdapter extends FragmentPagerAdapter {

    private final ArrayList<Floor> mFloorList;

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


}
