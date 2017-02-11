package com.example.campusmap.fragment;


import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.campusmap.R;
import com.example.campusmap.activity.InfoUpdaterActivity;
import com.example.campusmap.activity.NMTestActivity;
import com.example.campusmap.data.branch.Floor;
import com.example.campusmap.data.branch.Room;
import com.example.campusmap.database.SQLiteHelperCampusInfo;
import com.example.campusmap.form.InfoLocation;

import java.util.ArrayList;

public class RoomListFragment extends Fragment implements AdapterView.OnItemLongClickListener, AdapterView.OnItemClickListener, AlertDialog.OnClickListener {
    private static final String KEY_FLOOR = "floor";
    private static final String TAG = "RoomListFragment";
    private static final boolean DEBUG = false;
    private ListView mListView;
    private ArrayList<Room> mRoomList;
    private int mReservRoomID = -1;
    private Room clicked_room;

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
        View view = inflater.inflate(R.layout.fragment_room_list, container, false);
        mListView = (ListView) view.findViewById(R.id.room_listview);

        if (getArguments() == null) {
            return view;
        }

        Floor floor = (Floor) getArguments().getSerializable(KEY_FLOOR);
        if (floor == null) {
            return view;
        }

        // ## Get DataBase ##
        SQLiteHelperCampusInfo helper = SQLiteHelperCampusInfo.getInstance(getContext());

        ArrayAdapter<String> mAdapter = new ArrayAdapter<>(
                getContext(),
                android.R.layout.simple_list_item_1
        );
        mRoomList = helper.getRoomList(floor.getBuildingId(), floor.getId());
        for (Room room : mRoomList){
            mAdapter.add(room.toString());
        }

        mListView.setAdapter(mAdapter);
        mListView.setOnItemClickListener(this);
        mListView.setOnItemLongClickListener(this);

        // getFloorList => arraylist
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (DEBUG) {
            Log.i(TAG, "onStart: called!!");
            Log.i(TAG, "onStart: reservRoomID " + mReservRoomID);
        }
        if (mReservRoomID != -1) {
            focusRoom(mReservRoomID);
        }
    }

    public void reserveRoom(int roomID) {
        if (mRoomList == null) { // not yet create RoomList
            mReservRoomID = roomID;
            return;
        }
        focusRoom(roomID);
    }

    private void focusRoom(int roomID) {
        for (int roomIndex=0; roomIndex<mRoomList.size(); roomIndex++) {
            if (roomID == mRoomList.get(roomIndex).getId()) {
                if (DEBUG) Log.i(TAG, "focusRoom: clicked_room index : " + roomIndex);
                mListView.setSelection(roomIndex);
                // focus selection
                break;
            }
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        clicked_room = mRoomList.get(position);
        String message = clicked_room.getName() + "에 해당하는 건물로 이동하시겠습니까?";
        new AlertDialog.Builder(getActivity())
                .setTitle("이동하시겠습니까?")
                .setMessage(message)
                .setPositiveButton("예", this)
                .setNegativeButton("취소", null)
                .show();
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        Intent intent;
        switch (which) {
            case DialogInterface.BUTTON_POSITIVE:
                intent = new Intent(getActivity(), NMTestActivity.class);
                intent.putExtra(
                        NMTestActivity.KEY_INFO_LOCATION,
                        new InfoLocation(clicked_room.getName(), InfoLocation.TAG_ROOM, clicked_room.getBuildingId(), clicked_room.getFloorId(), clicked_room.getId())
                );
                startActivity(intent);
                break;
        }
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        Room room = mRoomList.get(position);
        Intent intent = new Intent(getActivity(), InfoUpdaterActivity.class);
        intent.putExtra(
                InfoUpdaterActivity.KEY_INFO_LOCATION,
                new InfoLocation(room.getName(), InfoLocation.TAG_ROOM, room.getBuildingId(), room.getFloorId(), room.getId())
        );
        startActivity(intent);
        return true;
    }
}
