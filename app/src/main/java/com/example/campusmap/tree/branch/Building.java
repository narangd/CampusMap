package com.example.campusmap.tree.branch;

import java.io.Serializable;

public class Building implements Serializable {
    private int mID;
    private int mNumber;
    private String mName;

    public int getID() {
        return mID;
    }

    public int getNumber() {
        return mNumber;
    }

    public String getName() {
        return mName;
    }

    public Building(int id, int number, String name) {
        mID = id;
        mNumber = number;
        mName = name;
    }

    @Override
    public String toString() {
        return mNumber + "-" + mName;
    }
}
