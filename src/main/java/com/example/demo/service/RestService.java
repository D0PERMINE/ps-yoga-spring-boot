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
    private final String redirectUri = "https://bb5e-2a00-20-6041-c0c1-d98c-a8a4-ea1d-a59f.eu.ngrok.io";
    private final String clientId = "dvdxc0Z2SsWaOebq32qlvA";
    private final String clientSecret = "u8Wmy1Emv3p1V1ieP68GPjmDgvyVdJuV";
    private final String idAndSecret = clientId + ":" + clientSecret;
    private String encodedidAndSecret = Base64.getEncoder().encodeToString(idAndSecret.getBytes());

    public RestService(RestTemplateBuilder restTemplateBuilder) {
        this.restTemplate = restTemplateBuilder.build();
        this.restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory());
    }

public Post getPostWithResponseHandling() {
    String url = "https://jsonplaceholder.typicode.com/posts/{id}";
    ResponseEntity<Post> response = this.restTemplate.getForEntity(url, Post.class, 1);
    if(response.getStatusCode() == HttpStatus.OK) {
        System.out.println(response.getBody());
        return response.getBody();
    } else {
        return null;
    }
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
        System.out.println(tokens);
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
        System.out.println(tokens);
        return tokens;
    }
}