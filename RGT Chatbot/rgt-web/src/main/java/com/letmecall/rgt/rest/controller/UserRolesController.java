package com.letmecall.rgt.rest.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.letmecall.rgt.aspect.ApiMetrics;
import com.letmecall.rgt.aspect.CalLog;
import com.letmecall.rgt.domain.UserRoles;
import com.letmecall.rgt.model.Response;
import com.letmecall.rgt.model.StatusType;
import com.letmecall.service.UserRolesServcie;

import jakarta.validation.Valid;

@RestController
@RequestMapping(value = RgtResource.USER_ROLES_BASE_URL)
public class UserRolesController {

	private static final Logger logger = LoggerFactory.getLogger(UserRolesController.class);

	@Autowired
	private UserRolesServcie userRolesServcie;

	@PostMapping(consumes = RgtResource.CONSUMES, produces = RgtResource.PRODUCES)
	@ApiMetrics(methodName = "CreateUserRoles")
	@CalLog
	public ResponseEntity<Response<UserRoles>> addUserRoles(@RequestBody @Valid UserRoles userRoles) {
		Response<UserRoles> response = null;
		try {
			userRoles = userRolesServcie.createUserRoles(userRoles);
			if (userRoles != null) {
				response = Response.buildResponse(userRoles, StatusType.SUCCESS, HttpStatus.CREATED.value(),
						"UserRoles Details are added successfully.");
			} else {
				response = Response.buildResponse(userRoles, StatusType.ERROR, HttpStatus.INTERNAL_SERVER_ERROR.value(),
						"UserRoles Details are not added.");
			}
		} catch (Exception e) {
			performExceptionLogger(e);
			response = Response.buildResponse(null, StatusType.ERROR, HttpStatus.INTERNAL_SERVER_ERROR.value(),
					"Error Occurred While Creating UserRoles");
		}
		return new ResponseEntity<>(response, HttpStatus.valueOf(Integer.parseInt(response.getCode())));
	}

	@PutMapping(value = RgtResource.USER_ROLES_BY_ID, consumes = RgtResource.CONSUMES, produces = RgtResource.PRODUCES)
	@ApiMetrics(methodName = "UpdateUserRoles")
	@CalLog
	public ResponseEntity<Response<UserRoles>> updateUserRoles(@PathVariable(name = "userRoleId") Long userRoleId,
			@RequestBody @Valid UserRoles userRoles) {
		Response<UserRoles> response = null;
		try {
			userRoles.setUserRoleId(userRoleId);
			userRoles = userRolesServcie.updateUserRoles(userRoles);
			if (userRoles != null) {
				response = Response.buildResponse(userRoles, StatusType.SUCCESS, HttpStatus.OK.value(),
						"UserRoles Details are updated successfully.");
			} else {
				response = Response.buildResponse(userRoles, StatusType.ERROR, HttpStatus.INTERNAL_SERVER_ERROR.value(),
						"UserRoles Details are not are updated.");
			}
		} catch (Exception e) {
			performExceptionLogger(e);
			response = Response.buildResponse(null, StatusType.ERROR, HttpStatus.INTERNAL_SERVER_ERROR.value(),
					"Error Occurred While Updating UserRoles");
		}
		return new ResponseEntity<>(response, HttpStatus.valueOf(Integer.parseInt(response.getCode())));
	}

	@GetMapping(value = RgtResource.USER_ROLES_BY_ID)
	@ApiMetrics(methodName = "FetchUserRolesById")
	@CalLog
	public ResponseEntity<Response<UserRoles>> getUserRoles(@PathVariable(name = "userRoleId") Long userRoleId) {
		UserRoles userRoles = new UserRoles();
		userRoles.setUserRoleId(userRoleId);
		userRoles = userRolesServcie.fetchUserRole(userRoles);
		Response<UserRoles> response = null;
		if (userRoles != null) {
			response = Response.buildResponse(userRoles, StatusType.SUCCESS, HttpStatus.OK.value(),
					"UserRoles Details are fetch successfully.");
		} else {
			response = Response.buildResponse(userRoles, StatusType.ERROR, HttpStatus.INTERNAL_SERVER_ERROR.value(),
					"UserRoles Details are not found " + userRoleId + ".");
		}
		return new ResponseEntity<>(response, HttpStatus.valueOf(Integer.parseInt(response.getCode())));
	}

	@GetMapping
	@ApiMetrics(methodName = "FetchUserRoles")
	@CalLog
	public ResponseEntity<Response<List<UserRoles>>> getUserRoles(
			@RequestParam(name = "pageNumber", defaultValue = "1") Integer pageNumber,
			@RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize) {
		UserRoles userRoles = new UserRoles();
		userRoles.setPageNumber(pageNumber);
		userRoles.setPageSize(pageSize);
		Response<List<UserRoles>> response = Response.buildResponse(userRolesServcie.fetchUserRoles(userRoles),
				StatusType.SUCCESS, HttpStatus.OK.value(), "UserRoles Details are fetch successfully.");
		return new ResponseEntity<>(response, HttpStatus.valueOf(Integer.parseInt(response.getCode())));
	}

	@DeleteMapping(value = RgtResource.USER_ROLES_BY_ID)
	@ApiMetrics(methodName = "DeleteUserRoles")
	@CalLog
	public ResponseEntity<Response<UserRoles>> deletUserRoles(@PathVariable(name = "userRoleId") Long userRoleId) {
		Response<UserRoles> response = null;
		try {
			UserRoles userRoles = new UserRoles();
			userRoles.setUserRoleId(userRoleId);
			Boolean value = userRolesServcie.deleteUserRole(userRoles);
			if (value != null && value) {
				response = Response.buildResponse(userRoles, StatusType.SUCCESS, HttpStatus.OK.value(),
						"UserRoles Details are deleted successfully.");
			} else {
				response = Response.buildResponse(userRoles, StatusType.ERROR, HttpStatus.INTERNAL_SERVER_ERROR.value(),
						"UserRoles Details are not found " + userRoleId + ".");
			}
		} catch (Exception e) {
			performExceptionLogger(e);
			response = Response.buildResponse(null, StatusType.ERROR, HttpStatus.INTERNAL_SERVER_ERROR.value(),
					"Error Occurred While Deleteing UserRoles");
		}
		return new ResponseEntity<>(response, HttpStatus.valueOf(Integer.parseInt(response.getCode())));
	}

	private void performExceptionLogger(Exception e) {
		logger.error("Exception Occured while executing service", e);
	}
}
