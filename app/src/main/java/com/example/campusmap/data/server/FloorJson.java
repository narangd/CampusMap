package com.example.campusmap.data.server;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
public class FloorJson {
    int id;
    int number;
    @JsonProperty("building_id")
    int buildingId;
    RoomJson[] room;
}

