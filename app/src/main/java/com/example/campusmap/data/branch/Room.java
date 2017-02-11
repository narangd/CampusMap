package com.example.campusmap.data.branch;

import java.io.Serializable;

import lombok.Value;

@Value
public class Room implements Serializable {
    int id;
    String name;
    String desc;
    int buildingId;
    int floorId;

    @Override
    public String toString() {
        return name + (desc ==null || desc.length()<=0 ? "" : "\n\t"+ desc.trim());
    }
}
