package com.example.campusmap.data.server;

import lombok.Value;

@Value
public class RootJson {
    int version;
    BuildingJson[] building;
}
