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
import com.letmecall.rgt.domain.ApiDetails;
import com.letmecall.rgt.model.Response;
import com.letmecall.rgt.model.StatusType;
import com.letmecall.rgt.rest.helper.ApiDetailsHelper;

import jakarta.validation.Valid;

@RestController
@RequestMapping(value = RgtResource.APIDETAILS_BASE_URL)
public class ApiDetailsController {

	private static final Logger logger = LoggerFactory.getLogger(ApiDetailsController.class);

	@Autowired
	ApiDetailsHelper apiDetailsHelper;

	@GetMapping
	@ApiMetrics(methodName = "findAllApiDetails")
	@CalLog
	public ResponseEntity<Response<List<ApiDetails>>> findAllApiDetails(
			@RequestParam(name = "pageNumber", defaultValue = "1") Integer pageNumber,
			@RequestParam(name = "pageSize", defaultValue = "10") Integer limit) {
		Response<List<ApiDetails>> response = Response.buildResponse(
				apiDetailsHelper.findAllApiDetails(pageNumber, limit), StatusType.SUCCESS, HttpStatus.OK.value(),
				"ApiDetails are fetched successfully.");
		return new ResponseEntity<>(response, HttpStatus.valueOf(Integer.parseInt(response.getCode())));
	}

	@PostMapping
	@ApiMetrics(methodName = "CreateApiDetails")
	@CalLog
	public ResponseEntity<Response<ApiDetails>> addApiDetails(@Valid @RequestBody ApiDetails apiDetails) {
		Response<ApiDetails> response = null;
		try {
			apiDetails = apiDetailsHelper.createApiDetails(apiDetails);
			if (apiDetails != null) {
				response = Response.buildResponse(apiDetails, StatusType.SUCCESS, HttpStatus.CREATED.value(),
						" ApiDetails are added successfully.");
			} else {
				response = Response.buildResponse(apiDetails, StatusType.ERROR, HttpStatus.INTERNAL_SERVER_ERROR.value(),
						" ApiDetails are not added.");
			}
		} catch (Exception e) {
			performExceptionLogger(e);
			response = Response.buildResponse(null, StatusType.ERROR, HttpStatus.OK.value(),
					"Error Occurred While Creating ApiDetails");
		}
		return new ResponseEntity<>(response, HttpStatus.valueOf(Integer.parseInt(response.getCode())));
	}

	@PutMapping(value = RgtResource.APIDETAILS_BY_ID)
	@ApiMetrics(methodName = "UpdateApiDetails")
	@CalLog
	public ResponseEntity<Response<ApiDetails>> updateApiDetails(@PathVariable("resourcesId") Long apiDetailsId,
			@Valid @RequestBody ApiDetails apiDetails) {
		logger.debug("updateApiDetails");
		Response<ApiDetails> response = null;
		try {
			apiDetails.setApiDetailsId(apiDetailsId);
			apiDetails = apiDetailsHelper.updateApiDetails(apiDetails);
			if (apiDetails != null) {
				response = Response.buildResponse(apiDetails, StatusType.SUCCESS, HttpStatus.OK.value(),
						"ApiDetails are updated successfully.");
			} else {
				response = Response.buildResponse(apiDetails, StatusType.ERROR, HttpStatus.INTERNAL_SERVER_ERROR.value(),
						"ApiDetails are not not updated.");
			}
		} catch (Exception e) {
			performExceptionLogger(e);
			response = Response.buildResponse(null, StatusType.ERROR, HttpStatus.INTERNAL_SERVER_ERROR.value(),
					"Error Occurred While Updating ApiDetails");
		}
		return new ResponseEntity<>(response, HttpStatus.valueOf(Integer.parseInt(response.getCode())));
	}

	@DeleteMapping(value = RgtResource.APIDETAILS_BY_ID)
	@ApiMetrics(methodName = "DeleteApiDetails")
	@CalLog
	public ResponseEntity<Response<ApiDetails>> deletApiDetails(@PathVariable("resourcesId") Long apiDetailsId) {
		Response<ApiDetails> response = null;
		try {
			ApiDetails apiDetails = new ApiDetails();
			apiDetails.setApiDetailsId(apiDetailsId);
			apiDetails = apiDetailsHelper.deleteApiDetails(apiDetails);
			if (apiDetails != null) {
				response = Response.buildResponse(apiDetails, StatusType.SUCCESS, HttpStatus.OK.value(),
						"ApiDetails are delete successfully.");
			} else {
				response = Response.buildResponse(apiDetails, StatusType.ERROR, HttpStatus.INTERNAL_SERVER_ERROR.value(),
						" ApiDetails are not found " + apiDetailsId + ".");
			}
		} catch (Exception e) {
			performExceptionLogger(e);
			response = Response.buildResponse(null, StatusType.ERROR, HttpStatus.INTERNAL_SERVER_ERROR.value(),
					"Error Occurred While Deleting ApiDetails");
		}
		return new ResponseEntity<>(response, HttpStatus.valueOf(Integer.parseInt(response.getCode())));
	}

	@GetMapping(value = RgtResource.APIDETAILS_BY_ID)
	@ApiMetrics(methodName = "FetchApiDetails")
	@CalLog
	public ResponseEntity<Response<ApiDetails>> findByApiDetails(@PathVariable("resourcesId") Long apiDetailsId) {
		Response<ApiDetails> response;
		try {
			ApiDetails apiDetails = apiDetailsHelper.findByApiDetails(apiDetailsId);
			if (apiDetails != null) {
				response = Response.buildResponse(apiDetails, StatusType.SUCCESS, HttpStatus.OK.value(),
						"ApiDetails are fetch successfully.");
			} else {
				response = Response.buildResponse(apiDetails, StatusType.ERROR, HttpStatus.INTERNAL_SERVER_ERROR.value(),
						" ApiDetails are not found " + apiDetailsId + ".");
			}
		} catch (Exception e) {
			performExceptionLogger(e);
			response = Response.buildResponse(null, StatusType.ERROR, HttpStatus.INTERNAL_SERVER_ERROR.value(),
					"Error Occurred While fetch ApiDetails");
		}
		return new ResponseEntity<>(response, HttpStatus.valueOf(Integer.parseInt(response.getCode())));
	}

	private void performExceptionLogger(Exception e) {
		logger.error("Exception Occured while executing service", e);
	}
}
