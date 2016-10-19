package com.example.campusmap.pathfinding;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.Log;

import com.example.campusmap.algorithm.AStar;
import com.example.campusmap.form.PointD;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Map {
    private static final String TAG = "ADP_Drawing";
    private static final boolean DEBUG = false;
    public final double rect_size = 0.00005;

    private final int xTileCount;
    private final int yTileCount;

    private Tile[][] tiles;
    ArrayList<Polygon> obstacle = new ArrayList<>();

    private Random random = new Random();
    private Path path = new Path();
    private PointD min;
    private PointD max;

    public Tile start;
    public Tile goal;

    public Map(PointD min, PointD max) {
        xTileCount = (int)((max.x - min.x)/rect_size)+1;
        yTileCount = (int)((max.y - min.y)/rect_size)+1;

        this.min = min;
        this.max = max;

        tiles = new Tile[yTileCount][xTileCount];

        int xIndex, yIndex=0;

        for(double y=min.y; y<=max.y; y+=rect_size,yIndex++)
        {
            xIndex = 0;
            for(double x = min.x; x<max.x; x+=rect_size,xIndex++)
            {
                tiles[yIndex][xIndex] = new Tile(x, y, xIndex, yIndex);
            }
        }

        initRandomToStartGoalTile();
    }

    public void initTiles() {
        for(int h = 0; h< yTileCount; h++) {
            for(int w = 0; w< xTileCount; w++) {
                tiles[h][w].init();
            }
        }
    }

    public void initRandomToStartGoalTile() {
        if (start != null)
            start.state = Tile.State.NONE;
        if (goal != null)
            goal.state = Tile.State.NONE;

        do {
            start = tiles[random.nextInt(yTileCount)][random.nextInt(xTileCount)];
        } while (start.state == Tile.State.WALL);
        do {
            goal = tiles[random.nextInt(yTileCount)][random.nextInt(xTileCount)];
        } while (goal.state == Tile.State.WALL);
    }

    public void register(List<Polygon> polygonList) {
        if (DEBUG) Log.i(TAG, "+++ register() called! +++");
        if (DEBUG) Log.i(TAG, "polygonList size : " + polygonList.size());
        for (Polygon polygon : polygonList) {
            obstacle.add(polygon);
        }

//        for (int h = 0; h< yTileCount; h++) {
//            for (int w = 0; w< xTileCount; w++) {
//                for (Polygon polygon : polygonList) {
//                    Rect rect = tiles[h][w].getRect();
//                    if ( polygon.contain(new Point(rect.centerX(), rect.centerY())) ) {
//                        tiles[h][w].state = Tile.State.WALL;
//                    }
//                }
//            }
//        }
        if (DEBUG) Log.i(TAG, "obstacle size : " + polygonList.size());
    }

    public void resetPolygon() {
        obstacle.clear();
    }

    public List<Tile> getNeighborOfTile(Tile tile) {
        List<Tile> neighbor = new ArrayList<>();

        for (int x=-1; x<=1; x++) {
            for (int y=-1; y<=1; y++) {
                int checkX = tile.getX() + x;
                int checkY = tile.getY() + y;
                if (checkX >= 0 && checkX < xTileCount && checkY >= 0 && checkY < yTileCount) {
                    neighbor.add(tiles[checkY][checkX]);
                }
            }
        }
//        int checkX, checkY;
//
//        checkX = tile.getX()/Tile.width + 1;
//        checkY = tile.getY()/Tile.height;
//        if (checkX >= 0 && checkX < xTileCount && checkY >= 0 && checkY < yTileCount) {
//            neighbor.add(tiles[checkY][checkX]);
//        }
//
//        checkX = tile.getX()/Tile.width - 1;
//        checkY = tile.getY()/Tile.height;
//        if (checkX >= 0 && checkX < xTileCount && checkY >= 0 && checkY < yTileCount) {
//            neighbor.add(tiles[checkY][checkX]);
//        }
//
//        checkX = tile.getX()/Tile.width;
//        checkY = tile.getY()/Tile.height + 1;
//        if (checkX >= 0 && checkX < xTileCount && checkY >= 0 && checkY < yTileCount) {
//            neighbor.add(tiles[checkY][checkX]);
//        }
//
//        checkX = tile.getX()/Tile.width;
//        checkY = tile.getY()/Tile.height - 1;
//        if (checkX >= 0 && checkX < xTileCount && checkY >= 0 && checkY < yTileCount) {
//            neighbor.add(tiles[checkY][checkX]);
//        }

        return neighbor;
    }

    public int getDistance(Tile one, Tile another) {
        int xDistance = Math.abs(one.getX() - another.getX());
        int yDistance = Math.abs(one.getY()-another.getY());
//        return 10*Math.abs((one.getX() - another.getX())/Tile.width +
//                ((one.getY() - another.getY())/Tile.height)) ;
        if (xDistance > yDistance)
            return 14*yDistance + 10*(xDistance-yDistance);
        else
            return 14*xDistance + 10*(yDistance-xDistance);
    }

    private void updateFromPath(Tile.State state) {
//        if(path == null)
//            return;
////        for(Point point : path.getPath()) {
////            getTile(point.x, point.y).state = state;
////        }
    }

    public List<PointD> pathFinding() {
//        updateFromPath(Tile.State.NONE);
//        path.replacePath( findPath(start, goal) );
        path.replacePath(AStar.findPath(this, start, goal));
//        updateFromPath(Tile.State.WAY);
        start.state = Tile.State.START;
        goal.state = Tile.State.GOAL;
        return path.simplify();
    }

    public void drawTiles(Canvas canvas, Paint paint) {
//        for (Tile[] tiles : this.tiles) {
//            for (Tile tile : tiles) {
//                tile.draw(canvas, paint);
//            }
//        }
    }

//    public void drawPath(Canvas canvas, Paint paint) {
//        if (path == null || path.size() <= 0) {
//            return;
//        }
//
//        paint.setColor(Color.CYAN);
//        paint.setStyle(Paint.Style.STROKE);
//        paint.setStrokeWidth(10);
//
//        android.graphics.Path path = new android.graphics.Path();
//
//        Point prevPoint = this.path.getPath().pollFirst();
//        path.moveTo(prevPoint.x, prevPoint.y);
//        for (Point point : this.path.getPath()) {
//            path.lineTo(point.x, point.y);
//        }
//        canvas.drawPath(path, paint);
//    }

    public Tile getTile(double x, double y) {
        double delta_x = x - min.x;
        double delta_y = y - min.y;
        if (delta_x < 0 || delta_y < 0 || delta_x > max.x-min.x || delta_y > max.y-min.y) {
            return null;
        }
        return tiles[(int)(delta_y/rect_size)][(int)(delta_x/rect_size)];
    }
    public Tile getTile(int x, int y) {
        return tiles[y][x];
    }

    public int getXTileCount() {
        return xTileCount;
    }
    public int getYTileCount() {
        return yTileCount;
    }

}
