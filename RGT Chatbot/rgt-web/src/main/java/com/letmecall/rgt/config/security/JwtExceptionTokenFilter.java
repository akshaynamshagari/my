package com.letmecall.rgt.config.security;

import java.io.IOException;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.filter.GenericFilterBean;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.letmecall.rgt.model.Response;
import com.letmecall.rgt.model.StatusType;

import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.MalformedJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletResponse;

public class JwtExceptionTokenFilter extends GenericFilterBean {

	private static ObjectMapper objectMapper = new ObjectMapper();

	public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain)
			throws IOException, ServletException {
		try {
			filterChain.doFilter(request, response);
		} catch (RuntimeException ex) {
			String message = "";
			if (ex instanceof TokenExpireException) {
				message = "Invalid token or token is expire.";
			} else if (ex instanceof TokenNotFoundException) {
				message = "Invalid header or token not passed.";
			} else if (ex instanceof BadCredentialsException) {
				message = ex.getMessage();
			} else if (ex instanceof JwtException || ex instanceof IllegalArgumentException
					|| ex instanceof MalformedJwtException) {
				message = "Invalid token passed";
			} else if (ex instanceof ApiAccessException) {
				message = "Specific api access is not available for Your account";
			}
			Response<String> result = Response.buildResponse("AUTH_ERROR", StatusType.ERROR, 403, message);
			response.setContentType(MediaType.APPLICATION_JSON_VALUE);
			response.getWriter().write(objectMapper.writeValueAsString(result));
			HttpServletResponse hsr = (HttpServletResponse) response;
			hsr.setStatus(HttpStatus.FORBIDDEN.value());
		}
	}

}
