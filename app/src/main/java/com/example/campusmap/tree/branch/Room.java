package com.example.campusmap.tree.branch;

import java.io.Serializable;

public class Room implements Serializable {
    private int mID;
    private String mName;
    private String mText;
    private int mBuildingID;
    private int mFloorID;

    public Room(int id, String name, String text, int buildingID, int floorID) {
        mID = id;
        mName = name;
        mText = text;
        mBuildingID = buildingID;
        mFloorID = floorID;
    }

    public int getID() { return mID; }

    public String getName() {
        return mName;
    }

    public String getText() {
        return mText;
    }

    public int getBuildingID() { return mBuildingID; }

    public int getFloorID() { return mFloorID; }

    @Override
    public String toString() {
        return mName + (mText==null ? "" : "\n\t"+ mText.trim());
    }
}
