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

import com.example.campusmap.R;
import com.example.campusmap.activity.DrawerTestActivity;
import com.example.campusmap.activity.InfoUpdaterActivity;
import com.example.campusmap.database.InfoLocation;
import com.example.campusmap.database.SQLiteHelperCampusInfo;
import com.example.campusmap.tree.branch.Building;
import com.example.campusmap.view.TouchImageView;

public class CampusMapFragment extends Fragment implements AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener {
    private static final String TAG = "CampusMapFragment";
    public static final int TAP_INDEX = 0;

    private Context context;
    private ArrayAdapter<Building> mAdapter;
    private ListView listView;
    private int prevCheckIndex = 0;

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
        context = rootView.getContext();

        listView = (ListView) rootView.findViewById(R.id.building_list);

        ViewGroup imageHeader = (ViewGroup) inflater.inflate(R.layout.header_building, listView, false);
        if (imageHeader != null) {
            TouchImageView touchImageView = (TouchImageView) imageHeader.findViewById(R.id.campus_map_view);
            touchImageView.setMaxZoom(4.0f);
        }

        SQLiteHelperCampusInfo helper = SQLiteHelperCampusInfo.getInstance(context);
        SQLiteDatabase db = helper.getReadableDatabase();

        if (listView != null) {
            listView.addHeaderView(imageHeader, null, false);
            mAdapter = new ArrayAdapter<>(
                    context,
                    android.R.layout.simple_list_item_1,
                    helper.getBuildingList(db)
            );
            listView.setAdapter(mAdapter);
            listView.setOnItemClickListener(this);
            listView.setOnItemLongClickListener(this);
        }

        if (getArguments() != null) {
            int building = getArguments().getInt("building");
            onItemClick(null, null, building, 0);
        }
        return rootView;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        listView.setItemChecked(prevCheckIndex, false);
        prevCheckIndex = position;
        listView.setItemChecked(position, true);
//        parent.set

        Building building = mAdapter.getItem(position-1);

        Intent intent = new Intent(context, DrawerTestActivity.class);
        intent.putExtra(DrawerTestActivity.KEY_BUILDING_ID, building.getID()); // id to index
        startActivity(intent);
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        Building building = mAdapter.getItem(position);
        Intent intent = new Intent(getActivity(), InfoUpdaterActivity.class);
        intent.putExtra(
                InfoUpdaterActivity.KEY_INFO_LOCATION,
                new InfoLocation(building.getName(), building.getID(), InfoLocation.NONE, InfoLocation.NONE)
        );
        startActivity(intent);
        return true;
    }
}
