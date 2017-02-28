package com.example.campusmap.server;

import com.example.campusmap.data.server.RootJson;
import com.example.campusmap.util.Request;

public class ServerClient {
    private static final String root = "http://34.194.159.2:8000";
    private static final String datas = root + "/api/versions";

    private static ServerClient client = new ServerClient();

    private ServerClient() {}

    public static RootJson datas(int version) {
        return Request.get(datas)
                .data("version", version)
                .send(RootJson.class);
    }

    public static RootJson datasTemp(int version) {
        return Request.get(root + "/building.json")
                .data("version", version)
                .send(RootJson.class);
    }
}
