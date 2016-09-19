package com.example.campusmap.tree.branch;

import java.io.Serializable;

public class Building implements Serializable {
    private final String mDescription;
    private final int mID;
    private final int mNumber;
    private final String mName;

    public int getID() {
        return mID;
    }

    public int getNumber() {
        return mNumber;
    }

    public String getName() {
        return mName;
    }

    public String getDescription() {
        return mDescription;
    }

    public Building(int id, int number, String name, String description) {
        mID = id;
        mNumber = number;
        mName = name;
        mDescription = description;
    }

    @Override
    public String toString() {
        return mNumber + "-" + mName;
    }
}
