package com.example.campusmap.tree.branch;

import android.graphics.Rect;

/**
 * Created by 성용 on 2016-07-13.
 */
public class BuildingLocation {
    private Rect mRect;
    private int mID;

    public BuildingLocation(int ID, Rect rect) {
        mID = ID;
        mRect = rect;
    }

    public int getID() {
        return mID;
    }

    public Rect getRect() {
        return mRect;
    }

    public boolean contains(int x, int y) {
        return mRect.contains(x, y);
    }
}
