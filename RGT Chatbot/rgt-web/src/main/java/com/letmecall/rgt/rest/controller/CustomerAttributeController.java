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
import com.letmecall.rgt.domain.CustomerAttribute;
import com.letmecall.rgt.model.Response;
import com.letmecall.rgt.model.StatusType;
import com.letmecall.service.CustomerAttributeService;

import jakarta.validation.Valid;

@RestController
@RequestMapping(value = RgtResource.CUSTOMERATTRIBUTE_BASE_URL)
public class CustomerAttributeController {

	private static final Logger logger = LoggerFactory.getLogger(CustomerAttributeController.class);

	@Autowired
	CustomerAttributeService customerAttributeService;

	@GetMapping(value = RgtResource.CUSTOMERATTRIBUTE_BY_CUSTOMER_ID)
	@ApiMetrics(methodName = "FetchCustomerAttribute")
	@CalLog
	public ResponseEntity<Response<List<CustomerAttribute>>> getAllCustomerAttributeByCustomerInfo(
			@PathVariable("customerId") Long customerId) {
		CustomerAttribute customerAttribute = new CustomerAttribute();
		customerAttribute.setCustomerId(customerId);
		Response<List<CustomerAttribute>> response = Response.buildResponse(
				customerAttributeService.findAllCustomerAttribute(customerAttribute), StatusType.SUCCESS,
				HttpStatus.OK.value(), "CustomerAttribute are fetched successfully.");
		return new ResponseEntity<>(response, HttpStatus.valueOf(Integer.parseInt(response.getCode())));
	}

	@PostMapping(value = RgtResource.CUSTOMERATTRIBUTE_BY_CUSTOMER_ID)
	@ApiMetrics(methodName = "AddCustomerAttribute")
	@CalLog
	public ResponseEntity<Response<CustomerAttribute>> addCustomerAttributeToCustomerInfo(
			@PathVariable("customerId") Long customerId, @Valid @RequestBody CustomerAttribute customerAttribute) {
		Response<CustomerAttribute> response = null;
		try {
			customerAttribute = customerAttributeService.createCustomerAttribute(customerAttribute);
			if (customerAttribute != null) {
				response = Response.buildResponse(customerAttribute, StatusType.SUCCESS, HttpStatus.CREATED.value(),
						" CustomerAttribute are added successfully.");
			} else {
				response = Response.buildResponse(customerAttribute, StatusType.ERROR,
						HttpStatus.INTERNAL_SERVER_ERROR.value(), " CustomerAttribute are not added.");
			}
		} catch (Exception e) {
			performExceptionLogger(e);
			response = Response.buildResponse(null, StatusType.ERROR, HttpStatus.INTERNAL_SERVER_ERROR.value(),
					"Error Occurred While Creating CustomerAttribute");
		}
		return new ResponseEntity<>(response, HttpStatus.valueOf(Integer.parseInt(response.getCode())));
	}

	@PutMapping(value = RgtResource.CUSTOMERATTRIBUTE_BY_ID + RgtResource.CUSTOMERATTRIBUTE_BY_CUSTOMER_ID)
	@ApiMetrics(methodName = "UpdateCustomerAttribute")
	@CalLog
	public ResponseEntity<Response<CustomerAttribute>> updateCustomerAttribute(
			@PathVariable("customerattributeId") Integer id, @PathVariable("customerId") Long customerId,
			@RequestBody @Valid CustomerAttribute customerAttribute) {
		Response<CustomerAttribute> response = null;
		try {
			customerAttribute.setCustomerAttrId(id);
			customerAttribute.setCustomerId(customerId);
			customerAttribute = customerAttributeService.updateCustomerAttribute(customerAttribute);
			if (customerAttribute != null) {
				response = Response.buildResponse(customerAttribute, StatusType.SUCCESS, HttpStatus.OK.value(),
						" CustomerAttribute are updated successfully.");
			} else {
				response = Response.buildResponse(customerAttribute, StatusType.ERROR,
						HttpStatus.INTERNAL_SERVER_ERROR.value(), " CustomerAttribute are not not updated.");
			}
		} catch (Exception e) {
			performExceptionLogger(e);
			response = Response.buildResponse(null, StatusType.ERROR, HttpStatus.INTERNAL_SERVER_ERROR.value(),
					"Error Occurred While CustomerAttribute Roles");
		}
		return new ResponseEntity<>(response, HttpStatus.valueOf(Integer.parseInt(response.getCode())));
	}

	@DeleteMapping(value = RgtResource.CUSTOMERATTRIBUTE_BY_ID)
	@ApiMetrics(methodName = "DeleteCustomerAttribute")
	@CalLog
	public ResponseEntity<Response<CustomerAttribute>> deletCustomerAttribute(
			@PathVariable("customerattributeId") Integer id) {
		CustomerAttribute customerAttribute = new CustomerAttribute();
		customerAttribute.setCustomerAttrId(id);
		customerAttribute = customerAttributeService.deleteCustomerAttribute(customerAttribute);
		Response<CustomerAttribute> response = null;
		if (customerAttribute != null) {
			response = Response.buildResponse(customerAttribute, StatusType.SUCCESS, HttpStatus.OK.value(),
					"Roles are delete successfully.");
		} else {
			response = Response.buildResponse(customerAttribute, StatusType.ERROR,
					HttpStatus.INTERNAL_SERVER_ERROR.value(), " Roles are not found " + id + ".");
		}
		return new ResponseEntity<>(response, HttpStatus.valueOf(Integer.parseInt(response.getCode())));
	}

	private void performExceptionLogger(Exception e) {
		logger.error("Exception Occured while executing service", e);
	}

}
