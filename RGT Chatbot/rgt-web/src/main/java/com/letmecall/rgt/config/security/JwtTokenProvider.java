package com.letmecall.rgt.config.security;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.crypto.spec.SecretKeySpec;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import com.google.common.base.Splitter;
import com.letmecall.rgt.dao.socialmedia.SocialUserDao;
import com.letmecall.rgt.domain.UserAuthDetails;
import com.letmecall.rgt.domain.socialmedia.SocialUser;
import com.letmecall.rgt.model.TokenData;
import com.letmecall.rgt.repository.RoleBasedApiAccessRepository;
import com.letmecall.rgt.rest.controller.RgtResource;
import com.letmecall.rgt.utility.Constant.Role;
import com.letmecall.service.UserAuthDetailsServcie;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;

@Component
public class JwtTokenProvider {

	private static final Logger logger = LoggerFactory.getLogger(JwtTokenProvider.class);

	@Autowired
	private RoleBasedApiAccessRepository roleBasedApiAccessRepository;

	@Value("${rgt.role.feature}")
	private String apiAccessBasedRole;

	@Autowired
	private SocialUserDao socialUserDao;

	@Autowired
	private UserAuthDetailsServcie userAuthDetailsServcie;

	private static final String TOKEN_EXPIRE = "Seems token is expired.Please login again.";
	private static final String CLAIMS_ROLES = "roles";

	/**
	 * 
	 * @param userDetails
	 * @param roles
	 * @param id
	 * @param issuer
	 * @param ttlMilliSecond
	 * @return created token
	 */
	public String createTokenForUser(UserAuthDetails userDetails, List<String> roles, String id, String issuer,
			long ttlMilliSecond) {
		logger.debug("createTokenForUser: user={},roles={}", userDetails, roles);
		Claims claims = Jwts.claims().setSubject(userDetails.getEmailAddress());
		claims.put(CLAIMS_ROLES, roles);
		long nowMilliSeccond = System.currentTimeMillis();
		Date now = new Date(nowMilliSeccond);
		// add expiration to created token expiration
		long expMilliSecond = nowMilliSeccond;
		if (ttlMilliSecond >= 0) {
			expMilliSecond = nowMilliSeccond + ttlMilliSecond;
		}
		Date tokenExpireTime = new Date(expMilliSecond);
		
		SecretKeySpec keySpec = new SecretKeySpec(userDetails.getPassword().getBytes(StandardCharsets.UTF_8), SignatureAlgorithm.HS512.getJcaName());

		return Jwts.builder().setId(id).setClaims(claims).setIssuer(issuer).setIssuedAt(now)
				.setExpiration(tokenExpireTime).signWith(keySpec).compact();
	}

	/**
	 * 
	 * @param userDetails
	 * @param roles
	 * @param id
	 * @param issuer
	 * @param ttlMilliSecond
	 * @return created token
	 */
	public String createTokenForUser(SocialUser socialUser, List<String> roles, String id, String issuer,
			long ttlMilliSecond) {
		logger.debug("createTokenForUser: user={},roles={}", socialUser, roles);
		Claims claims = Jwts.claims().setSubject(socialUser.getEmail());
		claims.put(CLAIMS_ROLES, roles);
		long nowMilliSeccond = System.currentTimeMillis();
		Date now = new Date(nowMilliSeccond);
		// add expiration to created token expiration
		long expMilliSecond = nowMilliSeccond;
		if (ttlMilliSecond >= 0) {
			expMilliSecond = nowMilliSeccond + ttlMilliSecond;
		}
		Date tokenExpireTime = new Date(expMilliSecond);

		return Jwts.builder().setId(id).setClaims(claims).setIssuer(issuer).setIssuedAt(now)
				.setExpiration(tokenExpireTime).signWith(SignatureAlgorithm.HS512, socialUser.getAccessToken())
				.compact();
	}

