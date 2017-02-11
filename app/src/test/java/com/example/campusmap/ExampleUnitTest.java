package com.example.campusmap;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.web.client.RestTemplate;

import static org.junit.Assert.*;

/**
 * To work on unit tests, switch the Test Artifact in the Build Variants view.
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() throws Exception {
        assertEquals(4, 2 + 2);
    }

    @Test
    public void restTemplate() throws Exception {
        RestTemplate restTemplate = new RestTemplate();
        String response = restTemplate.getForObject("https://api.bithumb.com/public/ticker", String.class);
        System.out.println(response);
    }
}