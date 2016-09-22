package com.example.campusmap.data.branch;

import java.io.Serializable;

public class Floor implements Serializable {
    private int mID;
    private final int mFloor;
    private int mBuildingID;

    public Floor(int id, int floor, int buildingID) {
        mID = id;
        mFloor = floor;
        mBuildingID = buildingID;
    }

    public int getID() {
        return mID;
    }

    public int getFloor() {
        return mFloor;
    }

    public int getBuildingID() {
        return mBuildingID;
    }

    @Override
    public String toString() {
        return mFloor + "층";
    }
}
