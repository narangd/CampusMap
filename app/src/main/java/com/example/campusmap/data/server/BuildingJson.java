package com.example.campusmap.data.server;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.Arrays;

import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
public class BuildingJson {
    int id;
    int number;
    String name;
    String description;
    FloorJson[] floor = new FloorJson[0];

    @Override
    public String toString() {
        return "BuildingJson{" +
                "id=" + id +
                ", number=" + number +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", floor=" + Arrays.toString(floor) +
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public FloorJson[] getFloor() {
        return floor;
    }

    public void setFloor(FloorJson[] floor) {
        this.floor = floor;
    }
}
