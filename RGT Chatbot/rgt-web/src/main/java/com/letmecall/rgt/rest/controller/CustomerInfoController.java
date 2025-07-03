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
import org.springframework.web.bind.annotation.RestController;

import com.letmecall.rgt.aspect.ApiMetrics;
import com.letmecall.rgt.aspect.CalLog;
import com.letmecall.rgt.domain.CustomerInfo;
import com.letmecall.rgt.model.Response;
import com.letmecall.rgt.model.StatusType;
import com.letmecall.service.CustomerInfoService;

import jakarta.validation.Valid;

@RestController
@RequestMapping(value = RgtResource.CUSTOMERINFO_BASE_URL)
public class CustomerInfoController {

	private static final Logger logger = LoggerFactory.getLogger(CustomerInfoController.class);

	@Autowired
	CustomerInfoService customerInfoService;

	@GetMapping()
	@ApiMetrics(methodName = "FetchCustomerInfo")
	@CalLog
	public ResponseEntity<Response<List<CustomerInfo>>> getAllCustomerInfo() {
		Response<List<CustomerInfo>> response = Response.buildResponse(customerInfoService.findAllCustomerInfo(),
				StatusType.SUCCESS, HttpStatus.OK.value(), "CustomerInfo are fetched successfully.");
		return new ResponseEntity<>(response, HttpStatus.valueOf(Integer.parseInt(response.getCode())));
	}

	@PostMapping()
	@ApiMetrics(methodName = "AddCustomerInfo")
	@CalLog
	public ResponseEntity<Response<CustomerInfo>> createCustomerInfo(@Valid @RequestBody CustomerInfo customerInfo) {
		Response<CustomerInfo> response = null;
		try {
			customerInfo = customerInfoService.createCustomerInfo(customerInfo);
			if (customerInfo != null) {
				response = Response.buildResponse(customerInfo, StatusType.SUCCESS, HttpStatus.CREATED.value(),
						" CustomerInfo are added successfully.");
			} else {
				response = Response.buildResponse(customerInfo, StatusType.ERROR,
						HttpStatus.INTERNAL_SERVER_ERROR.value(), " CustomerInfo are not added.");
			}
		} catch (Exception e) {
			performExceptionLogger(e);
			response = Response.buildResponse(null, StatusType.ERROR, HttpStatus.INTERNAL_SERVER_ERROR.value(),
					"Error Occurred While Creating Roles");
		}
		return new ResponseEntity<>(response, HttpStatus.valueOf(Integer.parseInt(response.getCode())));
	}

	@PutMapping(value = RgtResource.CUSTOMER_BY_ID)
	@ApiMetrics(methodName = "UpdateCustomerInfo")
	@CalLog
	public ResponseEntity<Response<CustomerInfo>> updateCustomerInfo(@PathVariable("customerId") Long customerId,
			@Valid @RequestBody CustomerInfo customerInfo) {
		Response<CustomerInfo> response = null;
		try {
			customerInfo.setUserId(customerId);
			customerInfo = customerInfoService.updateCustomerInfo(customerInfo);
			if (customerInfo != null) {
				response = Response.buildResponse(customerInfo, StatusType.SUCCESS, HttpStatus.OK.value(),
						" CustomerInfo are updated successfully.");
			} else {
				response = Response.buildResponse(customerInfo, StatusType.ERROR,
						HttpStatus.INTERNAL_SERVER_ERROR.value(), " CustomerInfo are not not updated.");
			}
		} catch (Exception e) {
			performExceptionLogger(e);
			response = Response.buildResponse(null, StatusType.ERROR, HttpStatus.INTERNAL_SERVER_ERROR.value(),
					"Error Occurred While Updating CustomerInfo");
		}
		return new ResponseEntity<>(response, HttpStatus.valueOf(Integer.parseInt(response.getCode())));
	}

	@DeleteMapping(value = RgtResource.CUSTOMER_BY_ID)
	@ApiMetrics(methodName = "DeleteCustomerInfo")
	@CalLog
	public ResponseEntity<Response<CustomerInfo>> deletCustomerInfo(@PathVariable("customerId") Long customerId) {
		CustomerInfo customerInfo = new CustomerInfo();
		customerInfo.setUserId(customerId);
		customerInfo = customerInfoService.deleteCustomerInfo(customerInfo);
		Response<CustomerInfo> response = null;
		if (customerInfo != null) {
			response = Response.buildResponse(customerInfo, StatusType.SUCCESS, HttpStatus.OK.value(),
					"CustomerInfo are delete successfully.");
		} else {
			response = Response.buildResponse(customerInfo, StatusType.ERROR, HttpStatus.INTERNAL_SERVER_ERROR.value(),
					" CustomerInfo are not found " + customerId + ".");
		}
		return new ResponseEntity<>(response, HttpStatus.valueOf(Integer.parseInt(response.getCode())));
	}

	@PutMapping(value = RgtResource.ENABLE_BY_CUSTOMER)
	@ApiMetrics(methodName = "EnableCustomerInfo")
	@CalLog
	public ResponseEntity<Response<CustomerInfo>> enableCustomerInfo(@PathVariable("customerId") Long customerId) {
		Response<CustomerInfo> response = null;
		try {
			CustomerInfo customerInfo = new CustomerInfo();
			customerInfo.setUserId(customerId);
			customerInfo = customerInfoService.updateCustomerInfo(customerInfo);
			if (customerInfo != null) {
				response = Response.buildResponse(customerInfo, StatusType.SUCCESS, HttpStatus.OK.value(),
						" CustomerInfo are enbale successfully.");
			} else {
				response = Response.buildResponse(customerInfo, StatusType.ERROR,
						HttpStatus.INTERNAL_SERVER_ERROR.value(), " CustomerInfo are not not updated.");
			}
		} catch (Exception e) {
			performExceptionLogger(e);
			response = Response.buildResponse(null, StatusType.ERROR, HttpStatus.INTERNAL_SERVER_ERROR.value(),
					"Error Occurred While Updating CustomerInfo");
		}
		return new ResponseEntity<>(response, HttpStatus.valueOf(Integer.parseInt(response.getCode())));
	}

	private void performExceptionLogger(Exception e) {
		logger.error("Exception Occured while executing service", e);
	}
}
