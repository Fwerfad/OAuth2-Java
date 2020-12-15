package com.intuit.developer.sampleapp.oauth2.controller;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.UUID;

import javax.servlet.http.HttpSession;

import com.github.scribejava.apis.VkontakteApi;
import com.github.scribejava.core.builder.ServiceBuilder;
import com.github.scribejava.core.oauth.OAuth20Service;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.view.RedirectView;

import com.intuit.developer.sampleapp.oauth2.domain.OAuth2Configuration;
import com.intuit.developer.sampleapp.oauth2.helper.HttpHelper;
import com.intuit.developer.sampleapp.oauth2.service.ValidationService;

/**
 * @author dderose
 *
 */
@Controller
public class OAuth2Controller {
	
	private static final Logger logger = Logger.getLogger(OAuth2Controller.class);
	private static final String clientId = "7651720";
	private static final String clientSecret = "jKh2Mp5PcUwJbkSmxq2Q";
	private static final OAuth20Service service = new ServiceBuilder(clientId)
			.apiSecret(clientSecret)
			.defaultScope("wall,offline") // replace with desired scope
			.callback("http://localhost:8080/callback")
			.build(VkontakteApi.instance());
	private static final String customScope = "wall,offline,email";
	private static final String authorizationUrl = service.createAuthorizationUrlBuilder().scope(customScope).build();


	@Autowired
    public OAuth2Configuration oAuth2Configuration;
	
	@Autowired
    public ValidationService validationService;
	
	@Autowired
    public HttpHelper httpHelper;
	    
	@RequestMapping("/")
	public String home() {
		return "home";
	}
	
	@RequestMapping("/connected")
	public String connected() {
		return "connected";
	}

	@RequestMapping("/connectToVK")
	public View connectToVK(HttpSession session) {
		logger.info("inside vk ");
		return new RedirectView(prepareUrl(oAuth2Configuration.getC2QBScope(), generateCSRFToken(session)), true, true, false);
	}

	@RequestMapping("/signInWithVK")
	public View signInWithVK(HttpSession session) {
		logger.info("inside vk ");
		return new RedirectView(prepareUrl(oAuth2Configuration.getC2QBScope(), generateCSRFToken(session)), true, true, false);
	}
	private String prepareUrl(String scope, String csrfToken)  {
		return authorizationUrl;
	}

}
