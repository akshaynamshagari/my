package com.letmecall.rgt.config.security;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import com.letmecall.rgt.domain.UserAuthDetails;
import com.letmecall.rgt.domain.socialmedia.SocialUser;
import com.letmecall.rgt.rest.controller.RgtResource;

@Component
public class RestTokenProvider {

	@Autowired
	private JwtTokenProvider tokenHandler;

	@Value("${com.letmecall.nextdoor.token.expiry.time.millis}")
	private long tokenExpireinMillis;

	@Value("${com.letmecall.nextdoor.token.expiry.days}")
	private long tokenExpireinDays;

	@Value("${com.letmecall.nextdoor.rest.user.token.issuer}")
	private String issuer;

	public String createTokenForUser(UserAuthDetails userDetails, List<String> roles) {
		return tokenHandler.createTokenForUser(userDetails, roles, UUID.randomUUID().toString(), issuer,
				tokenExpireinDays * tokenExpireinMillis);
	}

	public String createTokenForUser(SocialUser socialUser, List<String> roles) {
		return tokenHandler.createTokenForUser(socialUser, roles, socialUser.getProviderUserId(), issuer,
				socialUser.getExpireTime());
	}

	public boolean authenticateUserByToken(String token) {
		if (token.contains(RgtResource.TOKEN_TYPE))
			return tokenHandler.parseUserFromOauth2Token(token);
		else
			return tokenHandler.parseUserFromToken(token);
	}

	public Authentication getAuthentication(String token) {
		return tokenHandler.getAuthentication(token);
	}

	public static Date getTokenCreationDate(String token) {
		return JwtTokenProvider.getIssuedAt(token);
	}

	public static Map<String, String> splitToken(String token) {
		return JwtTokenProvider.splitToken(token);
	}

	public boolean apiAccessForSpecificRole(List<String> role, String apiName, String requestMethod) {
		return tokenHandler.apiAccessForSpecifiedRole(role, apiName, requestMethod);
	}

}
