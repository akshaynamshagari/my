package com.letmecall.rgt.rest.auth.controller;

import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.letmecall.rgt.config.security.AuthenticationManualRequest;
import com.letmecall.rgt.config.security.AuthenticationRequest;
import com.letmecall.rgt.config.security.CustomAuthenticationProvider;
import com.letmecall.rgt.config.security.RestTokenProvider;
import com.letmecall.rgt.config.security.TokenNotFoundException;
import com.letmecall.rgt.domain.MemberInvite;
import com.letmecall.rgt.domain.UserAuthDetails;
import com.letmecall.rgt.domain.UserOtp;
import com.letmecall.rgt.model.Response;
import com.letmecall.rgt.model.StatusType;
import com.letmecall.rgt.model.TokenData;
import com.letmecall.rgt.rest.controller.RgtResource;
import com.letmecall.rgt.rest.helper.BaseRestUtill;
import com.letmecall.rgt.rest.helper.UserAuthDetailsHelper;
import com.letmecall.rgt.util.TokenUtils;

import io.jsonwebtoken.JwtException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;

@RestController
@RequestMapping(value = RgtResource.AUTH_BASE_URL)
public class AuthenticationController {

	private static final Logger logger = LoggerFactory.getLogger(AuthenticationController.class);

	private static final String USER_EXIST = "Your details are existing with us. Please login to search.";

	private static final String USER_IS_SIGNUP = "User Signup in successfully.";

	private static final String AUTH_ERROR = "AUTH_ERROR";

	private static final String USER_IS_NOT_EXIST = "Your details are not existing with us. Please signup to search.";

	private static final String USER_IS_NOT_EXIST_WITH_VERIFY = "User details doesnot exists.";

	private static final String USER_INVITATION_EXPIRED = "Invalid Link. Either the link is expired or already been used.";

	@Autowired
	private CustomAuthenticationProvider authenticationProvider;

	@Autowired
	private RestTokenProvider tokenService;

	@Autowired
	private UserAuthDetailsHelper userAuthDetailsHelper;

	@PostMapping(value = RgtResource.LOGIN_URL)
	public ResponseEntity<Response<UserAuthDetails>> authenticateUser(
			@Valid @RequestBody AuthenticationRequest authRequest, HttpServletResponse httpServletResponse) {
		logger.debug("authenticateUser : authenticateUser={}", authRequest);
		Response<UserAuthDetails> response = null;
		String userName = authRequest.getUserName();
		String password = authRequest.getPassword();
		Authentication authentication = null;
		try {
			authentication = authenticationProvider
					.authenticate(new UsernamePasswordAuthenticationToken(userName, password));
			if (authentication.isAuthenticated()) {
				SecurityContextHolder.getContext().setAuthentication(authentication);
				UserAuthDetails userDetails = userAuthDetailsHelper.fetchCompleteUserDetail(null,
						authRequest.getUserName());

				userAuthDetailsHelper.updateLastAccessTime(userDetails.getUserId(), userDetails.getEmailAddress());
				buildLoginOrSignupResponse(userDetails);
				response = Response.buildResponse(userDetails, StatusType.SUCCESS, HttpStatus.OK.value(),
						"User logged in successfully");
			} else {
				response = Response.buildResponse(null, StatusType.ERROR, HttpStatus.UNAUTHORIZED.value(),
						"Your details are Not existing with us. Please Signup to search.");
			}
		} catch (Exception ex) {
			response = buildLoginOrSignupErrorResposne(ex);
		}
		return setTokensResponse(response);
	}

