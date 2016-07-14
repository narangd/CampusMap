package com.example.campusmap.fragment.pager;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.example.campusmap.database.SearchResultItem;
import com.example.campusmap.fragment.BuildingDetailFragment;
import com.example.campusmap.fragment.FloorListFragment;
import com.example.campusmap.tree.branch.Building;

/**
 * Created by 성용 on 2016-06-29.
 */
public class CampusMapPagerAdapter extends FragmentPagerAdapter {
    private static final int COUNT = 2;

    private Building mBuilding;

    public CampusMapPagerAdapter(FragmentManager fm, Building building) {
        super(fm);
        mBuilding = building;
    }

    public CampusMapPagerAdapter(FragmentManager fm, SearchResultItem resultItem) {
        super(fm);
//        mBuildingID = resultItem.mParentId;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return BuildingDetailFragment.newInstance(mBuilding.getID());
            case 1:
                return FloorListFragment.newInstance(mBuilding.getID());
        }
        return null;
    }

    @Override
    public int getCount() {
        return COUNT;
    }
}
