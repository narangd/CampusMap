package com.example.campusmap.data.server;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
public class RoomJson {
    int id;
    String name;
    String description;
    int main;
    @JsonProperty("floor_id")
    int floorId;
}
