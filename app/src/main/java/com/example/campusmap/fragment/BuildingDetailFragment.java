package com.example.campusmap.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.example.campusmap.R;
import com.example.campusmap.data.branch.Building;
import com.example.campusmap.database.SQLiteHelperCampusInfo;

public class BuildingDetailFragment extends Fragment implements AdapterView.OnItemClickListener {
    private static final String BUILDING_ID = "building";

    private ArrayAdapter<Pair<String,Integer[]>> mAdapter;

    public BuildingDetailFragment() {
        // Required empty public constructor
    }
    public static BuildingDetailFragment newInstance(int buildingID) {
        BuildingDetailFragment fragment = new BuildingDetailFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(BUILDING_ID, buildingID);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_building_detail, container, false);

        Toolbar toolbar = (Toolbar) rootView.findViewById(R.id.toolbar);
        ListView listView = (ListView) rootView.findViewById(R.id.list_main_rooms);

        // # TextView In Header
        ViewGroup detailView = (ViewGroup) inflater.inflate(R.layout.header_building_detail, listView, false);
        TextView textView = (TextView) detailView.findViewById(R.id.description);

        Bundle bundle = getArguments();
        if (bundle != null) {
            int buildingID = bundle.getInt(BUILDING_ID);
            SQLiteHelperCampusInfo helper = SQLiteHelperCampusInfo.getInstance(getContext());

            Building building = helper.getBuildingDetail(buildingID);
            if (building != null) {
                toolbar.setTitle(building.getName());
                textView.setText(building.getDescription());
            }

//            mAdapter = new MainRoomArrayAdapter(
//                    getContext(),
//                    android.R.layout.simple_list_item_1,
//                    helper.getMainRooms(buildingID)
//            );
            listView.addHeaderView(detailView, null, false);
//            listView.setAdapter(mAdapter);
            listView.setOnItemClickListener(this);
        } else {
            textView.setText("설명들...");
            listView.setAdapter(new ArrayAdapter<>(
                    getContext(),
                    android.R.layout.simple_list_item_1,
                    new String[]{"주요건물1", "주요건물2", "주요걸물3"}
            ));
        }
        return rootView;
    }

    // ## For ListView ##
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//        Pair<String,Integer[]> item = mAdapter.getItem(position);
//        Toast.makeText(
//                getContext(),
//                "건물ID:"+item.second[0]+", 층ID:"+item.second[1]+", 방ID:"+item.second[2],
//                Toast.LENGTH_SHORT).
//                show();
    }
}
