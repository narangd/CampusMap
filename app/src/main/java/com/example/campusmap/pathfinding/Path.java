package com.example.campusmap.pathfinding;

import android.graphics.Point;

import com.example.campusmap.form.PointD;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class Path {

    private LinkedList<Tile> path = new LinkedList<>();

    public Path() {}

    public void replacePath(LinkedList<Tile> tiles)
    {
        if(tiles == null)
            return;
        path.clear();
        path.addAll(tiles);
    }

    public List<PointD> simplify() {
        ArrayList<PointD> way_point = new ArrayList<>();
        Point prev_direction = new Point();

        if (path.size() > 0) {
            way_point.add(path.get(0).getPoint());
        } else {
            return way_point;
        }
        
        for (int i=1; i<path.size(); i++) {
            Point direction = new Point(
                    path.get(i).getX() - path.get(i-1).getX(),
                    path.get(i).getY() - path.get(i-1).getY()
            );

            if (prev_direction.x != direction.x ||
                    prev_direction.y != direction.x) {
                way_point.add(path.get(i).getPoint());
            }
            prev_direction = direction;
        }
        if (!way_point.contains(path.getLast().getPoint())) {
            way_point.add(path.getLast().getPoint());
        }
        return way_point;
    }

    public LinkedList<Tile> getPath()
    { return this.path; }

    public int size() {
        return path.size();
    }
}
