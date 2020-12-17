package com.intuit.developer.sampleapp.oauth2.controller;
import com.github.scribejava.apis.VkontakteApi;
import com.github.scribejava.core.model.*;
import com.github.scribejava.core.oauth.AccessTokenRequestParams;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;

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

    @RequestMapping("/callback")
    public String callBackFromOAuth(@RequestParam("code") String authCode) {
        try {
            final OAuth2AccessToken accessToken;
            accessToken = OAuth2Controller.service.getAccessToken(AccessTokenRequestParams.create(authCode));
            final OAuthRequest request = new OAuthRequest(Verb.GET, PROTECTED_RESOURCE_URL);
            OAuth2Controller.service.signRequest(accessToken, request);
            try (Response response = OAuth2Controller.service.execute(request)) {
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
    @ResponseBody
    @RequestMapping("/getName")
    public String revokeToken() {
        try {
            return CallbackController.name + " " + CallbackController.surname;
        }
        catch (Exception e) {
            return e.toString();
        }

    }
}
