package com.example.campusmap.pathfinding;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.util.Log;

import com.example.campusmap.algorithm.AStar;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.TreeSet;

public class Map {
    private static final String TAG = "ADP_Drawing";
    private static final boolean DEBUG = false;

    /** Width Tile Count */
    public static final int XSIZE = 20; // before120
    /** Height Tile Count */
    public static final int YSIZE = 15; // before 90

    /** Tile 2D Array */
    private Tile[][] tiles;
    ArrayList<Polygon> obstacle = new ArrayList<>();

    /** Width Tile Size */
    public int xTileSIZE;
    /** Height Tile Size */
    public int yTileSIZE;

    private Random random = new Random();

    public Tile start;
    public Tile goal;
    Path path = new Path();

    private Drawing.Progress progress;

    // canvas
    public Map(int width, int height) {
        Tile.width = width/XSIZE;
        Tile.height = height/YSIZE;
        xTileSIZE = width/Tile.width;
        if (width%Tile.width > 0)
            this.xTileSIZE++;
        yTileSIZE = height/Tile.height;
        if (height%Tile.height > 0)
            this.yTileSIZE++;

        // add polygon.
//        obstacle.add(new Polygon(this, "POLYGON((1.677 3.277,2.048 3.277,2.048 4.236,1.679 4.236,1.677 3.277))"));
//        obstacle.add(new Polygon(this, "POLYGON((2.288 3.281,2.288 4.069,2.364 4.069,2.364 4.109,2.298 4.191,2.298 4.284,2.397 4.407,2.545 4.407,2.623 4.309,2.649 4.191,2.546 4.063,2.653 4.063,2.653 3.281,2.288 3.281))"));

        // create tiles...
        tiles = new Tile[yTileSIZE][xTileSIZE];
        for(int h = 0; h< yTileSIZE; h++)
        {
            for(int w = 0; w< xTileSIZE; w++)
            {
                tiles[h][w] = new Tile(w*Tile.width, h*Tile.height);
            }
        }

        initRandomToStartGoalTile();
    }

