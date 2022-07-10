package com.example.demo.service;

import com.example.demo.model.Post;
import com.example.demo.model.Tokens;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.*;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Base64;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Service
public class RestService {

    private final RestTemplate restTemplate;
    private final String redirectUri = "https://springboot-app-yoga-heorku.herokuapp.com";
    private final String clientId = "SGzrqOyvRceYFZIoiqB7uQ";
    private final String clientSecret = "9yGgmvJzgY9OKAqLvFEb7pjN57XAl1NN";
    private final String idAndSecret = clientId + ":" + clientSecret;
    private String encodedidAndSecret = Base64.getEncoder().encodeToString(idAndSecret.getBytes());


    public RestService(RestTemplateBuilder restTemplateBuilder) {
        this.restTemplate = restTemplateBuilder.build();
        this.restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory());
    }
    public Tokens getTokens(String authCode) {
        String url = "https://zoom.us/oauth/token?code=" + authCode + "&grant_type=authorization_code&redirect_uri=" + this.redirectUri;

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.valueOf(MediaType.APPLICATION_FORM_URLENCODED_VALUE));
        headers.setBasicAuth(encodedidAndSecret);

        // build the request
        HttpEntity<Post> entity = new HttpEntity<>(headers);

        // send POST request
        Tokens tokens = restTemplate.postForObject(url, entity, Tokens.class);
        System.out.println(tokens.getRefresh_token());
        return tokens;
    }

    public Tokens refreshToken(String refreshToken) {
        String url = "https://zoom.us/oauth/token?refresh_token=" + refreshToken + "&grant_type=refresh_token";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.valueOf(MediaType.APPLICATION_FORM_URLENCODED_VALUE));
        headers.setBasicAuth(encodedidAndSecret);

        // build the request
        HttpEntity<Post> entity = new HttpEntity<>(headers);

        // send POST request
        Tokens tokens = restTemplate.postForObject(url, entity, Tokens.class);
        return tokens;
    }
}