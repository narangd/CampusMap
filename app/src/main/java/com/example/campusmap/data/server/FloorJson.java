package com.example.campusmap.data.server;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Arrays;

import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
public class FloorJson {
    int id;
    int number;
    @JsonProperty("building_id")
    int buildingId;
    RoomJson[] room = new RoomJson[0];

    @Override
    public String toString() {
        return "FloorJson{" +
                "id=" + id +
                ", number=" + number +
                ", buildingId=" + buildingId +
                ", room=" + Arrays.toString(room) +
                '}';
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public int getBuildingId() {
        return buildingId;
    }

    public void setBuildingId(int buildingId) {
        this.buildingId = buildingId;
    }

    public RoomJson[] getRoom() {
        return room;
    }

    public void setRoom(RoomJson[] room) {
        this.room = room;
    }
}