	/**
	 * 
	 * @param userName
	 * @param token
	 * @return parseUserFromToken
	 * 
	 */
	public boolean parseUserFromToken(String lmcToken) {
		logger.debug("parseUserFromToken :token ={}", lmcToken);
		Map<String, String> tokens = getTokenDetails(lmcToken);
		Long userId = Long.parseLong(tokens.get(RgtResource.TOKEN_ID));
		String token = tokens.get(RgtResource.TOKEN);
		boolean isValidtoken = false;
		try {
			UserAuthDetails userDetails = getUserDetailBean(userId, null);
			if (userDetails == null) {
				throw new IllegalArgumentException(TOKEN_EXPIRE);
			}

			String password = userDetails.getPassword();

			if (password == null) {
				throw new IllegalArgumentException("Could not find password in DB");
			}

//			Jws<Claims> claims = Jwts.parser().setSigningKey(keySpec).parseClaimsJws(token);
			Jws<Claims> claims = Jwts.parserBuilder()
				    .setSigningKey(password.getBytes(StandardCharsets.UTF_8))
				    .build()
				    .parseClaimsJws(token);

			if (logger.isDebugEnabled()) {
				logger.debug("TOKEN Token ID={},Token  Subject={},Token  Issuer={},Token  Expiration{}",
						claims.getBody().getId(), claims.getBody().getSubject(), claims.getBody().getIssuer(),
						claims.getBody().getExpiration());
			}
			Date tokenCreationTime = claims.getBody().getIssuedAt();
			Date tokenExpireTime = claims.getBody().getExpiration();
			Date logoutTime = userDetails.getLastLogOutTime();
			if (logoutTime != null && logoutTime.after(tokenCreationTime)) {
				logger.debug("LogoutTime ={}, IssuedAt ={},ExpirationTime ={}", logoutTime, tokenCreationTime,
						tokenExpireTime);
			} else {
				logger.debug("Seems token is not expired.");
				isValidtoken = true;
			}
		} catch (ExpiredJwtException | IllegalArgumentException e) {
			buildExeption(e);
		}
		return isValidtoken;
	}

	public boolean parseUserFromOauth2Token(String lmcToken) {
		logger.debug("parseUserFromOauth2Token :token ={}", lmcToken);
		Map<String, String> tokens = getTokenDetails(lmcToken);
		Long userId = Long.parseLong(tokens.get(RgtResource.TOKEN_ID));
		String token = tokens.get(RgtResource.TOKEN);
		String tokenType = tokens.get(RgtResource.TOKEN_TYPE);
		boolean isValidtoken = true;
		try {
			UserAuthDetails userDetails = getUserDetailBean(userId, null);
			if (userDetails == null) {
				throw new IllegalArgumentException(TOKEN_EXPIRE);
			}

			com.letmecall.rgt.entity.socialmedia.SocialUser socialUser = socialUserDao
					.findSocialUserByUserName(userDetails.getEmailAddress(), tokenType);
			if (socialUser == null || socialUser.getAccessToken() == null) {
				throw new IllegalArgumentException("Could not find access token in DB");
			}

			String accessToken = socialUser.getAccessToken();
			Date currentTime = new Date();
			Date fbTokenExpiry = new Date(socialUser.getExpireTime() * 1000);

			Jws<Claims> claims = Jwts.parser().setSigningKey(accessToken).parseClaimsJws(token);

			if (logger.isDebugEnabled()) {
				logger.debug("TOKEN Token ID={},Token  Subject={},Token  Issuer={},Token  Expiration{}",
						claims.getBody().getId(), claims.getBody().getSubject(), claims.getBody().getIssuer(),
						claims.getBody().getExpiration());
			}

			Date tokenCreationTime = claims.getBody().getIssuedAt();
			Date tokenExpireTime = claims.getBody().getExpiration();
			if (tokenExpireTime.before(currentTime) || fbTokenExpiry.before(currentTime)) {
				logger.info("userName={} token expired with Claims Expiration ={}, FB Token Expiration ={}",
						claims.getBody().getSubject(), tokenExpireTime, fbTokenExpiry);
				isValidtoken = false;
			}
			if (socialUser.getLogoutTime() != null) {
				Date fbLogoutTime = new Date(socialUser.getLogoutTime() * 1000);
				if (tokenCreationTime.before(fbLogoutTime)) {
					logger.info("userName={} old token passed with Claims issued Time ={}, FB Token Expiration ={}",
							claims.getBody().getSubject(), tokenExpireTime, fbTokenExpiry);
					isValidtoken = false;
				}

			}

		} catch (ExpiredJwtException | IllegalArgumentException e) {
			buildExeption(e);
		}
		return isValidtoken;
	}

	private void buildExeption(Throwable ercepiton) {
		logger.error("Exception  : errormsg={}", ercepiton.getMessage());
		throw new JwtException("Invalid token passed");
	}

