package com.example.campusmap.data.server;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class FloorJson {
    int id;
    int number;
    @JsonProperty("building_id")
    int buildingId;
    RoomJson[] room;
}

