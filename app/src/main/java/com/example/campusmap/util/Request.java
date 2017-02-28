package com.example.campusmap.util;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

public class Request {
    private static final String TAG = "Request";

    private String url = "";
    private HttpMethod method = HttpMethod.GET;
    private Map<String,Object> datas = new HashMap<>();

    private int connectionTimeout = 3000;
    private int readTimeout = 10000;

    private Request(String url, HttpMethod method) {
        this.url = url;
        this.method = method;
    }

    private RestTemplate newRestTemplate() {
        HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory();
        factory.setConnectTimeout(connectionTimeout);
        factory.setReadTimeout(readTimeout);
//        factory.setHttpClient();
// http://stackoverflow.com/questions/27420841/how-to-do-a-progress-bar-to-show-progress-download-of-a-big-file-with-androidann
        RestTemplate restTemplate = new RestTemplate(factory);

        restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
        return restTemplate;
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
        datas.put(name, value);
        return this;
    }

    public Request data(Map<String,Object> datas) {
        this.datas = datas;
        return this;
    }

    public <ResponseObject> ResponseObject send(Class<ResponseObject> returnType) {
        RestTemplate restTemplate = newRestTemplate();
//        restTemplate.

        ResponseEntity<ResponseObject> responseEntity = new ResponseEntity<>(HttpStatus.OK);
        try {
//            responseEntity = restTemplate.exchange(url, method, entity, returnType);
            switch (method) {
                case GET:
                    responseEntity = restTemplate.getForEntity(url, returnType, datas);
                    break;
                case POST:
                    responseEntity = restTemplate.postForEntity(url, datas, returnType);
                    break;
                case PUT:
//                    responseEntity = restTemplate.put
            }
        } catch (Exception e) {
            Log.e(TAG, "send: ", e);
            responseEntity = new ResponseEntity<>(HttpStatus.OK);
        }

        Log.i(TAG, "send: content length : " + responseEntity.getHeaders().getContentLength());
//        restTemplate

        responseEntity.getStatusCode(); // ResponseHandler ...

        return responseEntity.getBody();
    }
}
