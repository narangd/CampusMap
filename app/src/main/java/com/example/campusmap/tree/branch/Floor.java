package com.example.campusmap.tree.branch;

import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.LinkedList;

public class Floor extends LinkedList<Room> implements Parent{
    private int num;
    private Parent parent;

    public Floor(int num, Parent parent) {
        this.num = num;
        this.parent = parent;
    }

    public int getNum() {
        return num;
    }

    @Override
    public String toString() {
        return String.format("%2d층", num);
    }

    @Override
    public Parent getParent() {
        return parent;
    }
    @Override
    public int compareTo(@NonNull Parent another) {
        return toString().compareTo(another.toString());
    }
}
