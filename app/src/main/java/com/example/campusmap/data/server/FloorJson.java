package com.example.campusmap.data.server;

import lombok.Value;

@Value
public class FloorJson {
    int id;
    int number;
    int buildingId;
    RoomJson[] room;
}

