package com.example.campusmap.data.server;

import lombok.Data;

@Data
public class BuildingJson {
    int id;
    int number;
    String name;
    String description;
    FloorJson[] floor;
}
