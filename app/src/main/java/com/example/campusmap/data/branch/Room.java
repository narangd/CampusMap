package com.example.campusmap.data.branch;

import java.io.Serializable;

public class Room implements Serializable {
    private int id;
    private String name;
    private String desc;
    private int buildingID;
    private int floorID;

    public Room(int id, String name, String desc, int buildingID, int floorID) {
        this.id = id;
        this.name = name;
        this.desc = desc;
        this.buildingID = buildingID;
        this.floorID = floorID;
    }

    public int getID() { return id; }

    public String getName() {
        return name;
    }

    public String getText() {
        return desc;
    }

    public int getBuildingID() { return buildingID; }

    public int getFloorID() { return floorID; }

    @Override
    public String toString() {
        return name + (desc ==null || desc.length()<=0 ? "" : "\n\t"+ desc.trim());
    }
}
