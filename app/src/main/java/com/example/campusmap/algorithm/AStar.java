package com.example.campusmap.algorithm;

import android.graphics.Point;
import android.util.Log;

import com.example.campusmap.pathfinding.Map;
import com.example.campusmap.pathfinding.Tile;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.TreeMap;

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
//        SortedList<Tile> openSet = new SortedList<>();
//        ArrayList<Tile> openSet = new ArrayList<>();
        TreeMap<Tile,Tile> openMap = new TreeMap<>();

        HashMap<Tile, Tile> cameFrom = new HashMap<>();

//        openSet.insert(start);
        openMap.put(start, start);

        int count = 10;
        Log.i("AStar", "findPath: openmap : " + openMap);

        while( openMap.size() > 0)//size() > 0 )
        {
//            Collections.sort(openSet);
//            Tile current = openSet.pollFirst();
            Tile current = openMap.pollFirstEntry().getValue();
            //openSet.get(0); // error this .... compare...
            if (current == goal)
                break;

            closedSet.add(current);
//            openSet.remove(current);

            for ( Tile neighbor : map.getNeighborOfTile(current) ) {
                if (closedSet.contains(neighbor) || neighbor.state == Tile.State.WALL)
                    continue;       // Ignore the neighbor which is already evaluated.

                // The distance from start to a neighbor
                int tentative_gScore = current.G + current.getDistance(neighbor);

                if (!openMap.containsValue(neighbor)) {
                    openMap.put(neighbor, neighbor); // Discover a new node
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
            Log.i("AStar", "findPath: openmap : " + openMap);

            if (--count <= 0)
                break;
        }

        openMap.clear();
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
