//package com.letmecall.rgt.config.security.oauth2;
//
//import java.io.IOException;
//import java.util.Arrays;
//import java.util.Calendar;
//
//import jakarta.servlet.ServletException;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//
//import org.springframework.security.core.Authentication;
//import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
//import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
//import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
//import org.springframework.security.oauth2.core.OAuth2AccessToken;
//import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
//import org.springframework.security.web.authentication.WebAuthenticationDetails;
//import org.springframework.stereotype.Component;
//
//import com.letmecall.rgt.config.AppProperties;
//import com.letmecall.rgt.config.security.RestTokenProvider;
//import com.letmecall.rgt.config.security.oauth2.user.OAuth2UserInfo;
//import com.letmecall.rgt.config.security.oauth2.user.OAuth2UserInfoFactory;
//import com.letmecall.rgt.domain.UserAuthDetails;
//import com.letmecall.rgt.domain.socialmedia.SocialUser;
//import com.letmecall.rgt.util.CookieUtils;
//import com.letmecall.rgt.util.TokenUtils;
//import com.letmecall.rgt.utility.DateUtil;
//import com.letmecall.rgt.utility.Constant.Role;
//import com.letmecall.service.SocialUserService;
//import com.letmecall.service.UserAuthDetailsServcie;
//
//import lombok.extern.slf4j.Slf4j;
//
////@Component
//@Slf4j
//public class OAuth2LoginSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {
//
//	SocialUserService socialUserService;
//
//	UserAuthDetailsServcie userDetailsService;
//
//	OAuth2AuthorizedClientService clientService;
//
//	HttpCookieOAuth2AuthorizationRequestRepository httpCookieOAuth2AuthorizationRequestRepository;
//
//	AppProperties appProperties;
//
//	RestTokenProvider restTokenProvider;
//
//	public OAuth2LoginSuccessHandler(SocialUserService socialUserService, UserAuthDetailsServcie userDetailsService,
//			OAuth2AuthorizedClientService clientService,
//			HttpCookieOAuth2AuthorizationRequestRepository httpCookieOAuth2AuthorizationRequestRepository,
//			AppProperties appProperties, RestTokenProvider restTokenProvider) {
//		super();
//		this.socialUserService = socialUserService;
//		this.userDetailsService = userDetailsService;
//		this.clientService = clientService;
//		this.httpCookieOAuth2AuthorizationRequestRepository = httpCookieOAuth2AuthorizationRequestRepository;
//		this.appProperties = appProperties;
//		this.restTokenProvider = restTokenProvider;
//	}
//
//	@Override
//	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
//			Authentication authentication) throws ServletException, IOException {
//		CustomOAuth2User oauth2User = (CustomOAuth2User) authentication.getPrincipal();
//		WebAuthenticationDetails webAuthenticationDetails = (WebAuthenticationDetails) authentication.getDetails();
//		OAuth2AccessToken oAuth2AccessToken = oauth2User.getAccessToken() != null ? oauth2User.getAccessToken()
//				: getAccessToken(authentication);
//		UserAuthDetails userAuthDetails = new UserAuthDetails();
//		userAuthDetails.setEmailAddress(oauth2User.getEmail());
//		userAuthDetails = userDetailsService.fetchUserAuthDetails(userAuthDetails);
//
//		OAuth2UserInfo oAuth2UserInfo = OAuth2UserInfoFactory.getOAuth2UserInfo(oauth2User.getOauth2ClientName(),
//				oauth2User.getAttributes());
//		SocialUser socialUser = socialUserService.findSocialUserByUserName(oauth2User.getEmail(),
//				oauth2User.getOauth2ClientName());
//
//		if (socialUser == null) {
//			socialUser = new SocialUser();
//			socialUser.setEmail(oAuth2UserInfo.getEmail());
//			socialUser.setDisplayName(oAuth2UserInfo.getName());
//			socialUser.setImageUrl(oAuth2UserInfo.getImageUrl());
//			socialUser.setProfileUrl(oAuth2UserInfo.getImageUrl());
//			socialUser.setProviderId(oauth2User.getOauth2ClientName());
//			socialUser.setProviderUserId(oAuth2UserInfo.getId());
//			socialUser.setRank(0);
//			buildAccessTokenAndExpire(webAuthenticationDetails, oAuth2AccessToken, socialUser);
//			// create social login details
//			socialUser = socialUserService.saveSocialUser(socialUser, socialUser.getExpireTime());
//		} else {
//			buildAccessTokenAndExpire(webAuthenticationDetails, oAuth2AccessToken, socialUser);
//			// update expire time & token
//			socialUser = socialUserService.updateSocialUser(socialUser);
//		}
//
//		if (userAuthDetails == null) {
//			userAuthDetails = createUserDetailsWithoutAuthDetails(socialUser);
//		}
//
//		// update login time
//		userDetailsService.updateLastAccessTime(userAuthDetails);
//
//		storeTokenIntoCookie(response, userAuthDetails, socialUser);
//
//		clearAuthenticationAttributes(request, response);
//
//		super.onAuthenticationSuccess(request, response, authentication);
//
//	}
//
//	private String storeTokenIntoCookie(HttpServletResponse response, UserAuthDetails userAuthDetails,
//			SocialUser socialUser) {
//		String jwtToken = restTokenProvider.createTokenForUser(socialUser, Arrays.asList(Role.ROLE_USER.name()));
//
//		CookieUtils.addCookieWithoutHttp(response, "id", userAuthDetails.getUserId().toString(),
//				HttpCookieOAuth2AuthorizationRequestRepository.COOKIEEXPIRESECONDS);
//		CookieUtils.addCookieWithoutHttp(response, "rgtToken", jwtToken,
//				HttpCookieOAuth2AuthorizationRequestRepository.COOKIEEXPIRESECONDS);
//		CookieUtils.addCookieWithoutHttp(response, "rgtAuthType", socialUser.getProviderId(),
//				HttpCookieOAuth2AuthorizationRequestRepository.COOKIEEXPIRESECONDS);
//
//		return TokenUtils.buildToken(userAuthDetails, socialUser, jwtToken);
//	}
//
//	private void buildAccessTokenAndExpire(WebAuthenticationDetails webAuthenticationDetails,
//			OAuth2AccessToken oAuth2AccessToken, SocialUser socialUser) {
//		if (oAuth2AccessToken != null) {
//			socialUser.setAccessToken(oAuth2AccessToken.getTokenValue());
//			if (oAuth2AccessToken.getExpiresAt() != null) {
//				try {
//					long expireTime = oAuth2AccessToken.getExpiresAt().getEpochSecond();
//					socialUser.setExpireTime(expireTime);
//				} catch (Exception e) {
//					e.printStackTrace();
//				}
//			}
//		} else {
//			socialUser.setAccessToken(webAuthenticationDetails.getSessionId());
//		}
//
//		if (socialUser.getExpireTime() == null) {
//			Calendar calendar = Calendar.getInstance();
//			calendar.add(Calendar.MONTH, 2);
//			socialUser.setExpireTime(calendar.getTimeInMillis());
//		} else {
//			socialUser.setExpireTime(socialUser.getExpireTime() * 1000);
//		}
//	}
//
//	private UserAuthDetails createUserDetailsWithoutAuthDetails(SocialUser socialUser) {
//		UserAuthDetails userAuthDetails = new UserAuthDetails();
//		userAuthDetails.setEmailAddress(socialUser.getEmail());
//		userAuthDetails.setLastLoginTime(DateUtil.getCurrentTimeStamp());
//		userAuthDetails.setIsActive(true);
//		userAuthDetails.setIsRegistered(false);
//		userAuthDetails.setLastLogOutTime(null);
//		userAuthDetails.setFirstName(socialUser.getDisplayName());
//		userAuthDetails.setLastName("");
//		userAuthDetails.setAuthProviderName(socialUser.getProviderId().toUpperCase());
//		return userDetailsService.createUserDetails(userAuthDetails);
//	}
//
//	private void clearAuthenticationAttributes(HttpServletRequest request, HttpServletResponse response) {
//		super.clearAuthenticationAttributes(request);
//		httpCookieOAuth2AuthorizationRequestRepository.removeAuthorizationRequestCookies(request, response);
//	}
//
//	private OAuth2AccessToken getAccessToken(Authentication authentication) {
//		OAuth2AuthenticationToken oauthToken = (OAuth2AuthenticationToken) authentication;
//		OAuth2AuthorizedClient client = clientService
//				.loadAuthorizedClient(oauthToken.getAuthorizedClientRegistrationId(), oauthToken.getName());
//		return client.getAccessToken();
//	}
//
//}
