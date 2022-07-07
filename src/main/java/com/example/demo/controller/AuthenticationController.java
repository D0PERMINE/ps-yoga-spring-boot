package com.example.demo.controller;

import com.example.demo.model.Post;
import com.example.demo.model.Tokens;
import com.example.demo.service.ReadWriteInTextFileManager;
import com.example.demo.service.RestService;
import com.example.demo.service.ScheduledTasks;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
@RestController
public class AuthenticationController {

    HashMap<String, String> userStateAndCode = new HashMap<>();
    private String accessToken = null;
    private String refreshToken = null;
    private String tokenURL = "https://zoom.us/oauth/token";
    private final String fileName = "tokens.txt";
    private RestTemplateBuilder restTemplate = new RestTemplateBuilder();
    private RestService restService = new RestService(restTemplate);
    private boolean readTokens = false;
    private static final Logger logger = LoggerFactory.getLogger(ScheduledTasks.class);


    @GetMapping("/")
    String saveUserStateAndCode(@RequestParam(required = false, name = "state") String userState,@RequestParam String code) {
        if((code != null || userState != null) && this.accessToken == null){
            userStateAndCode.put(userState, code);
            String valueCode = userStateAndCode.get(userState);
            Tokens tokens = restService.getTokens(valueCode);
            this.accessToken = tokens.getAccess_token();
            this.refreshToken = tokens.getRefresh_token();
            ReadWriteInTextFileManager.writeIntoFile(tokens.getAccess_token(), tokens.getRefresh_token(), fileName);
            return "Your code is automatically used to authenticate to Zoom -- " + valueCode + "-- You can close this tab now.";
        }
        return "TOKEN_ALREADY_REQUESTED";
    }
    // every 80 minutes refresh Tokens in advance
    @Scheduled(fixedDelay = 4800000)
    @GetMapping("/refreshToken")
    String sendNewAccessToken() {
        if (!this.readTokens)
            return "NO_TOKEN";
        logger.info("refreshed Token" );
        Tokens tokens = restService.refreshToken(this.refreshToken);
        if(tokens.getAccess_token() == null)
            return "couldn't get token";
        this.accessToken = tokens.getAccess_token();
        this.refreshToken = tokens.getRefresh_token();
        ReadWriteInTextFileManager.writeIntoFile(this.accessToken, this.refreshToken, fileName);
        return this.accessToken;
    }
    @GetMapping("/token")
    String sendTokens(){
        // first invocation after server start
        if(!this.readTokens) {
            this.readTokens = true;
            String[] tokens = ReadWriteInTextFileManager.readFromFile("tokens.txt").split("\\r?\\n");
            System.out.println(tokens[1]);
            // if no tokens in text file
             if(tokens.length!=2)
                 return "NO_TOKEN";
             try {
                 Tokens tokenPair = restService.refreshToken(tokens[1]);
                 this.accessToken = tokenPair.getAccess_token();
                 this.refreshToken = tokenPair.getRefresh_token();
                 ReadWriteInTextFileManager.writeIntoFile(this.accessToken, this.refreshToken, fileName);
             }catch (Exception e){
                 System.err.println(e);
                 return "NO_TOKEN";
             }
        }
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
