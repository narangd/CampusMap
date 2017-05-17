package com.example.campusmap.data.server;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.Arrays;

import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
public class RootJson {
    int version;
    String name;
    BuildingJson[] building = new BuildingJson[0];

    @Override
    public String toString() {
        return "RootJson{" +
                "version=" + version +
                ", name='" + name + '\'' +
                ", building=" + Arrays.toString(building) +
                '}';
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BuildingJson[] getBuilding() {
        return building;
    }

    public void setBuilding(BuildingJson[] building) {
        this.building = building;
    }
}
