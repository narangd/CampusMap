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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.campusmap.R;
import com.example.campusmap.tree.branch.Building;
import com.example.campusmap.xmlparser.BuildingInfoParser;

import java.io.Serializable;
import java.util.ArrayList;

public class CampusmapActivity extends AppCompatActivity {
    static BuildingInfoParser parser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_campus_map);

        ViewPager mViewPager = (ViewPager) findViewById(R.id.container);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

        Intent intent = getIntent();
        Serializable data = intent.getSerializableExtra("path");
        ArrayList<Integer> path;
        if (data instanceof ArrayList ) {
            if (((ArrayList) data).get(0) instanceof Integer) {
                path = (ArrayList<Integer>) data;

                FloorPagerAdapter mFloorPagerAdapter = new FloorPagerAdapter(getSupportFragmentManager(), path.get(0));
                mViewPager.setAdapter(mFloorPagerAdapter);
                if (path.size() > 1) {
                    mViewPager.setCurrentItem(path.get(1), true);
                }
            }
        }

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
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class FloorFragment extends Fragment {
        private static final String ARG_SECTION_NUMBER = "floor_number";
        private static final String FLOOR_NUMBER = "room_number";

        public FloorFragment() {
        }

        public static FloorFragment newInstance(int building_num, int floor_num) {
            FloorFragment fragment = new FloorFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, floor_num);
            args.putInt(FLOOR_NUMBER, building_num);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_building, container, false);

            int floor_index = getArguments().getInt(ARG_SECTION_NUMBER);
            int building_index = getArguments().getInt(FLOOR_NUMBER);

            ListView listView = (ListView) rootView.findViewById(R.id.room_listview);
            listView.setAdapter(new ArrayAdapter(
                    getContext(),
                    android.R.layout.simple_list_item_1,
                    parser.toRoomList(building_index, floor_index)
            ));
            return rootView;
        }
    }

    public class FloorPagerAdapter extends FragmentPagerAdapter {
        ArrayList<Integer> path;
        Building building;
        int mBuildingIndex;

        public FloorPagerAdapter(FragmentManager fm, int buildingIndex) {
            super(fm);
            if (path.size() > 0) {
                mBuildingIndex = buildingIndex;
                building = parser.toFloorList(buildingIndex);
            }
        }

        @Override
        public Fragment getItem(int position) {
            return FloorFragment.newInstance(mBuildingIndex, position);
        }

        @Override
        public int getCount() {
            if (building != null) {
                return building.size(); // floor size
            }
            return 0;
        }

        @Override
        public CharSequence getPageTitle(int position) {
//            switch (position) {
//                case 0:
//                    return "SECTION 1";
//                case 1:
//                    return "SECTION 2";
//                case 2:
//                    return "SECTION 3";
//            }
            return building.get(position).toString();
        }
    }
}
