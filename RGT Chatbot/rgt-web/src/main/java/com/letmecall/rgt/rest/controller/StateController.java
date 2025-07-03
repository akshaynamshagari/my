package com.letmecall.rgt.rest.controller;

import java.util.ArrayList;
import java.util.List;

import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
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
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.letmecall.rgt.aspect.ApiMetrics;
import com.letmecall.rgt.aspect.CalLog;
import com.letmecall.rgt.domain.common.Country;
import com.letmecall.rgt.domain.common.State;
import com.letmecall.rgt.model.CustomRespose;
import com.letmecall.rgt.model.Response;
import com.letmecall.rgt.model.StatusType;
import com.letmecall.rgt.rest.helper.BaseRestUtill;
import com.letmecall.rgt.rest.helper.StateExcelHelper;
import com.letmecall.service.common.StateService;

import jakarta.validation.Valid;

@RestController
@RequestMapping(value = RgtResource.STATE_BASE_URL)
public class StateController {

	private static final Logger logger = LoggerFactory.getLogger(StateController.class);

	private static final String SUCCESS_MSG = "State are fetched successfully.";

	@Autowired
	StateService stateService;

	@Autowired
	StateExcelHelper stateExcelHelper;

	@PostMapping(consumes = RgtResource.CONSUMES, produces = RgtResource.PRODUCES)
	@ApiMetrics(methodName = "CreateState")
	@CalLog
	public ResponseEntity<Response<State>> addState(@RequestBody @Valid State state) {
		Response<State> response = null;
		try {
			state = stateService.createState(state);
			if (state != null) {
				response = Response.buildResponse(state, StatusType.SUCCESS, HttpStatus.OK.value(),
						"State are added successfully.");
			} else {
				response = Response.buildResponse(state, StatusType.ERROR, HttpStatus.INTERNAL_SERVER_ERROR.value(),
						"State are not added..");
			}
		} catch (Exception e) {
			response = buildErrorResponse(e);
		}
		return new ResponseEntity<>(response, HttpStatus.valueOf(Integer.parseInt(response.getCode())));
	}

	private Response<State> buildErrorResponse(Exception e) {
		performExceptionLogger(e);
		return Response.buildResponse(null, StatusType.ERROR, HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage());
	}

	@PutMapping(value = RgtResource.STATE_BY_ID, consumes = RgtResource.CONSUMES, produces = RgtResource.PRODUCES)
	@ApiMetrics(methodName = "UpdateState")
	@CalLog
	public ResponseEntity<Response<State>> updateState(@PathVariable(name = "stateId") Short stateId,
			@RequestBody @Valid State state) {
		Response<State> response = null;
		try {
			state.setStateId(stateId);
			state = stateService.updateState(state);
			if (state != null) {
				response = Response.buildResponse(state, StatusType.SUCCESS, HttpStatus.OK.value(),
						"State are updated successfully.");
			} else {
				response = Response.buildResponse(state, StatusType.ERROR, HttpStatus.INTERNAL_SERVER_ERROR.value(),
						"State are not not updated.");
			}
		} catch (Exception e) {
			response = buildErrorResponse(e);
		}
		return new ResponseEntity<>(response, HttpStatus.valueOf(Integer.parseInt(response.getCode())));
	}

	@DeleteMapping(value = RgtResource.STATE_BY_ID)
	@ApiMetrics(methodName = "DeleteState")
	@CalLog
	public ResponseEntity<Response<State>> deleteState(@PathVariable(name = "stateId") Short stateId) {
		State state = new State();
		state.setStateId(stateId);
		Boolean isExgist = stateService.deleteState(state);
		Response<State> response = null;
		if (isExgist != null && isExgist) {
			response = Response.buildResponse(null, StatusType.SUCCESS, HttpStatus.OK.value(),
					"State are delete successfully.");
		} else {
			response = Response.buildResponse(null, StatusType.ERROR, HttpStatus.OK.value(),
					"State are not found " + stateId + ".");
		}
		return new ResponseEntity<>(response, HttpStatus.valueOf(Integer.parseInt(response.getCode())));
	}

	@GetMapping(value = RgtResource.STATE_BY_SEARCH)
	@ApiMetrics(methodName = "FetchStateBySearch")
	@CalLog
	public ResponseEntity<Response<State>> getStateBySearch(
			@RequestParam(name = "stateName", required = false) String stateName,
			@RequestParam(name = "stateId", required = false) Short stateId) {
		State state = new State();
		state.setStateName(stateName);
		state.setStateId(stateId);
		if (stateId != null) {
			state = stateService.fetchStateById(state);
		} else {
			state = stateService.fetchStateByStateDetails(state);
		}

		Response<State> response = null;
		if (state != null) {
			response = Response.buildResponse(state, StatusType.SUCCESS, HttpStatus.OK.value(), SUCCESS_MSG);
		} else {
			response = Response.buildResponse(state, StatusType.ERROR, HttpStatus.OK.value(),
					"State are not found " + stateName + ".");
		}
		return new ResponseEntity<>(response, HttpStatus.valueOf(Integer.parseInt(response.getCode())));
	}

