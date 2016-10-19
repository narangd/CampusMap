package com.example.campusmap.algorithm;

import android.graphics.Point;

import com.example.campusmap.pathfinding.Map;
import com.example.campusmap.pathfinding.Tile;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;

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
    public static LinkedList<Tile> findPath(Map map, Tile start, Tile goal) {
        if(start == null || goal == null)
            return null;

        HashSet<Tile> closedSet = new HashSet<>();
        MyArrayList<Tile> openSet = new MyArrayList<>();

        HashMap<Tile, Tile> cameFrom = new HashMap<>();

        openSet.add(start);

        int count = 10;

        while( openSet.size() > 0)//size() > 0 )
        {
//            Collections.sort(openSet);
            Tile current = openSet.pullLowest();

            if (current == goal)
                break;

            closedSet.add(current);

            for ( Tile neighbor : map.getNeighborOfTile(current) ) {
                boolean isNewNode = false;
                if (closedSet.contains(neighbor) || neighbor.state == Tile.State.WALL)
                    continue;       // Ignore the neighbor which is already evaluated.

                // The distance from start to a neighbor
                int tentative_gScore = current.G + current.getDistance(neighbor);

                if (!openSet.contains(neighbor)) {
                    isNewNode = true;
                }
                else if (tentative_gScore >= neighbor.G)
                    continue;       // This is not a better path.

                // This path is the best until now. Record it!
//                neighbor.parent = current;
                neighbor.G = tentative_gScore;
                neighbor.H = map.getDistance(goal, neighbor);
                neighbor.F = neighbor.G + neighbor.H; // F = G + H.
                cameFrom.put(neighbor, current);

                // do not change F value!!
                if (isNewNode) {
                    openSet.add(neighbor); // Discover a new node
                }
            }

//            if (--count <= 0)
//                break;
        }

        openSet.clear();
        closedSet.clear();

        return reconstructPath(cameFrom, goal);
    }

    private static LinkedList<Tile> reconstructPath(HashMap<Tile,Tile> cameFrom, Tile current) {
        LinkedList<Tile> path = new LinkedList<>();
        path.add(current);
        while (cameFrom.containsKey(current)) {
            current = cameFrom.get(current);
            path.add(current);
        }
        cameFrom.clear();
        return path;
    }

    interface Serchable {
        Point getLocation();
    }
}
