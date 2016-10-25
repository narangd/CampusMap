package com.example.campusmap.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.campusmap.R;
import com.example.campusmap.activity.DrawerTestActivity;
import com.example.campusmap.activity.InfoUpdaterActivity;
import com.example.campusmap.data.branch.Building;
import com.example.campusmap.database.SQLiteHelperCampusInfo;
import com.example.campusmap.form.InfoLocation;

import java.util.ArrayList;

public class CampusMapFragment extends Fragment implements AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener {
    private static final String TAG = "CampusMapFragment";
    public static final int TAP_INDEX = 0;

    private ListView mListView;
    private int prevCheckIndex = 0;
    private ArrayList<Building> mBuildingList;

    public static CampusMapFragment newInstance() {
        return new CampusMapFragment();
    }

    public CampusMapFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_campus_map, container, false);

        mListView = (ListView) rootView.findViewById(R.id.building_list);

        ViewGroup imageHeader = (ViewGroup) inflater.inflate(R.layout.header_building, mListView, false);

        SQLiteHelperCampusInfo helper = SQLiteHelperCampusInfo.getInstance(getActivity());
        mBuildingList = helper.getBuildingList();

        if (mListView != null) {
            mListView.addHeaderView(imageHeader, null, false);
            ArrayAdapter<Building> mAdapter = new ArrayAdapter<>(
                    getActivity(),
                    android.R.layout.simple_list_item_1,
                    mBuildingList
            );
            mListView.setAdapter(mAdapter);
            mListView.setOnItemClickListener(this);
            mListView.setOnItemLongClickListener(this);
        }

        return rootView;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Building building = mBuildingList.get(position-1);

        Intent intent = new Intent(getActivity(), DrawerTestActivity.class);
        intent.putExtra(
                DrawerTestActivity.KEY_INFO_LOCATION,
                new InfoLocation(building.getName(), InfoLocation.TAG_BUILDING, building.getID(), InfoLocation.NONE, InfoLocation.NONE)
        );
        startActivity(intent);
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        Building building = mBuildingList.get(position-1); // 0 is header
        Intent intent = new Intent(getActivity(), InfoUpdaterActivity.class);
        intent.putExtra(
                InfoUpdaterActivity.KEY_INFO_LOCATION,
                new InfoLocation(building.getName(), InfoLocation.TAG_BUILDING, building.getID(), InfoLocation.NONE, InfoLocation.NONE)
        );
        startActivity(intent);
        return true;
    }
}
