package com.example.campusmap.util;

import android.content.Context;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;

public class ResourceUtl {
    private static final String TAG = "ResourceUtl";
    public static String getRaw(Context context, int id) {

        try {
            InputStream is = context.getResources().openRawResource(id);
            InputStreamReader reader = new InputStreamReader(is);
            char[] buffer = new char[1024];
            StringBuilder stringBuilder = new StringBuilder();

            while (reader.read(buffer) > 0) {
                stringBuilder.append(buffer);
            }
            Log.i(TAG, "getRaw read size : " + stringBuilder.length());
            return stringBuilder.toString();
        } catch (IOException e) {
            Log.e(TAG, "getRaw: ", e);
            return "error";
        }
    }
}
