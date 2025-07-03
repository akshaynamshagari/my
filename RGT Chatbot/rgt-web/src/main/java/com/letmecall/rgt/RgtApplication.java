package com.letmecall.rgt;

import org.springframework.boot.Banner;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.io.FileSystemResource;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication()
@ComponentScan("com.letmecall")
@EntityScan("com.letmecall")
@EnableJpaRepositories(basePackages = "com.letmecall")
@EnableJpaAuditing(auditorAwareRef = "auditorAware")
@EnableAsync
public class RgtApplication extends SpringBootServletInitializer {

	public static void main(String[] args) {
		new SpringApplicationBuilder().sources(RgtApplication.class).bannerMode(Banner.Mode.OFF).run(args);
	}
	
	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
		return application.sources(RgtApplication.class);
	}
	
	@Bean
    public PropertySourcesPlaceholderConfigurer propertyPlaceholderConfigurer() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(System.getenv("chatbot_config_home"));
        stringBuilder.append("/chatbot.properties");
        PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer = new PropertySourcesPlaceholderConfigurer();
        propertySourcesPlaceholderConfigurer.setLocation(new FileSystemResource(stringBuilder.toString()));
        propertySourcesPlaceholderConfigurer.setIgnoreResourceNotFound(true);
        propertySourcesPlaceholderConfigurer.setLocalOverride(true);
        return propertySourcesPlaceholderConfigurer;
    }

}
