package com.example.campusmap.data.server;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
public class BuildingJson {
    int id;
    int number;
    String name;
    String description;
    FloorJson[] floor;
}
