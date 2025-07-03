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
import com.letmecall.rgt.domain.Roles;
import com.letmecall.rgt.model.Response;
import com.letmecall.rgt.model.StatusType;

import jakarta.validation.Valid;

@RestController
@RequestMapping(value = RgtResource.ROLES_BASE_URL)
public class RolesController {

	private static final Logger logger = LoggerFactory.getLogger(RolesController.class);

	@Autowired
	com.letmecall.service.RolesServcie rolesServcie;

	@PostMapping(consumes = RgtResource.CONSUMES, produces = RgtResource.PRODUCES)
	@ApiMetrics(methodName = "CreateRoles")
	@CalLog
	public ResponseEntity<Response<Roles>> addRoles(@RequestBody @Valid Roles roles) {
		Response<Roles> response = null;
		try {
			roles = rolesServcie.createRoles(roles);
			if (roles != null) {
				response = Response.buildResponse(roles, StatusType.SUCCESS, HttpStatus.CREATED.value(),
						" Roles are added successfully.");
			} else {
				response = Response.buildResponse(roles, StatusType.ERROR, HttpStatus.INTERNAL_SERVER_ERROR.value(),
						" Roles are not added.");
			}
		} catch (Exception e) {
			performExceptionLogger(e);
			response = Response.buildResponse(null, StatusType.ERROR, HttpStatus.INTERNAL_SERVER_ERROR.value(),
					"Error Occurred While Creating Roles");
		}
		return new ResponseEntity<>(response, HttpStatus.valueOf(Integer.parseInt(response.getCode())));
	}

	@PutMapping(value = RgtResource.ROLES_BY_ID, consumes = RgtResource.CONSUMES, produces = RgtResource.PRODUCES)
	@ApiMetrics(methodName = "UpdateRoles")
	@CalLog
	public ResponseEntity<Response<Roles>> updateRoles(@PathVariable(name = "rolesId") Long rolesId,
			@RequestBody @Valid Roles roles) {
		Response<Roles> response = null;
		try {
			roles.setRoleId(rolesId);
			roles = rolesServcie.updateRoles(roles);
			if (roles != null) {
				response = Response.buildResponse(roles, StatusType.SUCCESS, HttpStatus.CREATED.value(),
						" Roles are updated successfully.");
			} else {
				response = Response.buildResponse(roles, StatusType.ERROR, HttpStatus.INTERNAL_SERVER_ERROR.value(),
						" Roles are not not updated.");
			}
		} catch (Exception e) {
			performExceptionLogger(e);
			response = Response.buildResponse(null, StatusType.ERROR, HttpStatus.INTERNAL_SERVER_ERROR.value(),
					"Error Occurred While Updating Roles");
		}
		return new ResponseEntity<>(response, HttpStatus.valueOf(Integer.parseInt(response.getCode())));
	}

	@DeleteMapping(value = RgtResource.ROLES_BY_ID)
	@ApiMetrics(methodName = "DeleteRoles")
	@CalLog
	public ResponseEntity<Response<Roles>> deleteRoles(@PathVariable(name = "rolesId") Long rolesId) {
		Roles roles = new Roles();
		roles.setRoleId(rolesId);
		Boolean isExgist = rolesServcie.deleteRole(roles);
		Response<Roles> response = null;
		if (isExgist != null && isExgist) {
			response = Response.buildResponse(roles, StatusType.SUCCESS, HttpStatus.OK.value(),
					"Roles are delete successfully.");
		} else {
			response = Response.buildResponse(roles, StatusType.ERROR, HttpStatus.INTERNAL_SERVER_ERROR.value(),
					" Roles are not found " + rolesId + ".");
		}
		return new ResponseEntity<>(response, HttpStatus.valueOf(Integer.parseInt(response.getCode())));
	}

	@GetMapping(value = RgtResource.ROLES_BY_ID)
	@ApiMetrics(methodName = "FetchRolesById")
	@CalLog
	public ResponseEntity<Response<Roles>> getRoles(@PathVariable(name = "rolesId") Long rolesId) {
		Roles roles = new Roles();
		roles.setRoleId(rolesId);
		roles = rolesServcie.fetchRole(roles);
		Response<Roles> response = null;
		if (roles != null) {
			response = Response.buildResponse(roles, StatusType.SUCCESS, HttpStatus.OK.value(),
					"Roles are fetched successfully.");
		} else {
			response = Response.buildResponse(roles, StatusType.ERROR, HttpStatus.INTERNAL_SERVER_ERROR.value(),
					"Roles are not found " + rolesId + ".");
		}
		return new ResponseEntity<>(response, HttpStatus.valueOf(Integer.parseInt(response.getCode())));
	}

	@GetMapping()
	@ApiMetrics(methodName = "FetchRoles")
	@CalLog
	public ResponseEntity<Response<List<Roles>>> getRoles(
			@RequestParam(name = "pageNumber", defaultValue = "1") Integer pageNumber,
			@RequestParam(name = "pageSize", defaultValue = "10") Integer limit) {
		Roles roles = new Roles();
		roles.setPageNumber(pageNumber);
		roles.setPageSize(limit);
		Response<List<Roles>> response = Response.buildResponse(rolesServcie.fetchRoles(roles), StatusType.SUCCESS,
				HttpStatus.OK.value(), "Roles are fetched successfully.");
		return new ResponseEntity<>(response, HttpStatus.valueOf(Integer.parseInt(response.getCode())));
	}

	private void performExceptionLogger(Exception e) {
		logger.error("Exception Occured while executing service", e);
	}

}
