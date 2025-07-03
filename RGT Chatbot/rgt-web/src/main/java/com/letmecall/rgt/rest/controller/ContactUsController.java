package com.letmecall.rgt.rest.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.letmecall.rgt.aspect.ApiMetrics;
import com.letmecall.rgt.aspect.CalLog;
import com.letmecall.rgt.domain.ContactUs;
import com.letmecall.rgt.model.Response;
import com.letmecall.rgt.model.StatusType;
import com.letmecall.service.ContactUsService;

import jakarta.validation.Valid;

@RestController
@RequestMapping(value = RgtResource.CONTACTUS_BASE_URL)
public class ContactUsController {

	private static final Logger logger = LoggerFactory.getLogger(ContactUsController.class);

	@Autowired
	ContactUsService contactUsService;

	@PostMapping(consumes = RgtResource.CONSUMES, produces = RgtResource.PRODUCES)
	@ApiMetrics(methodName = "addContactUs")
	@CalLog
	public ResponseEntity<Response<ContactUs>> addContactUs(@RequestBody @Valid ContactUs contactUs) {
		Response<ContactUs> response = null;
		try {
			contactUs = contactUsService.createContactUs(contactUs);
			if (contactUs != null) {
				response = Response.buildResponse(contactUs, StatusType.SUCCESS, HttpStatus.CREATED.value(),
						" ContactUs are added successfully.");
			} else {
				response = Response.buildResponse(contactUs, StatusType.ERROR, HttpStatus.INTERNAL_SERVER_ERROR.value(),
						" ContactUs are not added.");
			}
		} catch (Exception e) {
			logger.error("Exception Occured while executing service", e);
			response = Response.buildResponse(null, StatusType.ERROR, HttpStatus.INTERNAL_SERVER_ERROR.value(),
					"Error Occurred While Creating ContactUs");
		}
		return new ResponseEntity<>(response, HttpStatus.valueOf(Integer.valueOf(response.getCode())));
	}

}
