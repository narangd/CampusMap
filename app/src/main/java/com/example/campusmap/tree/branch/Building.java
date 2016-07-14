package com.example.campusmap.tree.branch;

import java.io.Serializable;

public class Building implements Serializable {
    private int mID;
    private String mName;

    public int getID() {
        return mID;
    }

    public String getName() {
        return mName;
    }

    public Building(int id, String name) {
        mID = id;
        mName = name;
    }

    @Override
    public String toString() {
        return mID + "-" + mName;
    }
}
