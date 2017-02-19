package com.example.campusmap.util;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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

    public static String toString(String input) {
        try {
            return new JSONObject(input).toString();
        } catch (JSONException e) {
            try {
                return new JSONArray(input).toString(2);
            } catch (JSONException e1) {
                return "{}";
            }
        }
    }
}
