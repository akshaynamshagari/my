//package com.letmecall.rgt.config.security.oauth2;
//
//import java.util.Collection;
//import java.util.Map;
//
//import org.springframework.security.core.GrantedAuthority;
//import org.springframework.security.oauth2.core.OAuth2AccessToken;
//import org.springframework.security.oauth2.core.user.OAuth2User;
//
//import lombok.extern.slf4j.Slf4j;
//
//@Slf4j
//public class CustomOAuth2User implements OAuth2User {
//
//	private String oauth2ClientName;
//	private OAuth2User oauth2User;
//	private OAuth2AccessToken accessToken;
//
//	public CustomOAuth2User(OAuth2User oauth2User, String oauth2ClientName, OAuth2AccessToken accessToken) {
//		this.oauth2User = oauth2User;
//		this.oauth2ClientName = oauth2ClientName;
//		this.accessToken = accessToken;
//	}
//
//	@Override
//	public Map<String, Object> getAttributes() {
//		return oauth2User.getAttributes();
//	}
//
//	@Override
//	public Collection<? extends GrantedAuthority> getAuthorities() {
//		return oauth2User.getAuthorities();
//	}
//
//	@Override
//	public String getName() {
//		return oauth2User.getAttribute("name");
//	}
//
//	public String getEmail() {
//		return oauth2User.<String>getAttribute("email");
//	}
//
//	public String getOauth2ClientName() {
//		return this.oauth2ClientName.toUpperCase();
//	}
//
//	public OAuth2AccessToken getAccessToken() {
//		return accessToken;
//	}
//
//	public void setAccessToken(OAuth2AccessToken accessToken) {
//		this.accessToken = accessToken;
//	}
//
//	@Override
//	public String toString() {
//		return oauth2User.getAttribute("email");
//	}
//}
