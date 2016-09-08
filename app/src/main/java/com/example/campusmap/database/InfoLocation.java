package com.example.campusmap.database;

import java.io.Serializable;

public class InfoLocation implements Serializable {
    public static final int NONE = -1;

    private final String mName;
    public final int mBuildingID;
    public final int mFloorID;
    public final int mRoomID;

    public InfoLocation(String name, int buildingID, int floorID, int roomID) {
        mName = name;
        mBuildingID = buildingID;
        mFloorID = floorID;
        mRoomID = roomID;
    }

    @Override
    public String toString() {
        return mName;
    }
}
