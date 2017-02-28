package com.example.campusmap.data.server;

import lombok.Data;

@Data
public class RootJson {
    int version;
    BuildingJson[] building = new BuildingJson[0];
}
