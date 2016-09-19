package com.example.campusmap.database;

import java.io.Serializable;

public class InfoLocation implements Serializable {
    public static final String TAG_BUILDING = "building";
    public static final String TAG_FLOOR = "floor";
    public static final String TAG_ROOM = "room";
    public static final int NONE = -1;

    private final String mName;
    public final String mTag;
    public final int mBuildingID;
    public final int mFloorID;
    public final int mRoomID;

    public InfoLocation(String name, String tag, int buildingID, int floorID, int roomID) {
        mName = name;
        mTag = tag;
        mBuildingID = buildingID;
        mFloorID = floorID;
        mRoomID = roomID;
    }

    @Override
    public String toString() {
        return mName;
    }
}
