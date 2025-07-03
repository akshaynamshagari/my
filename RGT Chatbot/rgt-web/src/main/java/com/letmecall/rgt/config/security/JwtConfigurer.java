
package com.letmecall.rgt.config.security;

import org.springframework.security.config.annotation.SecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.stereotype.Component;

@Component
public class JwtConfigurer extends SecurityConfigurerAdapter<DefaultSecurityFilterChain, HttpSecurity> {

	private RestTokenProvider restTokenProvider;

	public JwtConfigurer(RestTokenProvider restTokenProvider) {
		this.restTokenProvider = restTokenProvider;
	}

	@Override
	public void configure(HttpSecurity httpSecurity) throws Exception {
		JwtTokenFilter jwtTokenFilter = new JwtTokenFilter(restTokenProvider);
		httpSecurity.addFilterBefore(jwtTokenFilter, UsernamePasswordAuthenticationFilter.class);

		JwtExceptionTokenFilter jwtExceptionTokenFilter = new JwtExceptionTokenFilter();
		httpSecurity.addFilterBefore(jwtExceptionTokenFilter, JwtTokenFilter.class);
	}
}
