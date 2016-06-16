package com.example.campusmap.pathfinding.graphic;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.util.Log;

import com.example.campusmap.pathfinding.algorithm.AStar;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Map {
    private static final String TAG = "ADP_Drawing";
    private static final boolean DEBUG = true;

    /**
     * Width Tile Count
     */
    public static final int XSIZE = 120;
    /**
     * Height Tile Count
      */
    public static final int YSIZE = 90;

    /**
     * Tile 2D Array
     */
    Tile[][] tiles;
    ArrayList<Polygon> obstacle = new ArrayList<>();

    /**
     * Width Tile Size
     */
    public int xTileSIZE;
    /**
     * Height Tile Size
     */
    public int yTileSIZE;

    private Random random = new Random();

    public Tile start;
    public Tile goal;
    Path path = new Path();

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
        List<Tile> neighbor = new ArrayList<Tile>();

        for (int x=-1; x<=1; x++) {
            for (int y=-1; y<=1; y++) {
                int checkX = tile.getX()/Tile.width + x;
                int checkY = tile.getY()/Tile.height + y;
                if (checkX >= 0 && checkX < xTileSIZE && checkY >= 0 && checkY < yTileSIZE) {
                    neighbor.add(tiles[checkY][checkX]);
                }
            }
        }

        return neighbor;
    }

    public int getDistance(Tile one, Tile another) {
        return Math.abs(one.getX() - another.getX()) * Tile.width
                + Math.abs(one.getY() - another.getY()) * Tile.height;
    }

    private void updateFromPath(Tile.State state) {
        if(path == null)
            return;
        for(Point point : path.getPath()) {
            getTile(point.x, point.y).state = state;
        }
    }

    public void pathFinding() {
        initTiles();
        updateFromPath(Tile.State.NONE);
        path.replacePath( AStar.findPath(this, start, goal) );
        updateFromPath(Tile.State.WAY);
    }

    public void drawTiles(Canvas canvas, Paint paint) {
        for (Tile[] tiles : this.tiles)
        {
            for (Tile tile : tiles)
            {
//                switch(tile.getState())
//                {
//                    case WALL:
////                        if (isWallVisible)
//                            //canvas.setColor(Color.YELLOW);
//                        paint.setColor(Color.YELLOW);
////                        else
////                            continue;
//                        break;
//                    case START:
//                        paint.setColor(Color.GREEN);
//                        break;
//                    case GOAL:
//                        paint.setColor(Color.RED);
//                        break;
//                    case WAY:
//                        paint.setColor(Color.BLUE);
//                        break;
//                    default:
//                        continue;
//                }
                tile.draw(canvas, paint);
            }
        }
    }

    public Tile getTile(int x, int y)
    {
        if(x<0 || x>= xTileSIZE *Tile.width || y<0 || y>= yTileSIZE *Tile.height)
            return null;
        return tiles[y/Tile.height][x/Tile.width];
    }
    public Tile getTile(Point point)
    {
        return getTile(point.x, point.y);
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
