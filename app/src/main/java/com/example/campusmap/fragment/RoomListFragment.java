package com.example.campusmap.fragment;


import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.campusmap.R;
import com.example.campusmap.database.SQLiteHelperCampusInfo;
import com.example.campusmap.tree.branch.Floor;
import com.example.campusmap.tree.branch.Room;

/**
 * A simple {@link Fragment} subclass.
 */
public class RoomListFragment extends Fragment {
    private static final String KEY_FLOOR = "floor";
    private ListView mListView;

    public RoomListFragment() {
        // Required empty public constructor
    }

    public static RoomListFragment newInstance(Floor floor) {
        RoomListFragment fragment = new RoomListFragment();
        Bundle args = new Bundle();
        args.putSerializable(KEY_FLOOR, floor);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_floor_list, container, false);
        mListView = (ListView) view.findViewById(R.id.list_room);

        if (getArguments() != null) {
            Floor mFloor = (Floor) getArguments().getSerializable(KEY_FLOOR);
            if (mFloor != null) {

                // ## Get DataBase ##
                SQLiteHelperCampusInfo helper = SQLiteHelperCampusInfo.getInstance(getContext());
                SQLiteDatabase db = helper.getReadableDatabase();

                ArrayAdapter<String> roomArrayAdapter = new ArrayAdapter<>(
                        getContext(),
                        android.R.layout.simple_list_item_1
                );
                for (Room room : helper.getRoomList(db, mFloor.getBuildingID(), mFloor.getID())){
                    roomArrayAdapter.add(room.toString());
                }

                mListView.setAdapter(roomArrayAdapter);
            }
            // getFloorList => arraylist
        }
        return view;
    }

    public void focusItem(int position) {

    }

}
