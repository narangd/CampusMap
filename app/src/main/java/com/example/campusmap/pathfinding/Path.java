package com.example.campusmap.pathfinding;

import android.graphics.Point;

import java.util.LinkedList;

/**
 * Created by 연구생 on 2015-11-10.
 */
public class Path {

    private LinkedList<Point> path = new LinkedList<>();

    public Path() {}

    public void replacePath(LinkedList<Point> points)
    {
        if(points == null)
            return;
        path.clear();
        path.addAll(points);
    }

    public LinkedList<Point> getPath()
    { return this.path; }
}
