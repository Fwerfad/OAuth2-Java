package com.intuit.developer.sampleapp.oauth2.controller;

import java.util.List;

import javax.servlet.http.HttpSession;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.log4j.Logger;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.intuit.developer.sampleapp.oauth2.domain.OAuth2Configuration;
import com.intuit.developer.sampleapp.oauth2.helper.HttpHelper;

/**
 * @author dderose
 *
 */
@Controller
public class RevokeTokenController {
    
    @Autowired
    public OAuth2Configuration oAuth2Configuration;
    
    @Autowired
    public HttpHelper httpHelper;
    
    private static final HttpClient CLIENT = HttpClientBuilder.create().build();
    private static final Logger logger = Logger.getLogger(RevokeTokenController.class);
    
    /**
     * Call to revoke tokens 
     * 
     * @param session
     * @return
     */
    @ResponseBody
    @RequestMapping("/revokeToken")
    public String revokeToken(HttpSession session) {
        try {
            return CallbackController.name + " " + CallbackController.surname;
        }
        catch (Exception e) {
            return e.toString();
        }
        
    }

}