	/**
	 * 
	 * @param userName
	 * @param token
	 * @return getAuthentication
	 * 
	 */

	public Authentication getAuthentication(String lmcToken) {
		logger.info("getAuthentication :token ={}", lmcToken);
		Map<String, String> tokens = getTokenDetails(lmcToken);
		String id = tokens.get(RgtResource.TOKEN_ID);
		String token = tokens.get(RgtResource.TOKEN);
		String tokenType = tokens.get(RgtResource.TOKEN_TYPE);
		Long userId = Long.parseLong(id);

		String password = null;
		UserAuthDetails userDetails = getUserDetailBean(userId, null);
		if (userDetails == null) {
			throw new IllegalArgumentException(TOKEN_EXPIRE);
		}

		if (tokenType != null) {
			com.letmecall.rgt.entity.socialmedia.SocialUser socialUser = socialUserDao
					.findSocialUserByUserName(userDetails.getEmailAddress(), tokenType);
			if (socialUser == null || socialUser.getAccessToken() == null) {
				throw new IllegalArgumentException("Could not find access token in DB");
			}
			password = socialUser.getAccessToken();
		} else {
			password = userDetails.getPassword();
		}

		Jws<Claims> claims = Jwts.parserBuilder()
			    .setSigningKey(password.getBytes(StandardCharsets.UTF_8))
			    .build()
			    .parseClaimsJws(token);
		@SuppressWarnings("unchecked")
		List<String> roles = claims.getBody().get(CLAIMS_ROLES, List.class);

		List<GrantedAuthority> authRoles = new ArrayList<>();
		for (String role : roles) {
			GrantedAuthority authRole = new SimpleGrantedAuthority(role);
			authRoles.add(authRole);
		}
		TokenData tokenData = new TokenData(userDetails.getEmailAddress(), userDetails.getUserId().toString());
		return new JWTAuthenticationToken(tokenData, authRoles);
	}

	private Map<String, String> getTokenDetails(String lmcToken) {
		Optional<Map<String, String>> optional = Optional.ofNullable(splitToken(lmcToken))
				.filter(map -> map.size() > 0);
		Map<String, String> tokens = new HashMap<>();
		if (optional.isPresent()) {
			tokens = optional.get();
		}
		return tokens;
	}

	private static Jws<Claims> getJwtsClaims(String token, String password) {
		Optional<Map<String, String>> optional = Optional.ofNullable(splitToken(token)).filter(map -> map.size() > 0);
		String lmcToken = null;
		if (optional.isPresent()) {
			lmcToken = optional.get().get(RgtResource.TOKEN);
		}

		String secureKey = null;
		if (password != null && password.trim().length() > 0) {
			secureKey = password;
		} else {
			secureKey = SecurityContextHolder.getContext().getAuthentication().getCredentials().toString();
		}
		return Jwts.parser().setSigningKey(secureKey).parseClaimsJws(lmcToken);
	}

	public static Map<String, String> splitToken(String token) {
		Map<String, String> tokens = Splitter.on(",").withKeyValueSeparator("=").split(token);
		if (tokens != null && (tokens.get(RgtResource.TOKEN_ID) == null || tokens.get(RgtResource.TOKEN) == null)) {
			throw new MalformedJwtException("Invalid Token format");
		}
		return tokens;
	}

	public static Date getIssuedAt(String token) {
		logger.info("getIssuedAt :token ={}", token);
		return getJwtsClaims(token, null).getBody().getIssuedAt();
	}

	public boolean apiAccessForSpecifiedRole(List<String> role, String apiName, String requestMethod) {
		Boolean status = null;
		if (Boolean.TRUE.equals(Boolean.valueOf(apiAccessBasedRole))) {
			if (role.contains(Role.ROLE_GLOBAL_ADMIN.name())) {
				status = true;
			} else {
				status = roleBasedApiAccessRepository.fetchRoleBaseApiAcces(role, apiName, requestMethod);
			}
		} else {
			status = true;
		}
		return status;
	}

	public UserAuthDetails getUserDetailBean(Long userId, String userName) {
		logger.debug("getUserDetailBean: userId={},userName={}", userId, userName);
		UserAuthDetails userAuthDetails = new UserAuthDetails();
		userAuthDetails.setUserId(userId);
		userAuthDetails.setEmailAddress(userName);
		return userAuthDetailsServcie.fetchUserAuthDetails(userAuthDetails);
	}

}
