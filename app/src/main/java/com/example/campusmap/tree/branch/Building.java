package com.example.campusmap.tree.branch;

import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.LinkedList;

public class Building extends LinkedList<Floor> implements Parent {
    private int num;
    private String name;

    public int getNum() {
        return num;
    }

    public String getName() {
        return name;
    }

    public Building(int num, String name) {
        this.num = num;
        this.name = name;
    }

    @Override
    public String toString() {
        return String.format("%s(%d)", name, num);
    }

    @Override
    public Parent getParent() {
        return null;
    }

    @Override
    public int compareTo(@NonNull Parent another) {
        return toString().compareTo(another.toString());
    }
}
