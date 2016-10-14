package com.example.campusmap.form;

public class PointD {
    public double x;
    public double y;

    public PointD() {
    }

    public PointD(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public PointD(PointD other) {
        x = other.x;
        y = other.y;
    }

    @Override
    public String toString() {
        return hashCode() + " {x:" + x + ", y:" + y + "}";
    }
}