	@PostMapping(value = RgtResource.LOGOUT_URL)
	public ResponseEntity<Response<String>> logoutUser(@RequestHeader(RgtResource.AUTHORIZATION_HEADER) String token) {
		logger.debug("logoutUser");
		Response<String> response = null;
		String userName = null;
		if (token != null) {
			Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
			TokenData tokenData = null;
			if (authentication.getPrincipal() instanceof TokenData) {
				tokenData = (TokenData) authentication.getPrincipal();
			}
			if (tokenData != null)
				userName = tokenData.getAttribute("userName");
			logger.debug("Logging out :: User={}", userName);
			Map<String, String> tokens = RestTokenProvider.splitToken(token);
			if (tokens.get(RgtResource.TOKEN_TYPE) != null) {
				userAuthDetailsHelper.saveLogOutTime(userName, tokens.get(RgtResource.TOKEN_TYPE));
				logger.debug("Customer Operation : Logout Customer={}", userName);
			} else {
				userAuthDetailsHelper.updateLogOutTime(userName);
				logger.debug("Customer Operation : Logout DB Customer={}", userName);
			}
			response = Response.buildResponse(null, StatusType.SUCCESS, 200, "User logout in successfully");
		} else {
			String message = "Invalid header or token not passed";
			response = Response.buildResponse(null, StatusType.ERROR, 403, message);
		}
		return new ResponseEntity<>(response, HttpStatus.valueOf(Integer.valueOf(response.getCode())));
	}

	@PostMapping(value = RgtResource.SIGNUP_URL_MANUAL_PROCESS)
	public ResponseEntity<Response<UserAuthDetails>> signupUserManualProcess(@RequestParam("uts") String uts,
			@Valid @RequestBody AuthenticationManualRequest authRequest, HttpServletRequest request) {
		logger.debug("signupUserManualProcess : authenticateUser={}", authRequest);
		Response<UserAuthDetails> response = null;
		String userName = authRequest.getUserName();
		String password = authRequest.getPassword();
		try {
			if (!authRequest.getPassword().equals(authRequest.getConfirmPassword())) {
				response = Response.buildResponse(null, StatusType.ERROR, HttpStatus.INTERNAL_SERVER_ERROR.value(),
						"Passwords do not match.Invalid user name or password!!!.");
			} else {
				Boolean isCustomerExist = userAuthDetailsHelper.isCustomerExist(userName);
				if (isCustomerExist != null && isCustomerExist) {
					logger.info("LoginActionController :: ExistingUser={} ", userName);
					response = Response.buildResponse(null, StatusType.ERROR, HttpStatus.INTERNAL_SERVER_ERROR.value(),
							USER_EXIST);
					response.setError(AUTH_ERROR);
				} else {
					uts = BaseRestUtill.decode(uts);

					MemberInvite memberInvite = userAuthDetailsHelper.validateMemberInviteKey(userName, uts);
					if (memberInvite == null) {
						response = Response.buildResponse(null, StatusType.ERROR,
								HttpStatus.INTERNAL_SERVER_ERROR.value(), USER_INVITATION_EXPIRED);
					} else {
						userAuthDetailsHelper.createUserDetails(authRequest, memberInvite);
						setSpringLoginSecurity(request, userName, password);
						UserAuthDetails userDetails = userAuthDetailsHelper.fetchCompleteUserDetail(null, userName);
						buildLoginOrSignupResponse(userDetails);
						response = Response.buildResponse(userDetails, StatusType.SUCCESS, HttpStatus.CREATED.value(),
								USER_IS_SIGNUP);
					}

				}
			}
		} catch (Exception ex) {
			response = buildLoginOrSignupErrorResposne(ex);
		}
		return setTokensResponse(response);
	}

	@PostMapping(value = RgtResource.FORGOT_PASSWORD_OTP_URL)
	public ResponseEntity<Response<String>> generateOTPForForGotPasswordUser(@Valid @RequestBody UserOtp userOtp) {
		logger.debug("generateOTPForForGotPasswordUser : userOtp={}", userOtp);
		Response<String> response = null;
		String userName = userOtp.getUserName();
		try {
			UserAuthDetails userAuthDetails = userAuthDetailsHelper.getUserDetailBean(null, userName);
			if (userAuthDetails == null) {
				response = Response.buildResponse(null, StatusType.ERROR, 500, USER_IS_NOT_EXIST);
			} else {
				userAuthDetailsHelper.generateOTPForForGotPassword(userOtp, userAuthDetails);
				response = Response.buildResponse(null, StatusType.SUCCESS, 200, "Otp genrated successfully");
			}
		} catch (Exception ex) {
			response = buildErrorResposne(ex);
		}
		return new ResponseEntity<>(response, HttpStatus.valueOf(Integer.valueOf(response.getCode())));
	}

