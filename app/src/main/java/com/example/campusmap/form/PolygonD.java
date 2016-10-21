package com.example.campusmap.form;

import java.util.ArrayList;

public class PolygonD {
    private int number;
    private ArrayList<PointD> points = new ArrayList<>();

    public PolygonD(int number) {
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

    public boolean contain(PointD point) {
        int i, j;
        boolean c = false;
        for (i = 0, j = points.size() - 1; i < points.size(); j = i++) {
            if (((points.get(i).y > point.y) != (points.get(j).y > point.y)) &&
                    (point.x < (points.get(j).x - points.get(i).x) * (point.y - points.get(i).y) / (points.get(j).y - points.get(i).y) + points.get(i).x))
                c = !c;
        }
        return c;
    }

    @Override
    public String toString() {
        return hashCode() + " {number:" + number + ", size:" + points.size() + "}";
    }
}
