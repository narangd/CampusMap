package com.example.campusmap.util;

import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Request {

    private String url = "";
    private HttpMethod method = HttpMethod.GET;
    private JSONObject datas = new JSONObject();

    private int connectionTimeout = 5000;
    private int readTimeout = 20000;

    private Request(String url, HttpMethod method) {
        this.url = url;
        this.method = method;
    }

    private RestTemplate newRestTemplate(int connectionOut, int readOut) {
        HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory();
        factory.setConnectTimeout(connectionOut);
        factory.setReadTimeout(readOut);
        return new RestTemplate(factory);
    }

    public Request connectionOut(int connectionTimeout) {
        this.connectionTimeout = Math.max(connectionTimeout, 500);
        return this;
    }

    public Request readOut(int readTimeout) {
        this.readTimeout = Math.max(readTimeout, 1000);
        return this;
    }

    public static Request get(String url) {
        return new Request(url, HttpMethod.GET);
    }

    public Request data(String name, Object value) {
        try {
            datas.put(name, value);
        } catch (JSONException e) {
            log.info("data insert error : {}", e.getMessage());
        }
        return this;
    }

    public Request data(JSONObject datas) {
        this.datas = datas;
        return this;
    }

    public <ResponseObject> ResponseObject send(Class<ResponseObject> returnType) {
        RestTemplate restTemplate = newRestTemplate(connectionTimeout, readTimeout);
//        restTemplate.

        HttpEntity<String> entity = new HttpEntity<>(datas.toString());
        ResponseEntity<ResponseObject> responseEntity = restTemplate.exchange(url, method, entity, returnType);

        responseEntity.getStatusCode(); // ResponseHandler ...

        return responseEntity.getBody();
    }
}