    public void initTiles() {
        for(int h = 0; h< yTileSIZE; h++) {
            for(int w = 0; w< xTileSIZE; w++) {
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
            start = tiles[random.nextInt(yTileSIZE)][random.nextInt(xTileSIZE)];
        } while (start.state == Tile.State.WALL);
        do {
            goal = tiles[random.nextInt(yTileSIZE)][random.nextInt(xTileSIZE)];
        } while (goal.state == Tile.State.WALL);
    }

    public void register(List<Polygon> polygonList) {
        if (DEBUG) Log.i(TAG, "+++ register() called! +++");
        if (DEBUG) Log.i(TAG, "polygonList size : " + polygonList.size());
        for (Polygon polygon : polygonList) {
            obstacle.add(polygon);
        }

        for (int h = 0; h< yTileSIZE; h++) {
            for (int w = 0; w< xTileSIZE; w++) {
                for (Polygon polygon : polygonList) {
                    Rect rect = tiles[h][w].getRect();
                    if ( polygon.contain(new Point(rect.centerX(), rect.centerY())) ) {
                        tiles[h][w].state = Tile.State.WALL;
                    }
                }
            }
        }
        if (DEBUG) Log.i(TAG, "obstacle size : " + polygonList.size());
    }

    public void resetPolygon() {
        obstacle.clear();
    }

    public List<Tile> getNeighborOfTile(Tile tile) {
        List<Tile> neighbor = new ArrayList<>();

        for (int x=-1; x<=1; x++) {
            for (int y=-1; y<=1; y++) {
                int checkX = tile.getX()/Tile.width + x;
                int checkY = tile.getY()/Tile.height + y;
                if (checkX >= 0 && checkX < xTileSIZE && checkY >= 0 && checkY < yTileSIZE) {
                    neighbor.add(tiles[checkY][checkX]);
                }
            }
        }
//        int checkX, checkY;
//
//        checkX = tile.getX()/Tile.width + 1;
//        checkY = tile.getY()/Tile.height;
//        if (checkX >= 0 && checkX < xTileSIZE && checkY >= 0 && checkY < yTileSIZE) {
//            neighbor.add(tiles[checkY][checkX]);
//        }
//
//        checkX = tile.getX()/Tile.width - 1;
//        checkY = tile.getY()/Tile.height;
//        if (checkX >= 0 && checkX < xTileSIZE && checkY >= 0 && checkY < yTileSIZE) {
//            neighbor.add(tiles[checkY][checkX]);
//        }
//
//        checkX = tile.getX()/Tile.width;
//        checkY = tile.getY()/Tile.height + 1;
//        if (checkX >= 0 && checkX < xTileSIZE && checkY >= 0 && checkY < yTileSIZE) {
//            neighbor.add(tiles[checkY][checkX]);
//        }
//
//        checkX = tile.getX()/Tile.width;
//        checkY = tile.getY()/Tile.height - 1;
//        if (checkX >= 0 && checkX < xTileSIZE && checkY >= 0 && checkY < yTileSIZE) {
//            neighbor.add(tiles[checkY][checkX]);
//        }

        return neighbor;
    }

    public int getDistance(Tile one, Tile another) {
        int xDistance = Math.abs(one.getX()-another.getX())/ Tile.width;
        int yDistance = Math.abs(one.getY()-another.getY())/ Tile.height;
//        return 10*Math.abs((one.getX() - another.getX())/Tile.width +
//                ((one.getY() - another.getY())/Tile.height)) ;
        if (xDistance > yDistance)
            return 14*yDistance + 10*(xDistance-yDistance);
        else
            return 14*xDistance + 10*(yDistance-xDistance);
    }

    private void updateFromPath(Tile.State state) {
        if(path == null)
            return;
        for(Point point : path.getPath()) {
            getTile(point.x, point.y).state = state;
        }
    }

    public void pathFinding() {
        updateFromPath(Tile.State.NONE);
//        path.replacePath( findPath(start, goal) );
        path.replacePath(AStar.findPath(this, start, goal));
        updateFromPath(Tile.State.WAY);
        start.state = Tile.State.START;
        goal.state = Tile.State.GOAL;
    }

    public void drawTiles(Canvas canvas, Paint paint) {
        for (Tile[] tiles : this.tiles) {
            for (Tile tile : tiles) {
                tile.draw(canvas, paint);
            }
        }
    }

    public LinkedList<Point> findPath(Tile start, Tile goal) {
        if(start == null || goal == null)
            return null;

        HashSet<Tile> closedSet = new HashSet<>();
        TreeSet<Tile> openSet = new TreeSet<>(new Tile.TileSorter());
//        ArrayList<Tile> openSet = new ArrayList<>();

        HashMap<Tile, Tile> cameFrom = new HashMap<>();

        openSet.add(start);
//        start.state = Tile.State.OPEN;

//        goal.parent = null;
//        start.G = 0;
//        start.H = map.getDistance(start, goal);
//        start.F = start.G + start.H;

        int count = 10;

        while( openSet.size() > 0)//size() > 0 )
        {
//            Collections.sort(openSet);
            Tile current = openSet.pollFirst();
                    //openSet.get(0); // error this .... compare...
            if (current == goal)
                break;

            closedSet.add(current);
            openSet.remove(current);

            for ( Tile neighbor : getNeighborOfTile(current) ) {
                if (closedSet.contains(neighbor) || neighbor.state == Tile.State.WALL)
                    continue;       // Ignore the neighbor which is already evaluated.

                // The distance from start to a neighbor
                int tentative_gScore = current.G + current.getDistance(neighbor);

                if (!openSet.contains(neighbor)) {
                    openSet.add(neighbor); // Discover a new node
                }
                else if (tentative_gScore >= neighbor.G)
                    continue;       // This is not a better path.

                // This path is the best until now. Record it!
                cameFrom.put(neighbor, current);
                neighbor.parent = current;
                neighbor.G = tentative_gScore;
                neighbor.H = getDistance(goal, neighbor);
                neighbor.F = neighbor.G + neighbor.H; // F = G + H.
            }
            if (--count <= 0)
                break;
        }

        openSet.clear();
        closedSet.clear();

        return reconstructPath(cameFrom, goal);
    }

    private LinkedList<Point> reconstructPath(HashMap<Tile,Tile> cameFrom, Tile current) {
        LinkedList<Point> path = new LinkedList<>();
        while (cameFrom.containsKey(current)) {
            current = cameFrom.get(current);
            path.add(current.getPoint());
        }
        cameFrom.clear();
        return path;
    }

    public void setOnProgressUpdate(Drawing.Progress progress) {
        this.progress = progress;
    }

    public Tile getTile(int x, int y)
    {
        if(x<0 || x>= xTileSIZE*Tile.width || y<0 || y>= yTileSIZE*Tile.height)
            return null;
        return tiles[y/Tile.height][x/Tile.width];
    }

    public int getxTileSIZE() {
        return xTileSIZE;
    }
    public int getyTileSIZE() {
        return yTileSIZE;
    }

    public Tile[][] getTiles() {
        return tiles;
    }

}
