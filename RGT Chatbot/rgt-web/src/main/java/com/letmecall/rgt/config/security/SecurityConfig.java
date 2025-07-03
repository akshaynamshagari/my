package com.letmecall.rgt.config.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;


@Configuration
@EnableWebSecurity
public class SecurityConfig {

	private static final String[] angular = { "/assets", "/assets/**", "/assets/Images", "/assets/Images/**", "/*.png",
			"/*.css", "/*.js", "/*.js.map", "/*.woff2", "/favicon.ico", "/actuator/**", "/images/**",
			"/api/states/active", "/api/serviceProviderXmap/cities/*",
			"/api/serviceProviderMember/serviceProviderXmap/*", "/api/cities/states/*", "/api/contactus", 
			"/api/rgt/chatbot/static/uiget", "/api/rgt/chatbot/uiget", "/api/rgt/chatbot/chat", "/favicon.ico", 
			"/api/rgt/chatbot/**", "/css/**", "/js/**"  };

	private static final String[] swagger = { "/swagger-ui/index.html", "/webjars", "/webjars/springfox-swagger-ui/**",
			"/swagger-resources/**", "/v2/api-docs", "/swagger-ui/**", "/css/**", "/js/**" };

	@Autowired
	private RestTokenProvider restTokenProvider;

	@Autowired
	private CustomAuthenticationProvider customAuthenticationProvider;

	public void configure(AuthenticationManagerBuilder authBuilder) throws Exception {
		authBuilder.authenticationProvider(customAuthenticationProvider);
	}

	@Bean
	public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
		return authConfig.getAuthenticationManager();
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

		http.headers(headers -> headers.frameOptions().disable())
.cors(Customizer.withDefaults()).csrf(csrf -> csrf.disable())
				.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
				.authorizeHttpRequests(auth -> auth.requestMatchers("/**").permitAll().anyRequest().authenticated());

		http.authenticationProvider(customAuthenticationProvider);

		JwtTokenFilter jwtTokenFilter = new JwtTokenFilter(restTokenProvider);
		http.addFilterBefore(jwtTokenFilter, UsernamePasswordAuthenticationFilter.class);

		JwtExceptionTokenFilter jwtExceptionTokenFilter = new JwtExceptionTokenFilter();
		http.addFilterBefore(jwtExceptionTokenFilter, JwtTokenFilter.class);

		return http.build();
	}

	@Bean
	public WebSecurityCustomizer webSecurityCustomizer() {
		return (web) -> web.ignoring().requestMatchers(angular).requestMatchers(swagger);
	}

}