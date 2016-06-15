package com.example.campusmap.pathfinding.graphic;

import android.graphics.Point;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by 연구생 on 2015-11-09.
 */
public class Polygon {
    ArrayList<Point> points = new ArrayList<>();

    public Polygon(Point ... points) {
        for (Point point : points) {
            this.points.add(point);
        }
    }

    /**
     * from DataBase Polygon...
     * ex) "13.121 24.132,15.435 24.132,15.435 30.932,13.129 30.932,13.121 24.132"
     * @param format
     */
    public Polygon(Map map, String format) {
//        Log.d("Polygon", format); // good.!
        String[] points = format.split("\\,");
        for(String point : points) {
            String[] xy = point.split(" ");
            float x = (Float.parseFloat(xy[0])/70) * (map.getxSIZE() * Tile.width);
            float y = (Float.parseFloat(xy[1])/70) * (map.getySIZE() * Tile.height) ;
            Point p = new Point((int)x, (int)y);
            this.points.add(p);
            //Log.d("Polygon", p.toString());
        }
    }

    public boolean contain(Point point) {
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
        return Arrays.toString(points.toArray());
    }
}
