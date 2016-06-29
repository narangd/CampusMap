package com.example.campusmap.fragment.pager;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.example.campusmap.fragment.CampusMapFragment;
import com.example.campusmap.fragment.FloorListFragment;

/**
 * Created by 성용 on 2016-06-29.
 */
public class CampusMapPagerAdapter extends FragmentPagerAdapter {
    private static final int COUNT = 2;

    public CampusMapPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return CampusMapFragment.newInstance();
            case 1:
                return FloorListFragment.newInstance();
        }
        return null;
    }

    @Override
    public int getCount() {
        return COUNT;
    }
}
