//package com.letmecall.rgt.config.security.oauth2;
//
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//
//import org.apache.commons.lang3.StringUtils;
//import org.springframework.security.oauth2.client.web.AuthorizationRequestRepository;
//import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
//import org.springframework.stereotype.Component;
//
//import com.letmecall.rgt.util.CookieUtils;
//
////@Component
//public class HttpCookieOAuth2AuthorizationRequestRepository
//		implements AuthorizationRequestRepository<OAuth2AuthorizationRequest> {
//	public static final String OAUTH2_AUTHORIZATION_REQUEST_COOKIE_NAME = "oauth2_auth_request";
//	public static final String REDIRECT_URI_PARAM_COOKIE_NAME = "redirect_uri";
//	public static final int COOKIEEXPIRESECONDS = 180;
//
//	public OAuth2AuthorizationRequest loadAuthorizationRequest(HttpServletRequest request) {
//		return CookieUtils.getCookie(request, OAUTH2_AUTHORIZATION_REQUEST_COOKIE_NAME)
//				.map(cookie -> CookieUtils.deserialize(cookie, OAuth2AuthorizationRequest.class)).orElse(null);
//	}
//
//	public void saveAuthorizationRequest(OAuth2AuthorizationRequest authorizationRequest, HttpServletRequest request,
//			HttpServletResponse response) {
//		if (authorizationRequest == null) {
//			CookieUtils.deleteCookie(request, response, OAUTH2_AUTHORIZATION_REQUEST_COOKIE_NAME);
//			CookieUtils.deleteCookie(request, response, REDIRECT_URI_PARAM_COOKIE_NAME);
//			return;
//		}
//
//		CookieUtils.addCookieforHttp(response, OAUTH2_AUTHORIZATION_REQUEST_COOKIE_NAME,
//				CookieUtils.serialize(authorizationRequest), COOKIEEXPIRESECONDS);
//		String redirectUriAfterLogin = request.getParameter(REDIRECT_URI_PARAM_COOKIE_NAME);
//		if (StringUtils.isNotBlank(redirectUriAfterLogin)) {
//			CookieUtils.addCookieforHttp(response, REDIRECT_URI_PARAM_COOKIE_NAME, redirectUriAfterLogin,
//					COOKIEEXPIRESECONDS);
//		}
//	}
//
//	@Override
//	public OAuth2AuthorizationRequest removeAuthorizationRequest(HttpServletRequest request,
//			HttpServletResponse response) {
//		return this.loadAuthorizationRequest(request);
//	}
//
//	public void removeAuthorizationRequestCookies(HttpServletRequest request, HttpServletResponse response) {
//		CookieUtils.deleteCookie(request, response, OAUTH2_AUTHORIZATION_REQUEST_COOKIE_NAME);
//		CookieUtils.deleteCookie(request, response, REDIRECT_URI_PARAM_COOKIE_NAME);
//	}
//
//}
