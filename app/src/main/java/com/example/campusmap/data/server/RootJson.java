package com.example.campusmap.data.server;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
public class RootJson {
    int version;
    String name;
    BuildingJson[] building = new BuildingJson[0];
}
