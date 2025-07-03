//package com.letmecall.rgt.config.security;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.security.authentication.AuthenticationManager;
//import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.config.annotation.web.builders.WebSecurity;
//import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
//import org.springframework.security.config.http.SessionCreationPolicy;
//import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
//import org.springframework.security.crypto.password.PasswordEncoder;
//
//import com.letmecall.rgt.config.security.oauth2.CustomOAuth2UserService;
//import com.letmecall.rgt.config.security.oauth2.OAuth2AuthenticationFailureHandler;
//import com.letmecall.rgt.config.security.oauth2.OAuth2LoginSuccessHandler;
//
//
//@Configuration
//public class SecurityConfiguration extends WebSecurityConfigurerAdapter {
//
//	private static final String[] angular = { "/assets", "/assets/**", "/assets/Images", "/assets/Images/**", "/*.png",
//			"/*.css", "/*.js", "/*.js.map", "/*.woff2", "/favicon.ico", "/actuator/**", "/images/**",
//			"/api/states/active", "/api/serviceProviderXmap/cities/*",
//			"/api/serviceProviderMember/serviceProviderXmap/*", "/api/cities/states/*","/api/contactus" };
//
//	private static final String[] swagger = { "/swagger-ui/index.html", "/webjars", "/webjars/springfox-swagger-ui/**",
//			"/swagger-resources/**", "/v2/api-docs", "/swagger-ui/**" };
//
//
//	@Autowired
//	private CustomOAuth2UserService oauth2UserService;
//	
//	@Autowired
//	private OAuth2LoginSuccessHandler oauthLoginSuccessHandler;
//	
//	@Autowired
//	private OAuth2AuthenticationFailureHandler oAuth2AuthenticationFailureHandler;
//	
//	@Autowired
//	private RestTokenProvider restTokenProvider;
//
//	@Autowired
//	private CustomAuthenticationProvider customAuthenticationProvider;
//
////	@Bean
////	public AuthenticationManager authenticationManagerBean() throws Exception {
////		return super.authenticationManagerBean();
////	}
//
//	public void configure(AuthenticationManagerBuilder authBuilder) throws Exception {
//		authBuilder.authenticationProvider(customAuthenticationProvider);
//	}
//
//	
//	@SuppressWarnings({ "deprecation", "removal" })
//	protected void configure(HttpSecurity http) throws Exception {
//		http.cors().and().csrf().disable().sessionManagement()
//		.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
//		.and().authorizeRequests().requestMatchers("/**")
//		.permitAll()
//		.anyRequest().authenticated()
//		.and().apply(new JwtConfigurer(restTokenProvider))
//		.and().oauth2Login().loginPage("/login")
//		.userInfoEndpoint()
//		.userService(oauth2UserService)
//		.and()
//		.successHandler(oauthLoginSuccessHandler)
//		.failureHandler(oAuth2AuthenticationFailureHandler)
//		.and()
//		.exceptionHandling().accessDeniedPage("/403");
//	}
//
//	@Bean
//	public PasswordEncoder passwordEncoder() {
//		return new BCryptPasswordEncoder();
//	}
//
//	public void configure(WebSecurity web) throws Exception {
//		// Filters will not get executed for the resources
//		web.ignoring()
//				.requestMatchers("/", "/resources/**", "/static/**", "/public/**", "/webui/**", "/h2-console/**", "/*.html",
//						"/**/*.html", "/**/*.css", "/**/*.js", "/**/*.png", "/**/*.jpg", "/**/*.gif", "/**/*.svg",
//						"/**/*.ico", "/**/*.ttf", "/**/*.woff", "/**/*.otf")
//				.requestMatchers(angular).requestMatchers(swagger);
//	}
//}
