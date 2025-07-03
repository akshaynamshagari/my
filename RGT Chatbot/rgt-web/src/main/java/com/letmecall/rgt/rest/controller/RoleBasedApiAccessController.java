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
import com.letmecall.rgt.domain.RoleBasedApiAccess;
import com.letmecall.rgt.model.Response;
import com.letmecall.rgt.model.StatusType;
import com.letmecall.service.RolesBasedApiAccessServcie;

import jakarta.validation.Valid;

@RestController
@RequestMapping(value = RgtResource.ROLE_BASED_APIDETAILS_BASE_URL)
public class RoleBasedApiAccessController {

	private static final Logger logger = LoggerFactory.getLogger(RoleBasedApiAccessController.class);

	@Autowired
	RolesBasedApiAccessServcie rolesBasedApiAccessServcie;

	@PostMapping()
	@ApiMetrics(methodName = "AddRolesBasedApiAccess")
	@CalLog
	public ResponseEntity<Response<RoleBasedApiAccess>> addRolesBasedApiAccess(
			@RequestBody @Valid RoleBasedApiAccess roleBasedApiAccess) {
		Response<RoleBasedApiAccess> response = null;
		try {
			roleBasedApiAccess = rolesBasedApiAccessServcie.createRoleBasedApiAccess(roleBasedApiAccess);
			if (roleBasedApiAccess != null) {
				response = Response.buildResponse(roleBasedApiAccess, StatusType.SUCCESS, HttpStatus.CREATED.value(),
						" RolesBasedApiAccess are added successfully.");
			} else {
				response = Response.buildResponse(roleBasedApiAccess, StatusType.ERROR,
						HttpStatus.INTERNAL_SERVER_ERROR.value(), " RolesBasedApiAccess are not added.");
			}
		} catch (Exception e) {
			performExceptionLogger(e);
			response = Response.buildResponse(null, StatusType.ERROR, HttpStatus.INTERNAL_SERVER_ERROR.value(),
					"Error Occurred While Creating RolesBasedApiAccess");
		}
		return new ResponseEntity<>(response, HttpStatus.valueOf(Integer.valueOf(response.getCode())));
	}

	@PutMapping(value = RgtResource.ROLE_BASED_APIDETAILS_BY_ID)
	@ApiMetrics(methodName = "UpdateRolesBasedApiAccess")
	@CalLog
	public ResponseEntity<Response<RoleBasedApiAccess>> updateRoleBasedApiAccess(
			@PathVariable(name = "roleBasedApiAccessId") Long roleBasedApiAccessId,
			@RequestBody @Valid RoleBasedApiAccess roleBasedApiAccess) {
		Response<RoleBasedApiAccess> response = null;
		try {
			roleBasedApiAccess.setRoleBaseApiAccess(roleBasedApiAccessId);
			roleBasedApiAccess = rolesBasedApiAccessServcie.updateRoleBasedApiAccess(roleBasedApiAccess);
			if (roleBasedApiAccess != null) {
				response = Response.buildResponse(roleBasedApiAccess, StatusType.SUCCESS, HttpStatus.CREATED.value(),
						" RolesBasedApiAccess are updated successfully.");
			} else {
				response = Response.buildResponse(roleBasedApiAccess, StatusType.ERROR, HttpStatus.CREATED.value(),
						" RolesBasedApiAccess are not not updated.");
			}
		} catch (Exception e) {
			performExceptionLogger(e);
			response = Response.buildResponse(null, StatusType.ERROR, HttpStatus.OK.value(),
					"Error Occurred While Updating RolesBasedApiAccess");
		}
		return new ResponseEntity<>(response, HttpStatus.valueOf(Integer.valueOf(response.getCode())));
	}

