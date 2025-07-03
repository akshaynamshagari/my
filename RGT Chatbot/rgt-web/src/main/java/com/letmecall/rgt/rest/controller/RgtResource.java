package com.letmecall.rgt.rest.controller;

import org.springframework.http.MediaType;

public class RgtResource {

	private RgtResource() {
		throw new IllegalStateException("NextDoorResource class");
	}

	public static final String ROLE_USER = "ROLE_USER";

	public static final String TOKEN = "rgtToken";

	public static final String TOKEN_ID = "id";

	public static final String TOKEN_TYPE = "rgtAuthType";

	public static final String HOME_BASE_URL = "/";

	public static final String SWAGGER_BASE_URL = "/swagger-ui/index.html";

	public static final String SWAGGER_DOC_URL = "/v2/api-docs";

	public static final String CONSUMES = MediaType.APPLICATION_JSON_VALUE;

	public static final String PRODUCES = MediaType.APPLICATION_JSON_VALUE;

	public static final String PRODUCES_MULTIPART_FORM_DATA = MediaType.MULTIPART_FORM_DATA_VALUE;

	public static final String AUTH_BASE_URL = "/api/auth";

	public static final String LOGIN_URL = "/login";

	public static final String SIGNUP_URL = "/signup";

	public static final String SIGNUP_URL_MANUAL_PROCESS = "/signupManualProcess";

	public static final String LOGOUT_URL = "/logout";

	public static final String AUTHORIZATION_HEADER = "Authorization";

	public static final String ROLES_BASE_URL = "/api/roles";

	public static final String ROLES_BY_ID = "/{rolesId}";

	public static final String USER_ROLES_BASE_URL = "/api/userroles";

	public static final String USER_ROLES_BY_ID = "/{userRoleId}";

	public static final String FORGOT_PASSWORD_OTP_URL = "/forgotpassword/otp";

	public static final String OTP_VERIFY_URL = "/otp/verify";

	public static final String PASSWORD_RESET_URL = "/passwordreset";

	public static final String LOGIN_WITH_OTP_URL = "/login/otp";

	public static final String LOGIN_OTP_VERIFY_URL = "/login/otp/verify";

	public static final String MEMBER_INVITE_BASE_URL = "/api/memberinvite";

	public static final String JOBEDETAILS_BASE_URL = "/api/jobdetails";

	public static final String JOBEDETAILS_BY_ID = "/{rolesId}";

	public static final String CUSTOMERINFO_BASE_URL = "/api/customers";

	public static final String CUSTOMER_BY_ID = "/{customerId}";

	public static final String ENABLE_BY_CUSTOMER = "/{customerId}/enable";

	public static final String CUSTOMERATTRIBUTE_BASE_URL = "/api/customerattribute";

	public static final String CUSTOMERATTRIBUTE_BY_CUSTOMER_ID = "/customers/{customerId}";

	public static final String CUSTOMERATTRIBUTE_BY_ID = "/{customerattributeId}";

	public static final String APIDETAILS_BASE_URL = "/api/resources";

	public static final String APIDETAILS_BY_ID = "/{resourcesId}";

	public static final String ROLE_BASED_APIDETAILS_BASE_URL = "/api/rolebasedapiaccess";

	public static final String ROLE_BASED_APIDETAILS_BY_ID = "/{roleBasedApiAccessId}";

	public static final String COUNTRY_BASE_URL = "/api/countrys";

	public static final String COUNTRY_BY_ID = "/{countryId}";

	public static final String COUNTRY_BY_SEARCH = "/search";

	public static final String COUNTRY_BY_ATTACHMENT_UPLOAD_URL = "/attachments/excel/upload";

	public static final String STATE_BASE_URL = "/api/states";

	public static final String STATE_BY_ID = "/{stateId}";

	public static final String STATE_BY_SEARCH = "/search";

	public static final String STATE_BY_ATTACHMENT_UPLOAD_URL = "/attachments/excel/upload";

	public static final String STATE_BY_COUNTRYID = "country/{countryId}";

	public static final String CITY_BASE_URL = "/api/cities";

	public static final String CITY_BY_ID = "/{cityId}";

	public static final String CITY_BY_SEARCH = "/searchCity";

	public static final String STATE_BY_ACTIVITE_CITIES = "/states/{stateId}";

	public static final String CITY_BY_ATTACHMENT_UPLOAD_URL = "/attachments/excel/upload";

	public static final String CONTACTUS_BASE_URL = "/api/contactus";
	
	public static final String CHAT = "/chat";
	
	public static final String CHATBOT_BASE_URL = "/api/rgt/chatbot";
	
	public static final String ADD_INTENT = "/add/intent";
	
	public static final String UPDATE_INTENT = "/update/intent";
	
	public static final String DELETE_INTENT = "/delete/intent";
	
	public static final String FETCH_INTENT = "/get/intent";
	
	public static final String ADD_ENTITIES = "/add/entites";
	
	public static final String GET_ENTITIES = "/get/entites";
	
	public static final String CHATBOT_UI_CHAT = "/ui";
}