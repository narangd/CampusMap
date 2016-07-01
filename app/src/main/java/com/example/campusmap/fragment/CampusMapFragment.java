package com.example.campusmap.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.campusmap.R;
import com.example.campusmap.activity.CampusMapActivity;
import com.example.campusmap.activity.ScrollingActivity;
import com.example.campusmap.tree.branch.Building;
import com.example.campusmap.tree.branch.Floor;
import com.example.campusmap.tree.branch.Parent;
import com.example.campusmap.xmlparser.BuildingInfoParser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;

public class CampusMapFragment extends Fragment implements AdapterView.OnItemClickListener {
    public static final int TAP_INDEX = 0;
    private static CampusMapFragment fragment = null;
    private Context context;

    /**
     * return only one CampusMapFragment.
     * @return
     */
    public static CampusMapFragment newInstance() {
        if (fragment == null) {
            fragment = new CampusMapFragment();
        }
//        Bundle args = new Bundle();
//        args.putString(ARG_PARAM1, param1);
//        fragment.setArguments(args);
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

        BuildingInfoParser parser = BuildingInfoParser.getInstance(getResources().getXml(R.xml.building_info));

//        if (parser.toBuildingList())
        ArrayAdapter<Building> adapter = new ArrayAdapter<>(
                context,
                android.R.layout.simple_list_item_1,
                parser.toBuildingList()
        );

        ListView listView = (ListView) rootView.findViewById(R.id.building_list);
        ViewGroup imageHeader = (ViewGroup) inflater.inflate(R.layout.header_building, listView, false);
        listView.addHeaderView(imageHeader, null, false);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(this);

        if (getArguments() != null) {
            int building = getArguments().getInt("building");
            onItemClick(null, null, building, 0);
        }

        return rootView;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        Intent intent = new Intent(context, CampusMapActivity.class);
        intent.putExtra("building_id", position);
        startActivity(intent);
    }

    /*public void sendPath(Parent parent) {
        LinkedList<Parent> parentArrayList = new LinkedList<>();
        ArrayList<Integer> path = new ArrayList<>();
        while (parent.getParent() != null) {
            parentArrayList.addFirst(parent.getParent());
            parent = parent.getParent();
        }
        if (parentArrayList.size() <= 0) {
            return;
        }

        if (parentArrayList.size() > 0) {
            for (Building building : parser.toBuildingList()) {
                if (building.toString().equals(parentArrayList.get(0).toString())) {
                    path.add(parser.toBuildingList().indexOf(building));
                    break;
                }
            }
        }
        if (parentArrayList.size() > 1) {
            for (Floor floor : parser.toFloorList(path.get(0))) {
                if (floor.toString().equals(parentArrayList.get(1).toString())) {
                    path.add(parser.toFloorList(path.get(0)).indexOf(floor));
                    break;
                }
            }
        }

        Toast.makeText(context, Arrays.toString(parentArrayList.toArray()) + "를 찾습니다.(" + Arrays.toString(path.toArray()) + ")", Toast.LENGTH_LONG)
                .show();
        Intent intent = new Intent(getContext(), CampusMapActivity.class);
        intent.putExtra("path", path);
        startActivity(intent);
     }*/
}
