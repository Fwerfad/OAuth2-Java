package com.intuit.developer.sampleapp.oauth2.controller;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.UUID;

import javax.servlet.http.HttpSession;

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

	/**
	 * Controller mapping for connectToQuickbooks button
	 * @return
	 */
	@RequestMapping("/connectToQuickbooks")
	public View connectToQuickbooks(HttpSession session) {
		logger.info("inside connectToQuickbooks ");
		return new RedirectView(prepareUrl(oAuth2Configuration.getC2QBScope(), generateCSRFToken(session)), true, true, false);
	}
	
	/**
	 * Controller mapping for signInWithIntuit button
	 * @return
	 */
	@RequestMapping("/signInWithIntuit")
	public View signInWithIntuit(HttpSession session) {
		logger.info("inside signInWithIntuit ");
		return new RedirectView(prepareUrl(oAuth2Configuration.getSIWIScope(), generateCSRFToken(session)), true, true, false);
	}
	
	/**
	 * Controller mapping for getAppNow button
	 * @return
	 */
	@RequestMapping("/getAppNow")
	public View getAppNow(HttpSession session) {
		logger.info("inside getAppNow "  );
		return new RedirectView(prepareUrl(oAuth2Configuration.getAppNowScope(), generateCSRFToken(session)), true, true, false);
	}
	
	private String prepareUrl(String scope, String csrfToken)  {
		return "https://oauth.vk.com/authorize?v=5.92&response_type=code&client_id=7651720&redirect_uri=http%3A%2F%2Flocalhost%3A8080%2Fcallback&scope=wall%2Coffline%2Cemail";
	}
	
	private String generateCSRFToken(HttpSession session)  {
		String csrfToken = UUID.randomUUID().toString();
		session.setAttribute("csrfToken", csrfToken);
		return csrfToken;
	}

}
