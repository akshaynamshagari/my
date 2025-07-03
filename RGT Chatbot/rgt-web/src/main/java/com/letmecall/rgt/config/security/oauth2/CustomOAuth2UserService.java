//package com.letmecall.rgt.config.security.oauth2;
//
//import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
//import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
//import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
//import org.springframework.security.oauth2.core.user.OAuth2User;
//import org.springframework.stereotype.Service;
//
//import lombok.extern.slf4j.Slf4j;
//
////@Service
//@Slf4j
//public class CustomOAuth2UserService extends DefaultOAuth2UserService {
//
//	@Override
//	public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
//		String clientName = userRequest.getClientRegistration().getClientName();
//		return new CustomOAuth2User(super.loadUser(userRequest), clientName, userRequest.getAccessToken());
//	}
//
//}
