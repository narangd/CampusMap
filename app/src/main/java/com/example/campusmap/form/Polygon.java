package com.example.campusmap.form;

import java.util.ArrayList;

public class Polygon {
    private int number;
    private ArrayList<PointD> points = new ArrayList<>();

    public Polygon(int number) {
        this.number = number;
    }

    public void addPoint(double x, double y) {
        points.add(new PointD(x, y));
    }

    public int getNumber() {
        return number;
    }

    public ArrayList<PointD> getPoints() {
        return points;
    }

    @Override
    public String toString() {
        return hashCode() + " {number:" + number + ", size:" + points.size() + "}";
    }
}
