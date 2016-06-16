package com.example.campusmap.pathfinding.algorithm;

import android.graphics.Point;

import com.example.campusmap.pathfinding.graphic.Map;
import com.example.campusmap.pathfinding.graphic.Tile;

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

//        goal.parent = null;
        start.G = 0;
        start.H = start.getDistance(goal);
        start.F = start.G + start.H;

        while( openSet.size() > 0)//size() > 0 )
        {
            Tile current =  openSet.pollFirst(); //pollFirst(); // lowest
            if (current == goal)
                break;

            openSet.remove(current);
            closedSet.add(current);

            for ( Tile neighbor : map.getNeighborOfTile(current) )
            {
                if (closedSet.contains(neighbor) || neighbor.state == Tile.State.WALL)
                    continue;       // Ignore the neighbor which is already evaluated.

                // The distance from start to a neighbor
                int tentative_gScore = current.G + current.getDistance(neighbor);

                if (!openSet.contains(neighbor))
                    openSet.add(neighbor); // Discover a new node
                else if (tentative_gScore >= neighbor.G)
                    continue;       // This is not a better path.

                // This path is the best until now. Record it!
                cameFrom.put(neighbor, current);
//                neighbor.parent = current;
                neighbor.G = tentative_gScore;
                neighbor.F = neighbor.G + map.getDistance(goal, neighbor);
            }
        }

        openSet.clear();
        closedSet.clear();

        return reconstructPath(cameFrom, goal);
    }

    private static LinkedList<Point> reconstructPath(HashMap<Tile,Tile> cameFrom, Tile current) {
        LinkedList<Point> path = new LinkedList<>();
//        while (way.parent != null) {
        while (cameFrom.containsKey(current)) {
            current = cameFrom.get(current);
            path.add(current.getPoint());
//            path.add(current.parent.getPoint()); // 여기가 문제...
//            current = current.parent;
        }
//        Log.d("Way Complete", "----------------------------------------------");
        return path;
    }
}
