package com.example.campusmap.algorithm;

import android.graphics.Point;
import android.util.Log;

import com.example.campusmap.pathfinding.Map;
import com.example.campusmap.pathfinding.Tile;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.TreeSet;

/**
 * Created by 연구생 on 2015-11-10.
 */
public class AStar {
    /**
     * AStart Path Finding Algorithm
     * @param map in Tiles
     * @param start start Tile in Tiles
     * @param goal goal Tile in Tiles
     * @return List Path
     */
    public static LinkedList<Point> findPath(Map map, Tile start, Tile goal) {
        if(start == null || goal == null)
            return null;

        HashSet<Tile> closedSet = new HashSet<>();
        TreeSet<Tile> openSet = new TreeSet<>();

        HashMap<Tile, Tile> cameFrom = new HashMap<>();

        openSet.add(start);
//        start.state = Tile.State.OPEN;

//        goal.parent = null;
//        start.G = 0;
//        start.H = map.getDistance(start, goal);
//        start.F = start.G + start.H;

        while( openSet.size() > 0)//size() > 0 )
        {
            Log.d("AStar", "openSet List : " + openSet.toString());
            Tile current =  openSet.pollFirst(); // error this .... compare...
            if (current == goal)
                break;

            closedSet.add(current);


            for ( Tile neighbor : map.getNeighborOfTile(current) )
            {
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
                neighbor.H = map.getDistance(goal, neighbor);
                neighbor.F = neighbor.G + neighbor.H; // F = G + H.
            }
        }

        openSet.clear();
        closedSet.clear();

        return reconstructPath(cameFrom, goal);
    }

    private static LinkedList<Point> reconstructPath(HashMap<Tile,Tile> cameFrom, Tile current) {
        LinkedList<Point> path = new LinkedList<>();
        while (cameFrom.containsKey(current)) {
            current = cameFrom.get(current);
            path.add(current.getPoint());
        }cameFrom.clear();
        return path;
    }
}
