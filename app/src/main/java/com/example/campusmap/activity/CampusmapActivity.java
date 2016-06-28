package com.example.campusmap.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.campusmap.R;
import com.example.campusmap.tree.branch.Building;
import com.example.campusmap.tree.branch.University;
import com.example.campusmap.xmlparser.BuildingInfoParser;

import java.io.Serializable;
import java.util.ArrayList;

public class CampusmapActivity extends AppCompatActivity {
    private static final String TAG = "CampusMapActivity";
    private static BuildingInfoParser parser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_campus_map);

        ViewPager mViewPager = (ViewPager) findViewById(R.id.container);

        findViewById(R.id.fab). // FloatingActionButton
                setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        if (parser == null)
            parser = BuildingInfoParser.getInstance(getResources().getXml(R.xml.building_info));
        University buildingList = parser.toBuildingList();

//        Intent intent = getIntent();
//        Serializable data;
//
//        data = intent.getSerializableExtra("building");
//        if (data != null) {
//            Log.i(TAG, "onCreate: data to buildingNumber");
//            int buildingNumber = (int)data;
//
//            FloorPagerAdapter mFloorPagerAdapter = new FloorPagerAdapter(getSupportFragmentManager(),
//                    buildingList.get(buildingNumber) );
//            Log.i(TAG, "onCreate: mFloorPagerAdapter set " + buildingList.get(buildingNumber).toString());
//            mViewPager.setAdapter(mFloorPagerAdapter);
//        } else {
        {
            FloorPagerAdapter mFloorPagerAdapter = new FloorPagerAdapter(getSupportFragmentManager(),
                    buildingList.get(0) );
            mViewPager.setAdapter(mFloorPagerAdapter);
            mViewPager.setCurrentItem(0);
        }

        ((TabLayout) findViewById(R.id.tabs)).
                setupWithViewPager(mViewPager);
    }



    /**
     * A placeholder fragment containing a simple view.
     */
    public static class FloorFragment extends Fragment {
        private static final String BUILDING = "building";
        private static final String FLOOR_NUMBER = "floor_number";

        public FloorFragment() {
        }

        public static FloorFragment newInstance(Building building, int floor_num) {
            FloorFragment fragment = new FloorFragment();

            Bundle args = new Bundle();
            args.putSerializable(BUILDING, building);
            args.putInt(FLOOR_NUMBER, floor_num);
            fragment.setArguments(args);

            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_building, container, false);

            Building building = (Building)getArguments().getSerializable(BUILDING);
            int floor_index = getArguments().getInt(FLOOR_NUMBER);

            ListView listView = (ListView) rootView.findViewById(R.id.room_listview);
            listView.setAdapter(new ArrayAdapter<>(
                    getContext(),
                    android.R.layout.simple_list_item_1,
                    new String[] { "A", "B", "C", "D"}
            ));
            return rootView;
        }
    }

    public class FloorPagerAdapter extends FragmentPagerAdapter {
        Building mBuilding;

        public FloorPagerAdapter(FragmentManager fm, Building building) {
            super(fm);
            mBuilding = building;
        }

        @Override
        public Fragment getItem(int position) {
            return FloorFragment.newInstance(mBuilding, position);
        }

        @Override
        public int getCount() {
            if (mBuilding != null) {
                return mBuilding.size(); // floor size
            }
            return 0;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mBuilding.get(position).toString();
        }
    }
}
