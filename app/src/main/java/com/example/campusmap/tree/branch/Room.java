package com.example.campusmap.tree.branch;

import android.support.annotation.NonNull;

import java.util.Iterator;

public class Room implements Parent {
    private String name;
    private String text;
    private Parent parent;

    public Room(String name, String text, Parent parent) {
        this.name = name;
        this.text = text;
        this.parent = parent;
    }

    public String getName() {
        return name;
    }

    public String getText() { return text; }

    @Override
    public String toString() {
        return String.format("%s", name) + (text==null ? "" : "\n\t"+text.trim());
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
