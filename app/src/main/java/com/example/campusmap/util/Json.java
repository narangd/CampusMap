package com.example.campusmap.util;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.InputStream;

public class Json {
    public static <T> T toClass(String json, Class<T> returnType) {
        try {
            return new ObjectMapper().readValue(json, returnType);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
    public static <T> T toClass(InputStream is, Class<T> returnType) {
        try {
            return new ObjectMapper().readValue(is, returnType);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
