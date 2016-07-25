package com.example.campusmap.fragment;

import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.example.campusmap.R;
import com.example.campusmap.activity.BuildingActivity;
import com.example.campusmap.database.SQLiteHelperCampusInfo;
import com.example.campusmap.tree.branch.Building;
import com.example.campusmap.tree.branch.BuildingLocation;
import com.example.campusmap.view.TouchImageView;

import java.util.ArrayList;

public class CampusMapFragment extends Fragment implements AdapterView.OnItemClickListener {
    private static final String TAG = "CampusMapFragment";
    public static final int TAP_INDEX = 0;
    private static CampusMapFragment fragment = null;

    private Context context;
    private ListView mListView;
    private ArrayAdapter<Building> mAdapter;
    private Toast mToast;
    private ViewGroup mImageHeader;
    private TouchImageView mTouchImageView;
    private ArrayList<BuildingLocation> mTrigerBoxes = new ArrayList<>();

    /**
     * return only one CampusMapFragment.
     * @return
     */
    public static CampusMapFragment newInstance() {
        if (fragment == null) {
            fragment = new CampusMapFragment();
        }
        return fragment;
    }

    public CampusMapFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_campus_map, container, false);
        context = rootView.getContext();

        mToast = Toast.makeText(context, "", Toast.LENGTH_SHORT);

        mListView = (ListView) rootView.findViewById(R.id.building_list);
        mImageHeader = (ViewGroup) inflater.inflate(R.layout.header_building, mListView, false);
        if (mImageHeader != null) {
            mTouchImageView = (TouchImageView) mImageHeader.findViewById(R.id.campus_map_view);
        }
        if (mListView != null) {
            mListView.addHeaderView(mImageHeader, null, false);
        }

        if (getArguments() != null) {
            int building = getArguments().getInt("building");
            onItemClick(null, null, building, 0);
        }
        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();

        SQLiteHelperCampusInfo helper = SQLiteHelperCampusInfo.getInstance(context);
        SQLiteDatabase db = helper.getReadableDatabase();

        if (mTouchImageView != null) {
            mTouchImageView.setMaxZoom(4.0f);
        }

        if (mListView != null) {
            mAdapter = new ArrayAdapter<>(
                    context,
                    android.R.layout.simple_list_item_1,
                    helper.getBuildingList(db)
            );
            mListView.setAdapter(mAdapter);
            mListView.setOnItemClickListener(this);
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intent = new Intent(context, BuildingActivity.class);
        intent.putExtra(BuildingActivity.KEY_BUILDING, mAdapter.getItem(position-1)); // id to index
        startActivity(intent);
    }
}
