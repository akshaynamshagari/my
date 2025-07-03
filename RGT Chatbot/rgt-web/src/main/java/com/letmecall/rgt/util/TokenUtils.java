package com.letmecall.rgt.util;

import com.letmecall.rgt.domain.UserAuthDetails;
import com.letmecall.rgt.domain.socialmedia.SocialUser;
import com.letmecall.rgt.rest.controller.RgtResource;

public class TokenUtils {

	public static String buildToken(UserAuthDetails userDetails, String token) {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append(RgtResource.TOKEN_ID);
		stringBuilder.append("=");
		stringBuilder.append(userDetails.getUserId());
		stringBuilder.append(",");
		stringBuilder.append(RgtResource.TOKEN);
		stringBuilder.append("=");
		stringBuilder.append(token);
		return stringBuilder.toString();
	}

	public static String buildToken(UserAuthDetails userDetails, SocialUser socialUser, String token) {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append(RgtResource.TOKEN_ID);
		stringBuilder.append("=");
		stringBuilder.append(userDetails.getUserId());
		stringBuilder.append(",");
		stringBuilder.append(RgtResource.TOKEN);
		stringBuilder.append("=");
		stringBuilder.append(token);
		stringBuilder.append(",");
		stringBuilder.append(RgtResource.TOKEN_TYPE);
		stringBuilder.append("=");
		stringBuilder.append(socialUser.getProviderId().toUpperCase());
		return stringBuilder.toString();
	}
}
