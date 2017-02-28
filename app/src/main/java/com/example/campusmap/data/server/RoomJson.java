package com.example.campusmap.data.server;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class RoomJson {
    int id;
    String name;
    String description;
    int main;
    @JsonProperty("floor_id")
    int floorId;
}
