package com.example.campusmap.activity;

import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.view.PagerTabStrip;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.example.campusmap.R;
import com.example.campusmap.tree.ListViewTree;
import com.example.campusmap.tree.branch.Building;
import com.example.campusmap.tree.branch.Floor;
import com.example.campusmap.xmlparser.BuildingInfoParser;

import java.io.Serializable;
import java.util.ArrayList;

public class CampusmapActivity extends AppCompatActivity {
    static BuildingInfoParser parser;

    private SectionsPagerAdapter mSectionsPagerAdapter;

    private ViewPager mViewPager;

    ArrayList<Integer> path;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_campus_map);

        Intent intent = getIntent();
        Serializable data = intent.getSerializableExtra("path");
        path = (ArrayList<Integer>)data;
        if (parser == null)
            parser = BuildingInfoParser.getInstance(getResources().getXml(R.xml.building_info));

//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager(), path);

        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        if (path.size() > 1) {
            mViewPager.setCurrentItem(path.get(1), true);
        }

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });


    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        private static final String ARG_SECTION_NUMBER = "section_number";

        public PlaceholderFragment() {
        }

        public static PlaceholderFragment newInstance(int building_num, int floor_num) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, floor_num);
            args.putInt("building", building_num);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_building, container, false);
            int floor_index = getArguments().getInt(ARG_SECTION_NUMBER);
            int building_index = getArguments().getInt("building");
            ListView listView = (ListView) rootView.findViewById(R.id.room_listview);
            listView.setAdapter(new ArrayAdapter(
                    getContext(),
                    android.R.layout.simple_list_item_1,
                    parser.toRoomList(building_index, floor_index)
            ));
            return rootView;
        }
    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter {
        ArrayList<Integer> path;
        Building building;
        int buildingindex;

        public SectionsPagerAdapter(FragmentManager fm, ArrayList<Integer> path) {
            super(fm);
            this.path = path;
            if (path.size() > 0) {
                buildingindex = path.get(0);
                building = parser.toBuildingList().get(buildingindex);
            }
        }

        @Override
        public Fragment getItem(int position) {
            return PlaceholderFragment.newInstance(buildingindex, position);
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
