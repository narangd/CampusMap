package com.example.campusmap.xmlparser.search;

import java.io.Serializable;

/**
 * Created by rla on 2015-10-29.
 */
public class Pair implements Serializable, Comparable<Pair> {
    String main, sub;

    public Pair(String main, String sub) {
        this.main = main;
        this.sub = sub;
    }

    public String getMain() {
        return main;
    }

    public String getSub() {
        return sub;
    }

    @Override
    public int compareTo(Pair another) {
        return main.compareTo(another.main);
    }
}