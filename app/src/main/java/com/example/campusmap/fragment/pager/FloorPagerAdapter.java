package com.example.campusmap.fragment.pager;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.Log;
import android.view.ViewGroup;

import com.example.campusmap.database.SQLiteHelperCampusInfo;
import com.example.campusmap.fragment.RoomListFragment;
import com.example.campusmap.tree.branch.Floor;

import java.util.ArrayList;

public class FloorPagerAdapter extends FragmentPagerAdapter {
    private static final String TAG = "FloorPagerAdapter";

    private final ArrayList<Floor> mFloorList;
    private OnFragmentListener mOnFragmentListener;

    public FloorPagerAdapter(FragmentManager fm, Context context, int buildingID) {
        super(fm);

        // ## Get DataBase ##
        SQLiteHelperCampusInfo helper = SQLiteHelperCampusInfo.getInstance(context);
        mFloorList = helper.getFloorList(buildingID);
    }

    @Override
    public Fragment getItem(int position) {
        return RoomListFragment.newInstance(mFloorList.get(position));
    }

    @Override
    public CharSequence getPageTitle(int position) {
        int floor = mFloorList.get(position).getFloor();
        String title = "";
        if (floor < 0) {
            floor = -floor;
            title += "지하 ";
        }
        return title + floor + "층";
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
        Log.i(TAG, "instantiateItem: called!! " + position);
        Fragment fragment = (Fragment) super.instantiateItem(container, position);

        if (mOnFragmentListener != null) {
            mOnFragmentListener.OnFragmentInstantiate(fragment, position);
        }
        return fragment;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        super.destroyItem(container, position, object);

        if (mOnFragmentListener != null) {
            mOnFragmentListener.OnFragmentDestroy(position);
        }
    }

    public void setOnFragmentListener(OnFragmentListener onFragmentListener) {
        mOnFragmentListener = onFragmentListener;
    }

    public interface OnFragmentListener {
        void OnFragmentInstantiate(Fragment fragment, int position);
        void OnFragmentDestroy(int position);
    }
}
