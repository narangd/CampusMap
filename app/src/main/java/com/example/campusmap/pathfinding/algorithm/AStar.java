package com.example.campusmap.pathfinding.algorithm;

import android.graphics.Point;

import com.example.campusmap.pathfinding.graphic.Map;
import com.example.campusmap.pathfinding.graphic.Tile;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.TreeSet;

/**
 * Created by 연구생 on 2015-11-10.
 */
public class AStar {
    public static LinkedList<Point> pathfinding(Map map, Tile start, Tile goal) {
        if(start == null || goal == null)
            return null;
        TreeSet<Tile> openset = new TreeSet<>();//<Tile>(); //
        HashSet<Tile> closedset = new HashSet<>();
        Tile current;
        int tentative_g_score;
        int count = 0;

        goal.parent = null;
        openset.add(start);
        start.G = 0;
        start.H = start.getDistance(goal);
        start.F = start.G + start.H;

        // 오픈 셋 검사. search OpenSet.
        while( openset.size() > 0)//size() > 0 )
        {
            current =  openset.pollFirst(); //pollFirst(); // lowest
            if (current == goal)
                break;
            openset.remove(current);
            closedset.add(current);
            // Add neighbor in OpenSet
            for ( Tile neighbor : map.neightbor_Tiles(current) )
            {
                // neighbor이 closedset안에 있거나, 벽이면 넘어감
                if (closedset.contains(neighbor) || neighbor.state == Tile.State.WALL)
                    continue;

                // neighbor과의 거리를 더한다.
                tentative_g_score = current.G + current.getDistance(neighbor);

                // neighbor가 이미 계산되어있지 않거나, 거리가 더작으면
                if (!openset.contains(neighbor) || tentative_g_score < neighbor.G) {
                    neighbor.parent = current;
                    neighbor.G = tentative_g_score;
                    neighbor.F = neighbor.G + map.getDistance(goal, neighbor);
                    // openset에 포함되지 않았다면
                    if (!openset.contains(neighbor)) {
                        openset.add(neighbor);
                    }
                }
            }
        }
        openset.clear();
        closedset.clear();
        return getPath(goal);
    }

    private static LinkedList<Point> getPath(Tile goal) {
        LinkedList<Point> path = new LinkedList<>();
        Tile way = goal;
        while (way.parent != null) {
//            Log.d("Way", way.getPoint().toString());
            path.add(way.parent.getPoint()); // 여기가 문제...
            way = way.parent;
        }
//        Log.d("Way Complete", "----------------------------------------------");
        return path;
    }
}
