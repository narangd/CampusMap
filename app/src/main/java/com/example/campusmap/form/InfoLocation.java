package com.example.campusmap.form;

import java.io.Serializable;

public class InfoLocation implements Serializable {
    public static final String TAG_BUILDING = "building";
    public static final String TAG_FLOOR = "floor";
    public static final String TAG_ROOM = "room";
    public static final int NONE = -1;

    private final String mName;
    private final String mTag;
    private final int mBuildingID;
    private final int mFloorID;
    private final int mRoomID;

    public InfoLocation(String name, String tag, int buildingID, int floorID, int roomID) {
        mName = name;
        mTag = tag;
        mBuildingID = buildingID;
        mFloorID = floorID;
        mRoomID = roomID;
    }

    public int getRoomID() {
        return mRoomID;
    }

    public int getFloorID() {
        return mFloorID;
    }

    public int getBuildingID() {
        return mBuildingID;
    }

    public String getTag() {
        return mTag;
    }

    public String getName() {
        return mName;
    }

    @Override
    public String toString() {
        return mName;
    }
}
