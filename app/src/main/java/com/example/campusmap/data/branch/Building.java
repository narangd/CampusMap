package com.example.campusmap.data.branch;

import java.io.Serializable;

import lombok.Value;

@Value
public class Building implements Serializable {

    int id;
    int number;
    String name;
    String description;

    @Override
    public String toString() {
        return number + "-" + name;
    }
}
