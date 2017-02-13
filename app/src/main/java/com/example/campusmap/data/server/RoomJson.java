package com.example.campusmap.data.server;

import lombok.Value;

@Value
public class RoomJson {
    int id;
    String name;
    String description;
    boolean main;
    int floorId;
}
