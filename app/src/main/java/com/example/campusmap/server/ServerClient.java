package com.example.campusmap.server;

import com.example.campusmap.BuildConfig;
import com.example.campusmap.data.server.RootJson;
import com.example.campusmap.util.Request;

public class ServerClient {
    private static final String root = BuildConfig.BUILD_TYPE.contentEquals("debuga")
            ? "http://118.91.120.158:8000"
            : "http://52.78.215.51:8000";
    private static final String datas = root + "/api/versions/";

    private static ServerClient client = new ServerClient();

    private ServerClient() {}

    public static RootJson versions(int version) {
        return Request.get(datas + version)
                .send(RootJson.class);
    }

    public static RootJson datasTemp(int version) {
        return Request.get(root + "/building.json")
                .data("version", version)
                .send(RootJson.class);
    }
}