	@PostMapping(value = RgtResource.OTP_VERIFY_URL)
	public ResponseEntity<Response<String>> verifyOTPForUser(@Valid @RequestBody UserOtp communityUserOtp) {
		logger.debug("verifyOTPForUser : communityUserOtp={}", communityUserOtp);
		Response<String> response = null;
		String userName = communityUserOtp.getUserName();
		try {
			if (StringUtils.isBlank(communityUserOtp.getOtpNumber())) {
				response = Response.buildResponse(null, StatusType.ERROR, 500, "Please provide valid OTP Number.");
			} else {
				UserAuthDetails userAuthDetails = userAuthDetailsHelper.getUserDetailBean(null, userName);
				if (userAuthDetails == null) {
					response = Response.buildResponse(null, StatusType.ERROR, 500, USER_IS_NOT_EXIST_WITH_VERIFY);
				} else {
					Boolean otpStatus = userAuthDetailsHelper.verifyOTPForUser(userAuthDetails.getUserId(),
							communityUserOtp.getOtpNumber());
					if (otpStatus != null && !otpStatus) {
						response = Response.buildResponse(null, StatusType.ERROR, 500,
								"Something went wrong! Please try again in some time.");
					} else {
						response = Response.buildResponse(null, StatusType.SUCCESS, 200, "Otp verify successfully");
					}
				}
			}
		} catch (Exception ex) {
			response = buildErrorResposne(ex);
		}
		return new ResponseEntity<>(response, HttpStatus.valueOf(Integer.valueOf(response.getCode())));
	}

	@PostMapping(value = RgtResource.PASSWORD_RESET_URL)
	public ResponseEntity<Response<String>> resetPassword(@Valid @RequestBody AuthenticationRequest authRequest) {
		logger.debug("resetPassword : authenticateUser={}", authRequest);
		Response<String> response = null;
		String userName = authRequest.getUserName();
		String password = authRequest.getPassword();
		try {
			UserAuthDetails communityUserAuthDetails = userAuthDetailsHelper.getUserDetailBean(null, userName);
			if (communityUserAuthDetails == null) {
				response = Response.buildResponse("", StatusType.ERROR, 500, USER_IS_NOT_EXIST_WITH_VERIFY);
			} else {
				userAuthDetailsHelper.resetPassword(communityUserAuthDetails.getUserId(), userName, password);
				response = Response.buildResponse("OK", StatusType.SUCCESS, 200,
						"Password reset is successfully by User.");
			}

		} catch (Exception ex) {
			logger.error("Exception Occured while executing service", ex);
			String message = ex.getMessage();
			response = Response.buildResponse(null, StatusType.ERROR, 403, message);
			response.setError(AUTH_ERROR);
		}
		return new ResponseEntity<>(response, HttpStatus.valueOf(Integer.valueOf(response.getCode())));
	}

	@PostMapping(value = RgtResource.LOGIN_WITH_OTP_URL)
	public ResponseEntity<Response<String>> generateOTPForLogggedUser(@Valid @RequestBody UserOtp userOtp) {
		logger.debug("generateOTPForUser : userOtp={}", userOtp);
		Response<String> response = null;
		String userName = userOtp.getUserName();
		try {
			UserAuthDetails userAuthDetails = userAuthDetailsHelper.getUserDetailBean(null, userName);
			if (userAuthDetails == null) {
				response = Response.buildResponse(null, StatusType.ERROR, HttpStatus.UNAUTHORIZED.value(),
						"Your details are Not existing with us. Please Signup to search.");
			} else {
				userAuthDetailsHelper.generateOTPForUserLogin(userOtp, userAuthDetails);
				response = Response.buildResponse(null, StatusType.SUCCESS, 200, "Otp genrated successfully");
			}
		} catch (Exception ex) {
			response = buildErrorResposne(ex);
		}
		return new ResponseEntity<>(response, HttpStatus.valueOf(Integer.valueOf(response.getCode())));
	}

