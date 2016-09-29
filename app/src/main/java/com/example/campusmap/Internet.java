package com.example.campusmap;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Looper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;
import java.util.Set;

public class Internet {
    public static final String CONNECTION_METHOD_POST = "POST";
    public static final String CONNECTION_METHOD_GET = "GET";
    private static final int TIMEOUT = 1000 * 5;

    public static boolean isInternetConnect(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting();
    }

    public static String connectHttpPage(String urlString, String method, Map data) {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            throw new IllegalThreadStateException("메인 쓰레드에서 인터넷에 접속할 수 없습니다.");
        }

        String parameter = "";
        if (data != null) {
            boolean first = true;
            Set keySet = data.keySet();
            for (Object key : keySet) {
                Object value = data.get(key);

                if (first) {
                    first = false;
                } else {
                    parameter += "&";
                }

                parameter += key + "=" + value;
            }
        }

        try {
            if (method.equals(CONNECTION_METHOD_GET)) {
                urlString += "?" + parameter;
            }

            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setReadTimeout( TIMEOUT );
            connection.setConnectTimeout( TIMEOUT );
            connection.setRequestMethod(method);
            connection.setDoInput(true);
            connection.setDoOutput(true);

            OutputStream os = connection.getOutputStream();
            OutputStreamWriter writer = new OutputStreamWriter(os);
            writer.write(parameter);
            writer.flush();
            writer.close();

            StringBuilder result = new StringBuilder();
            String line;
            InputStream is = connection.getInputStream();
            InputStreamReader isr = new InputStreamReader(is);
            BufferedReader reader  = new BufferedReader(isr);

            while ((line = reader.readLine()) != null) {
                result.append(line);
            }

            reader.close();

            return result.toString();

        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static void delay500ms() {
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
