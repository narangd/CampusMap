package com.example.campusmap.tree.branch;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Objects;

public class University extends LinkedList<Building>{
    int clickedBuildingIndex;
    public University() {
    }

    public void addBuilding(Building building) {
        add(building);
    }

    public void concatFloor(Floor floor) {
        getLast().add(floor);
    }

    public void concatRoom(Room room) {
        getLast().getLast().add(room);
    }

    public String[] toStringArray() {
        String[] strings = new String[size()];
        for (int i=0; i<size(); i++) {
            strings[i] = get(i).toString();
        }
        return strings;
    }

}
