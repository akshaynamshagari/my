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
import com.letmecall.rgt.domain.JobDetails;
import com.letmecall.rgt.model.Response;
import com.letmecall.rgt.model.StatusType;
import com.letmecall.service.JobDetailsService;

import jakarta.validation.Valid;

@RestController
@RequestMapping(value = RgtResource.JOBEDETAILS_BASE_URL)
public class JobDetailsController {

	private static final Logger logger = LoggerFactory.getLogger(JobDetailsController.class);

	@Autowired
	JobDetailsService jobDetailsService;

	@GetMapping("")
	@ApiMetrics(methodName = "FetchJobDetails")
	@CalLog
	public ResponseEntity<Response<List<JobDetails>>> findAllJobDetails() {
		Response<List<JobDetails>> response = Response.buildResponse(jobDetailsService.findAllJobDetails(),
				StatusType.SUCCESS, HttpStatus.OK.value(), "JobDetails are fetched successfully.");
		return new ResponseEntity<>(response, HttpStatus.valueOf(Integer.parseInt(response.getCode())));
	}

	@PostMapping("")
	@ApiMetrics(methodName = "AddJobDetails")
	@CalLog
	public ResponseEntity<Response<JobDetails>> addJobDetails(@Valid @RequestBody JobDetails jobDetails) {
		Response<JobDetails> response = null;
		try {
			jobDetails = jobDetailsService.createJobDetails(jobDetails);
			if (jobDetails != null) {
				response = Response.buildResponse(jobDetails, StatusType.SUCCESS, HttpStatus.CREATED.value(),
						" Roles are added successfully.");
			} else {
				response = Response.buildResponse(jobDetails, StatusType.ERROR, HttpStatus.INTERNAL_SERVER_ERROR.value(),
						" Roles are not added.");
			}
		} catch (Exception e) {
			performExceptionLogger(e);
			response = Response.buildResponse(null, StatusType.ERROR, HttpStatus.INTERNAL_SERVER_ERROR.value(),
					"Error Occurred While Creating Roles");
		}
		return new ResponseEntity<>(response, HttpStatus.valueOf(Integer.parseInt(response.getCode())));
	}

	@PutMapping(value = RgtResource.JOBEDETAILS_BY_ID)
	@ApiMetrics(methodName = "UpdateJobDetails")
	@CalLog
	public ResponseEntity<Response<JobDetails>> updateJobDetails(@PathVariable("jobdetailsId") Long jobdetailsId,
			@Valid @RequestBody JobDetails jobDetails) {
		Response<JobDetails> response = null;
		try {
			jobDetails.setJobId(jobdetailsId);
			jobDetails = jobDetailsService.updateJobDetails(jobDetails);
			if (jobDetails != null) {
				response = Response.buildResponse(jobDetails, StatusType.SUCCESS, HttpStatus.OK.value(),
						" JobDetails are updated successfully.");
			} else {
				response = Response.buildResponse(jobDetails, StatusType.ERROR, HttpStatus.INTERNAL_SERVER_ERROR.value(),
						" JobDetails are not not updated.");
			}
		} catch (Exception e) {
			performExceptionLogger(e);
			response = Response.buildResponse(null, StatusType.ERROR, HttpStatus.INTERNAL_SERVER_ERROR.value(),
					"Error Occurred While Updating JobDetails");
		}
		return new ResponseEntity<>(response, HttpStatus.valueOf(Integer.parseInt(response.getCode())));
	}

	@DeleteMapping(value = RgtResource.JOBEDETAILS_BY_ID)
	@ApiMetrics(methodName = "DeleteJobDetails")
	@CalLog
	public ResponseEntity<Response<JobDetails>> deletJobDetails(@PathVariable("jobdetailsId") Long jobdetailsId) {
		JobDetails jobDetails = new JobDetails();
		jobDetails.setJobId(jobdetailsId);
		jobDetails = jobDetailsService.deleteJobDetails(jobDetails);
		Response<JobDetails> response = null;
		if (jobDetails != null) {
			response = Response.buildResponse(jobDetails, StatusType.SUCCESS, HttpStatus.OK.value(),
					"Roles are delete successfully.");
		} else {
			response = Response.buildResponse(jobDetails, StatusType.ERROR, HttpStatus.INTERNAL_SERVER_ERROR.value(),
					" Roles are not found " + jobdetailsId + ".");
		}
		return new ResponseEntity<>(response, HttpStatus.valueOf(Integer.parseInt(response.getCode())));
	}

	private void performExceptionLogger(Exception e) {
		logger.error("Exception Occured while executing service", e);
	}
}
