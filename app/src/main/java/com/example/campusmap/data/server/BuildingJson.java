package com.example.campusmap.data.server;

import lombok.Value;

@Value
public class BuildingJson {
    int id;
    int number;
    String name;
    String description;
    FloorJson[] floor;
}
