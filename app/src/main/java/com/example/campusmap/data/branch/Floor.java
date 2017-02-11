package com.example.campusmap.data.branch;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Value;

@Value
@AllArgsConstructor
public class Floor implements Serializable {
    int id;
    int floor;
    int buildingId;

    @Override
    public String toString() {
        return floor + "층";
    }
}
