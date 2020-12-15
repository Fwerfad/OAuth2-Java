package com.intuit.developer.sampleapp.oauth2.controller;

import java.io.IOException;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.ExecutionException;
import javax.servlet.http.HttpSession;

import com.github.scribejava.core.builder.ServiceBuilder;
import com.github.scribejava.apis.VkontakteApi;
import com.github.scribejava.core.model.*;
import com.github.scribejava.core.oauth.AccessTokenRequestParams;
import com.github.scribejava.core.oauth.OAuth20Service;

import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.log4j.Logger;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.intuit.developer.sampleapp.oauth2.domain.BearerTokenResponse;
import com.intuit.developer.sampleapp.oauth2.domain.OAuth2Configuration;
import com.intuit.developer.sampleapp.oauth2.helper.HttpHelper;
import com.intuit.developer.sampleapp.oauth2.service.ValidationService;

/**
 * @author dderose
 *
 */
@Controller
public class CallbackController {
    public static String name = "";
    public static String surname = "";
    private static final String PROTECTED_RESOURCE_URL = "https://api.vk.com/method/users.get?v="
            + VkontakteApi.VERSION;
    
    @Autowired
    public OAuth2Configuration oAuth2Configuration;
    
    @Autowired
    public ValidationService validationService;
    
    @Autowired
    public HttpHelper httpHelper;
    
    private static final HttpClient CLIENT = HttpClientBuilder.create().build();
    private static ObjectMapper mapper = new ObjectMapper();
    private static final Logger logger = Logger.getLogger(CallbackController.class);

    @RequestMapping("/callback")
    public String callBackFromOAuth(@RequestParam("code") String authCode) {
        logger.debug("inside oauth2redirect " + authCode  );
        final String clientId = "7651720";
        final String clientSecret = "jKh2Mp5PcUwJbkSmxq2Q";
        final OAuth20Service service = new ServiceBuilder(clientId)
                .apiSecret(clientSecret)
                .defaultScope("wall,offline") // replace with desired scope
                .callback("http://localhost:8080/callback")
                .build(VkontakteApi.instance());
        final String customScope = "wall,offline,email";
        final String authorizationUrl = service.createAuthorizationUrlBuilder().scope(customScope).build();

        try {
            final OAuth2AccessToken accessToken;
            accessToken = service.getAccessToken(AccessTokenRequestParams.create(authCode));
            final OAuthRequest request = new OAuthRequest(Verb.GET, PROTECTED_RESOURCE_URL);
            service.signRequest(accessToken, request);
            try (Response response = service.execute(request)) {
                String[] arr = response.getBody().split("\"*\"");
                System.out.println("Имя:" + arr[5] + "\nФамилия:" + arr[11]);
                name = arr[5];
                surname = arr[11];
            }
            System.out.println();
        } catch (Exception e) {
            System.out.println(e);
        }

        return "connected";
    }

    private BearerTokenResponse retrieveBearerTokens(String auth_code, HttpSession session) {
        logger.info("inside bearer tokens");

        HttpPost post = new HttpPost(oAuth2Configuration.getIntuitBearerTokenEndpoint());

        // add header
        post = httpHelper.addHeader(post);
        List<NameValuePair> urlParameters = httpHelper.getUrlParameters(session, "");

        try {
            post.setEntity(new UrlEncodedFormEntity(urlParameters));
            HttpResponse response = CLIENT.execute(post);

            logger.info("Response Code : "+ response.getStatusLine().getStatusCode());
            if (response.getStatusLine().getStatusCode() != 200) {
                logger.info("failed getting access token");
                return null;
            }

            StringBuffer result = httpHelper.getResult(response);
            logger.debug("raw result for bearer tokens= " + result);

            return mapper.readValue(result.toString(), BearerTokenResponse.class);
            
        } catch (Exception ex) {
            logger.error("Exception while retrieving bearer tokens", ex);
        }
        return null;
    }
    
    private void saveUserInfo(String accessToken, HttpSession session) {
        //Ideally you would fetch the realmId and the accessToken from the data store based on the user account here.
        HttpGet userInfoReq = new HttpGet(oAuth2Configuration.getUserProfileApiHost());
        userInfoReq.setHeader("Accept", "application/json");
        userInfoReq.setHeader("Authorization","Bearer "+accessToken);

        try {
            HttpResponse response = CLIENT.execute(userInfoReq);

            logger.info("Response Code : "+ response.getStatusLine().getStatusCode());
            if (response.getStatusLine().getStatusCode() == 200) {
                
                StringBuffer result = httpHelper.getResult(response);
                logger.debug("raw result for user info= " + result);

                //Save the UserInfo here.
                JSONObject userInfoPayload = new JSONObject(result.toString());
                session.setAttribute("sub", userInfoPayload.get("sub"));
                session.setAttribute("givenName", userInfoPayload.get("givenName"));
                session.setAttribute("email", userInfoPayload.get("email"));
                
            } else {
                logger.info("failed getting user info");
            }

            
        }
        catch (Exception ex) {
            logger.error("Exception while retrieving user info ", ex);
        }
    }

}
