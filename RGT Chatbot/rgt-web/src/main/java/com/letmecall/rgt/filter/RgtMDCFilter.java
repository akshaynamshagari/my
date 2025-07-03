package com.letmecall.rgt.filter;

import java.util.UUID;

import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import com.letmecall.rgt.config.RgtMDCFilterConfiguration;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * A servlet that adds a key to the Mapped Diagnostic Context (MDC) to each
 * request so you can print a unique id in the logg messages of each request. It
 * also add the key as a header in the response so the caller of the request can
 * provide you the id to browse the logs.
 *
 * @see com.letmecall.rgt.config.RgtMDCFilterConfiguration
 **/
@Data
@EqualsAndHashCode(callSuper = false)
@Component
public class RgtMDCFilter extends OncePerRequestFilter {

	private final String responseHeader;
	private final String mdcTokenKey;
	private final String mdcClientIpKey;
	private final String requestHeader;

	public RgtMDCFilter() {
		responseHeader = RgtMDCFilterConfiguration.DEFAULT_RESPONSE_TOKEN_HEADER;
		mdcTokenKey = RgtMDCFilterConfiguration.DEFAULT_MDC_UUID_TOKEN_KEY;
		mdcClientIpKey = RgtMDCFilterConfiguration.DEFAULT_MDC_CLIENT_IP_KEY;
		requestHeader = null;
	}

	public RgtMDCFilter(final String responseHeader, final String mdcTokenKey, final String mdcClientIPKey,
			final String requestHeader) {
		this.responseHeader = responseHeader;
		this.mdcTokenKey = mdcTokenKey;
		this.mdcClientIpKey = mdcClientIPKey;
		this.requestHeader = requestHeader;
	}

	@Override
	protected void doFilterInternal(final HttpServletRequest request, final HttpServletResponse response,
			final FilterChain chain) throws java.io.IOException, ServletException {
		try {
			final String token = extractToken(request);
			final String clientIP = extractClientIP(request);
			MDC.put(mdcClientIpKey, clientIP);
			MDC.put(mdcTokenKey, token);
			if (StringUtils.hasText(responseHeader)) {
				response.addHeader(responseHeader, token);
			}
			chain.doFilter(request, response);
		} finally {
			MDC.remove(mdcTokenKey);
			MDC.remove(mdcClientIpKey);
		}
	}

	private String extractToken(final HttpServletRequest request) {
		final String token;
		if (StringUtils.hasText(requestHeader) && StringUtils.hasText(request.getHeader(requestHeader))) {
			token = request.getHeader(requestHeader);
		} else {
			token = UUID.randomUUID().toString().toUpperCase().replace("-", "");
		}
		return token;
	}

	private String extractClientIP(final HttpServletRequest request) {
		final String clientIP;
		if (request.getHeader("X-Forwarded-For") != null) {
			clientIP = request.getHeader("X-Forwarded-For").split(",")[0];
		} else {
			clientIP = request.getRemoteAddr();
		}
		return clientIP;
	}

	@Override
	protected boolean isAsyncDispatch(final HttpServletRequest request) {
		return false;
	}

	@Override
	protected boolean shouldNotFilterErrorDispatch() {
		return false;
	}
}
