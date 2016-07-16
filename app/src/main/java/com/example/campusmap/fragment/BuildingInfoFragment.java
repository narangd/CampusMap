package com.example.campusmap.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ListView;

import com.example.campusmap.R;
import com.example.campusmap.tree.ListViewTree;

public class BuildingInfoFragment extends Fragment {
    public static final int TAP_INDEX = 1;
    private static BuildingInfoFragment fragment = null;
    ListViewTree listViewTree;
    private Context context;

    public static BuildingInfoFragment getInstance() {
        if (fragment == null) {
            fragment = new BuildingInfoFragment();
        }
//        Bundle args = new Bundle();
//        args.putString(ARG_PARAM1, param1);
//        fragment.setArguments(args);
        return fragment;
    }

    public BuildingInfoFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_building_info, container, false);
        context = rootView.getContext();

        listViewTree = new ListViewTree(context);

        listViewTree.setAnimation(
                AnimationUtils.loadAnimation(context, R.anim.appear_listview),
                AnimationUtils.loadAnimation(context, R.anim.dark)
        );

        listViewTree.setRoot(
                (ListView) rootView.findViewById(R.id.buildinginfo_buildinglist)
        );
        listViewTree.addBranch(
                (ListView) rootView.findViewById(R.id.buildinginfo_floorlist)
        );
        listViewTree.addBranch(
                (ListView) rootView.findViewById(R.id.buildinginfo_roomlist)
        );

        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();

        listViewTree.complete();
    }

    public void onBackPressed() {
        listViewTree.hideLastListView();
    }

    public int getIndex() {
        return listViewTree.getFocusedIndex();
    }

}
