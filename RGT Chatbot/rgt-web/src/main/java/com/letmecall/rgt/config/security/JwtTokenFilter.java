package com.letmecall.rgt.config.security;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.GenericFilterBean;

public class JwtTokenFilter extends GenericFilterBean {

	private static final String[] url = { "/", "/api/auth/login", "/api/auth/signup", "/api/auth/logout",
			"/api/auth/signupManualProcess", "/api/auth/forgotpassword/otp", "/api/auth/otp/verify",
			"/api/auth/passwordreset", "/api/auth/login/otp", "/api/auth/login/otp/verify", "/api/rgt/chatbot/chat", "/favicon.ico" };

	private static final List<String> listUrl = Arrays.asList(url);

	private RestTokenProvider restTokenService;

	public JwtTokenFilter(RestTokenProvider restTokenService) {
		this.restTokenService = restTokenService;
	}

	public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain)
			throws IOException, ServletException {
		HttpServletRequest httpRequest = (HttpServletRequest) request;
		String token = httpRequest.getHeader("Authorization");
		String requestUri = httpRequest.getRequestURI();
		boolean angularRoutingURL = requestUri.contains("/api/");
		if (token != null) {
			Boolean isValidtoken = restTokenService.authenticateUserByToken(token);
			if (isValidtoken != null && isValidtoken) {
				Authentication authentication = restTokenService.getAuthentication(token);
				validateApiAcces(requestUri, authentication, httpRequest.getMethod());
				SecurityContextHolder.getContext().setAuthentication(authentication);
			} else {
				throw new TokenExpireException("Invalid token or token is expired,Please login again.");
			}
		} else {
			if (angularRoutingURL && !(listUrl.contains(requestUri) || requestUri.endsWith(".js")
					|| requestUri.endsWith("error") || requestUri.endsWith("swagger-ui/index.html")
					|| requestUri.endsWith("api-docs") || requestUri.endsWith("contactus"))) {
				throw new TokenNotFoundException("Invalid header or token not passed");
			}
		}
		filterChain.doFilter(request, response);
	}

	private void validateApiAcces(String requestUri, Authentication authentication, String method) {
		List<String> roles = new ArrayList<>();
		for (GrantedAuthority role : authentication.getAuthorities()) {
			roles.add(role.getAuthority());
		}
		Boolean isroleBasedApiAcces = restTokenService.apiAccessForSpecificRole(roles, requestUri, method);
		if (isroleBasedApiAcces != null && !isroleBasedApiAcces) {
			throw new ApiAccessException("Specific api access is not available for your account");
		}
	}

}