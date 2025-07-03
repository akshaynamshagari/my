package com.letmecall.rgt.config;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.letmecall.rgt.filter.RgtMDCFilter;

import lombok.Data;

/**
 * The class that configures a servlet that adds a key to the Mapped Diagnostic
 * Context (MDC) to each request so you can print a unique id in the logg
 * messages of each request. It also add the key as a header in the response so
 * the caller of the request can provide you the id to browse the logs. Set the
 * response header name to null/blank if you want the response to NOT include
 * such header.
 * 
 * If you provide a request header name, the filter will check first if the
 * request contains a header with that name and will use the ID it provides.
 * This is useful if your application chain has already assigned an id to the
 * "transaction". (Microservices, apps behind a proxy/gateway service...)
 * 
 * The MDC key and the header names are configurable.
 * 
 * Here's a configuration sample with the default values:
 * 
 * <pre>
 * summer:
 *   slf4jfilter:
 *     response_header: RlogId
 *     mdc_token_key: RgtMDCFilter.UUID
 *     mdc_client_ip_key: RgtMDCFilter.ClientIP
 *     request_header:
 * </pre>
 **/
@Data
@Configuration
public class RgtMDCFilterConfiguration {

	public static final String DEFAULT_RESPONSE_TOKEN_HEADER = "RlogId";
	public static final String DEFAULT_MDC_UUID_TOKEN_KEY = "RgtMDCFilter.UUID";
	public static final String DEFAULT_MDC_CLIENT_IP_KEY = "RgtMDCFilter.ClientIP";

	private String responseHeader = DEFAULT_RESPONSE_TOKEN_HEADER;
	private String mdcTokenKey = DEFAULT_MDC_UUID_TOKEN_KEY;
	private String mdcClientIpKey = DEFAULT_MDC_CLIENT_IP_KEY;
	private String requestHeader = null;

	@Bean
	public FilterRegistrationBean<RgtMDCFilter> servletRegistrationBean() {
		final FilterRegistrationBean<RgtMDCFilter> registrationBean = new FilterRegistrationBean<>();
		final RgtMDCFilter log4jMDCFilterFilter = new RgtMDCFilter(responseHeader, mdcTokenKey, mdcClientIpKey,
				requestHeader);
		registrationBean.setFilter(log4jMDCFilterFilter);
		registrationBean.setOrder(2);
		return registrationBean;
	}
}