	@GetMapping(value = RgtResource.STATE_BY_COUNTRYID)
	@ApiMetrics(methodName = "FetchStateByCountry")
	@CalLog
	public ResponseEntity<Response<List<State>>> getStatesByCountryID(
			@PathVariable(name = "countryId") Short countryId) {
		Country country = new Country();
		country.setCountryId(countryId);
		State state = new State();
		state.setCountry(country);
		Response<List<State>> response = Response.buildResponse(stateService.fetchStateByCountryId(state),
				StatusType.SUCCESS, HttpStatus.OK.value(), SUCCESS_MSG);
		return new ResponseEntity<>(response, HttpStatus.valueOf(Integer.parseInt(response.getCode())));
	}

	@GetMapping()
	@ApiMetrics(methodName = "FetchStates")
	@CalLog
	public ResponseEntity<Response<List<State>>> getStates(
			@RequestParam(name = "countryId", required = false) Short countryId,
			@RequestParam(name = "pageNumber", required = false) Integer pageNumber,
			@RequestParam(name = "pageSize", required = false) Integer limit) {
		State state = null;
		if (countryId != null || pageNumber != null || limit != null) {
			pageNumber = pageNumber != null ? pageNumber : 1;
			limit = limit != null ? limit : 10;
			Country country = new Country();
			country.setCountryId(countryId);
			state = new State();
			state.setCountry(country);
			state.setPageNumber(pageNumber);
			state.setPageSize(limit);
		}
		Response<List<State>> response = Response.buildResponse(stateService.fetchState(state), StatusType.SUCCESS,
				HttpStatus.OK.value(), SUCCESS_MSG);
		return new ResponseEntity<>(response, HttpStatus.valueOf(Integer.parseInt(response.getCode())));
	}

	@PostMapping(value = RgtResource.STATE_BY_ATTACHMENT_UPLOAD_URL, consumes = RgtResource.PRODUCES_MULTIPART_FORM_DATA)
	@ApiMetrics(methodName = "CreateState")
	@CalLog
	public ResponseEntity<CustomRespose<List<State>>> addStatesThroughExcel(
			@RequestPart(name = "file") MultipartFile multipartFile) {
		CustomRespose<List<State>> response = null;
		List<State> listStates = new ArrayList<>();
		List<State> errorStates = new ArrayList<>();
		List<State> savedStates = new ArrayList<>();
		try {
			if (BaseRestUtill.checkFileType(multipartFile)) {
				Workbook workbook = WorkbookFactory.create(multipartFile.getInputStream());
				Sheet sheet = workbook.getSheetAt(0);
				if (stateExcelHelper.createStateListThoughExcel(sheet, listStates, errorStates)) {
					if (listStates.isEmpty()) {
						response = CustomRespose.buildResponse(null, errorStates, null, errorStates.size(),
								StatusType.ERROR, HttpStatus.INTERNAL_SERVER_ERROR.value(),
								"States sheet missing required data or Duplicates are not allowed.");

					} else if (errorStates.isEmpty() || !listStates.isEmpty() || !errorStates.isEmpty()) {
						stateService.saveStates(listStates, savedStates);
						response = CustomRespose.buildResponse(savedStates, errorStates, savedStates.size(),
								errorStates.size(), StatusType.SUCCESS, HttpStatus.CREATED.value(),
								"Please check the response.");
					}
				}
			} else {
				response = CustomRespose.buildResponse(savedStates, errorStates, savedStates.size(), errorStates.size(),
						StatusType.ERROR, HttpStatus.INTERNAL_SERVER_ERROR.value(),
						"Only Excel files are allowed.Provide The Excel containing Member And Service And City.");
			}
		} catch (Exception e) {
			performExceptionLogger(e);
			response = CustomRespose.buildResponse(null, null, null, null, StatusType.ERROR,
					HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage());
		}
		String code = response != null ? response.getCode() : "500";
		return new ResponseEntity<>(response, HttpStatus.valueOf(Integer.parseInt(code)));
	}
	
	private void performExceptionLogger(Exception e) {
		logger.error("Exception Occured while executing service", e);
	}
}