	@DeleteMapping(value = RgtResource.ROLE_BASED_APIDETAILS_BY_ID)
	@ApiMetrics(methodName = "DeleteRolesBasedApiAccess")
	@CalLog
	public ResponseEntity<Response<RoleBasedApiAccess>> deleteRoleBasedApiAccess(
			@PathVariable(name = "roleBasedApiAccessId") Long roleBasedApiAccessId) {
		Response<RoleBasedApiAccess> response = null;
		try {
			RoleBasedApiAccess roleBasedApiAccess = new RoleBasedApiAccess();
			roleBasedApiAccess.setRoleBaseApiAccess(roleBasedApiAccessId);
			Boolean isExgist = rolesBasedApiAccessServcie.deleteRoleBasedApiAccess(roleBasedApiAccess);
			if (isExgist != null && isExgist) {
				response = Response.buildResponse(roleBasedApiAccess, StatusType.SUCCESS, HttpStatus.OK.value(),
						"RolesBasedApiAccess are delete successfully.");
			} else {
				response = Response.buildResponse(roleBasedApiAccess, StatusType.ERROR, HttpStatus.OK.value(),
						" RolesBasedApiAccess are not found " + roleBasedApiAccessId + ".");
			}
		} catch (Exception e) {
			performExceptionLogger(e);
			response = Response.buildResponse(null, StatusType.ERROR, HttpStatus.OK.value(),
					"Error Occurred While Deleting RolesBasedApiAccess");
		}
		return new ResponseEntity<>(response, HttpStatus.valueOf(Integer.valueOf(response.getCode())));
	}

	@GetMapping(value = RgtResource.ROLE_BASED_APIDETAILS_BY_ID)
	@ApiMetrics(methodName = "FetchRolesBasedApiAccessById")
	@CalLog
	public ResponseEntity<Response<RoleBasedApiAccess>> getRoleBasedApiAccess(
			@PathVariable(name = "roleBasedApiAccessId") Long roleBasedApiAccessId) {
		Response<RoleBasedApiAccess> response = null;
		try {
			RoleBasedApiAccess roleBasedApiAccess = new RoleBasedApiAccess();
			roleBasedApiAccess = rolesBasedApiAccessServcie.fetchRoleBasedApiAcces(roleBasedApiAccess);
			if (roleBasedApiAccess != null) {
				response = Response.buildResponse(roleBasedApiAccess, StatusType.SUCCESS, HttpStatus.OK.value(),
						"RolesBasedApiAccess are fetched successfully.");
			} else {
				response = Response.buildResponse(roleBasedApiAccess, StatusType.ERROR, HttpStatus.OK.value(),
						"RolesBasedApiAccess are not found " + roleBasedApiAccessId + ".");
			}
		} catch (Exception e) {
			performExceptionLogger(e);
			response = Response.buildResponse(null, StatusType.ERROR, HttpStatus.OK.value(),
					"Error Occurred While fetching RolesBasedApiAccess");
		}
		return new ResponseEntity<>(response, HttpStatus.valueOf(Integer.valueOf(response.getCode())));
	}

	@GetMapping()
	@ApiMetrics(methodName = "FetchRolesBasedApiAccess")
	@CalLog
	public ResponseEntity<Response<List<RoleBasedApiAccess>>> getRoleBasedApiAccess(
			@RequestParam(name = "pageNumber", defaultValue = "1") Integer pageNumber,
			@RequestParam(name = "pageSize", defaultValue = "10") Integer limit) {
		RoleBasedApiAccess roleBasedApiAccess = new RoleBasedApiAccess();
		roleBasedApiAccess.setPageNumber(pageNumber);
		roleBasedApiAccess.setPageSize(limit);
		Response<List<RoleBasedApiAccess>> response = Response.buildResponse(
				rolesBasedApiAccessServcie.fetchRoleBasedApiAccess(roleBasedApiAccess), StatusType.SUCCESS,
				HttpStatus.OK.value(), "Roles are fetched successfully.");
		return new ResponseEntity<>(response, HttpStatus.valueOf(Integer.valueOf(response.getCode())));
	}

	private void performExceptionLogger(Exception e) {
		logger.error("Exception Occured while executing service", e);
	}

}
