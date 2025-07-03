package com.letmecall.rgt.config;

import java.util.ArrayList;
import java.util.Arrays;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;

import com.letmecall.rgt.rest.controller.RgtResource;

import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.builders.RequestParameterBuilder;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.service.ParameterType;
import springfox.documentation.service.RequestParameter;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

//@Configuration
//@EnableSwagger2
public class SwaggerConfig {

	@Value("${rgt.implementation.environment}")
	private String enviroment;

	@Bean
	public Docket productApi() {

		Boolean swaggerEnable = !enviroment.equalsIgnoreCase("prod");
		// Adding Header
		return new Docket(DocumentationType.SWAGGER_2).select()
				.apis(RequestHandlerSelectors.basePackage("com.letmecall.rgt.rest")).build().apiInfo(metaData())
				.globalRequestParameters(Arrays.asList(createRequestParameter(RgtResource.AUTHORIZATION_HEADER, false)))
				.enable(swaggerEnable);
	}

	private ApiInfo metaData() {
		Contact defaultContact = new Contact("RGT", "http://ratnaglobaltech.com/", "contact@ratnaglobaltech.com");
		return new ApiInfo("RGT  REST API", "Api Documentation", "1.0", "Terms of service", defaultContact, "RGT 2.0",
				"http://ratnaglobaltech.com/", new ArrayList<>());
	}

	private RequestParameter createRequestParameter(String headerName, boolean required) {
		return new RequestParameterBuilder().name(headerName).required(required)
				//.query(q -> q.model(modelSpecificationBuilder -> modelSpecificationBuilder.scalarModel(ScalarType.STRING)))
				.in(ParameterType.HEADER).build();
	}

}
