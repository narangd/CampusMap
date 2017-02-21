package com.example.campusmap.server;

import com.example.campusmap.data.server.RootJson;
import com.example.campusmap.util.Request;

public class ServerClient {
    private static final String root = "32.194.159.2:8000";
    private static final String datas = root + "/api/datas";

    public RootJson datas(int version) {
        return Request.get(datas)
                .data("version", version)
                .send(RootJson.class);
    }
}
