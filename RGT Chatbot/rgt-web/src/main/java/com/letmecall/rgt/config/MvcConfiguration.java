package com.letmecall.rgt.config;

import java.io.IOException;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.resource.PathResourceResolver;

@Configuration
public class MvcConfiguration implements WebMvcConfigurer {

	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		String url = "file:/images/";
		registry.addResourceHandler("/images/**").addResourceLocations(url).setCachePeriod(3600);

		registry.addResourceHandler("/swagger-ui.html").addResourceLocations("").resourceChain(false);
		
		registry.addResourceHandler("/css/**").addResourceLocations("classpath:/static/css").resourceChain(true);

		registry.addResourceHandler("/**").addResourceLocations("classpath:/static/", "classpath:/public/").resourceChain(true)
				.addResolver(new PathResourceResolver() {
					@Override
					protected Resource getResource(String resourcePath, Resource location) throws IOException {
						Resource requestedResource = location.createRelative(resourcePath);

						return requestedResource.exists() && requestedResource.isReadable() ? requestedResource
								: new ClassPathResource("/public/index.html");
					}
				});

	}

	@Bean
	public WebMvcConfigurer corsConfigurer() {
		return new WebMvcConfigurer() {
			@Override
			public void addCorsMappings(CorsRegistry registry) {
				registry.addMapping("/api/**").allowedMethods("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS");
			}

		};
	}
	
	@Override
	public void addViewControllers(ViewControllerRegistry registry) {
		registry.addViewController("/403").setViewName("403");
		registry.addViewController("/login").setViewName("login");
	}

}