	@PostMapping(value = RgtResource.LOGIN_OTP_VERIFY_URL)
	public ResponseEntity<Response<UserAuthDetails>> verifyOTPForloginUser(
			@Valid @RequestBody UserOtp communityUserOtp) {
		logger.debug("verifyOTPForloginUser : communityUserOtp={}", communityUserOtp);
		Response<UserAuthDetails> response = null;
		String userName = communityUserOtp.getUserName();
		try {
			if (StringUtils.isBlank(communityUserOtp.getOtpNumber())) {
				response = Response.buildResponse(null, StatusType.ERROR, 500, "Please provide valid OTP Number.");
			} else {
				UserAuthDetails userAuthDetails = userAuthDetailsHelper.fetchCompleteUserDetail(null, userName);
				if (userAuthDetails == null) {
					response = Response.buildResponse(null, StatusType.ERROR, HttpStatus.EXPECTATION_FAILED.value(),
							USER_IS_NOT_EXIST_WITH_VERIFY);
				} else {
					Boolean otpStatus = userAuthDetailsHelper.verifyOTPForUser(userAuthDetails.getUserId(),
							communityUserOtp.getOtpNumber());
					if (otpStatus != null && !otpStatus) {
						response = Response.buildResponse(null, StatusType.ERROR, HttpStatus.EXPECTATION_FAILED.value(),
								"Something went wrong! Please try again in some time..");
					} else {
						buildLoginOrSignupResponse(userAuthDetails);
						Authentication authentication = tokenService.getAuthentication(userAuthDetails.getToken());
						SecurityContextHolder.getContext().setAuthentication(authentication);
						response = Response.buildResponse(userAuthDetails, StatusType.SUCCESS, 200,
								"Otp verify successfully");
					}
				}
			}

		} catch (Exception ex) {
			prepareErrorLog(ex);
			response = Response.buildResponse(null, StatusType.ERROR, HttpStatus.EXPECTATION_FAILED.value(),
					ex.getMessage());
		}
		return setTokensResponse(response);
	}

	void prepareErrorLog(Exception ex) {
		logger.error("Exception Occured while executing service", ex);
	}

	private ResponseEntity<Response<UserAuthDetails>> setTokensResponse(Response<UserAuthDetails> response) {
		logger.debug("setTokensResponse:INVOKED");
		HttpHeaders headers = null;
		if (response.getStatusType().toString().equals(StatusType.SUCCESS.toString())) {
			headers = new HttpHeaders();
			headers.add(RgtResource.AUTHORIZATION_HEADER, response.getResponse().getToken());
		}
		return new ResponseEntity<>(response, headers, HttpStatus.valueOf(Integer.valueOf(response.getCode())));
	}

	private Response<UserAuthDetails> buildLoginOrSignupErrorResposne(Exception ex) {
		prepareErrorLog(ex);
		Response<UserAuthDetails> response;
		String message = "";
		if (ex instanceof TokenNotFoundException) {
			message = "Invalid header or token not passed";
		} else if (ex instanceof BadCredentialsException) {
			message = ex.getMessage();
		} else if (ex instanceof JwtException || ex instanceof IllegalArgumentException) {
			message = "Invalid token passed";
		}
		response = Response.buildResponse(null, StatusType.ERROR, HttpStatus.FORBIDDEN.value(), message);
		response.setError(AUTH_ERROR);
		return response;
	}

	private void buildLoginOrSignupResponse(UserAuthDetails userDetails) {
		String token = tokenService.createTokenForUser(userDetails, userDetails.getRoles());
		String generaetdToken = buildToken(userDetails, token);
		logger.warn(" Token Genrated: UserName={}, Token={}", userDetails.getEmailAddress(), generaetdToken);
		userDetails.setToken(generaetdToken);
		userDetails.setPassword("");
		if (userDetails.getRoles().isEmpty()) {
			userDetails.getRoles().add("ROLE_USER");
		}
	}

	private String buildToken(UserAuthDetails userDetails, String token) {
		return TokenUtils.buildToken(userDetails, token);
	}

	private void setSpringLoginSecurity(HttpServletRequest request, String userName, CharSequence password) {
		logger.debug("setSpringLoginSecurity:INVOKED");
		UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(userName, password);
		request.getSession();
		token.setDetails(new WebAuthenticationDetails(request));
		Authentication authenticatedUser = authenticationProvider.authenticate(token);
		SecurityContextHolder.getContext().setAuthentication(authenticatedUser);
	}

	private Response<String> buildErrorResposne(Exception ex) {
		prepareErrorLog(ex);
		return Response.buildResponse(null, StatusType.ERROR, 500, ex.getMessage());
	}
}
