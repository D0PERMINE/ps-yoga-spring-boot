package com.example.demo.controller;

import com.example.demo.model.Post;
import com.example.demo.model.Tokens;
import com.example.demo.service.RestService;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
@RestController
public class AuthenticationController {

    HashMap<String, String> userStateAndCode = new HashMap<>();
    private String accessToken = null;
    private String refreshToken = null;
    private String tokenURL = "https://zoom.us/oauth/token";
    private RestTemplateBuilder restTemplate = new RestTemplateBuilder();
    private RestService restService = new RestService(restTemplate);


    @GetMapping("/")
    String saveUserStateAndCode(@RequestParam(required = false, name = "state") String userState,@RequestParam String code) {
        if((code != null || userState != null) && this.accessToken == null){
            userStateAndCode.put(userState, code);
            String valueCode = userStateAndCode.get(userState);
            Tokens tokens = restService.getTokens(valueCode);
            this.accessToken = tokens.getAccess_token();
            this.refreshToken = tokens.getRefresh_token();
            return "Your code is automatically used to authenticate to Zoom -- " + valueCode + "-- You can close this tab now.";
        }
        return "TOKEN_ALREADY_REQUESTED";
    }
    @GetMapping("/refreshToken")
    String sendNewAccessToken(){
        Tokens tokens = restService.refreshToken(this.refreshToken);
        if(tokens.getAccess_token() == null)
            return "couldn't get token";
        this.accessToken = tokens.getAccess_token();
        this.refreshToken = tokens.getRefresh_token();
        return this.refreshToken;
    }
    @GetMapping("/token")
    String sendTokens(){
        if(this.accessToken != null)
            return this.accessToken;
        else
            return "NO_TOKEN";
    }
    @GetMapping("/code/{userState}")
    String getCodeByUserState(@PathVariable String userState) {
        if(userState != null){
            if(userStateAndCode.get(userState)==null)
                return "error no code found for given state";
            return userStateAndCode.get(userState);
        }
        return null;
    }
}
