package com.example.campusmap.tree.branch;

import java.io.Serializable;

/**
 * Created by 연구생 on 2015-11-23.
 */
public interface Parent extends Comparable<Parent>, Serializable{
    String toString();
    Parent getParent();
}
