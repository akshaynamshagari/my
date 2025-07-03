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
import com.letmecall.rgt.model.CustomRespose;
import com.letmecall.rgt.model.Response;
import com.letmecall.rgt.model.StatusType;
import com.letmecall.rgt.rest.helper.BaseRestUtill;
import com.letmecall.rgt.rest.helper.CountryExcelHelper;
import com.letmecall.service.common.CountryService;

import jakarta.validation.Valid;

@RestController
@RequestMapping(value = RgtResource.COUNTRY_BASE_URL)
public class CountryController {

	private static final Logger logger = LoggerFactory.getLogger(CountryController.class);

	@Autowired
	CountryExcelHelper countryExcelHelper;

	@Autowired
	CountryService countryService;

	@PostMapping(consumes = RgtResource.CONSUMES, produces = RgtResource.PRODUCES)
	@ApiMetrics(methodName = "AddCountry")
	@CalLog
	public ResponseEntity<Response<Country>> addCountry(@RequestBody @Valid Country country) {
		Response<Country> response = null;
		try {
			country = countryService.createCountry(country);
			if (country != null) {
				response = Response.buildResponse(country, StatusType.SUCCESS, HttpStatus.CREATED.value(),
						"Country are added successfully.");
			} else {
				response = Response.buildResponse(country, StatusType.ERROR, HttpStatus.CREATED.value(),
						"Country are not added.");
			}
		} catch (Exception e) {
			logger.error("Exception Occured while executing service", e);
			response = Response.buildResponse(null, StatusType.ERROR, HttpStatus.INTERNAL_SERVER_ERROR.value(),
					e.getMessage());
		}
		return new ResponseEntity<>(response, HttpStatus.valueOf(Integer.parseInt(response.getCode())));
	}

	@PutMapping(value = RgtResource.COUNTRY_BY_ID, consumes = RgtResource.CONSUMES, produces = RgtResource.PRODUCES)
	@ApiMetrics(methodName = "UpdateCountry")
	@CalLog
	public ResponseEntity<Response<Country>> updateCountry(@PathVariable(name = "countryId") Short countryId,
			@RequestBody @Valid Country country) {
		Response<Country> response = null;
		try {
			country.setCountryId(countryId);
			country = countryService.updateCountry(country);
			if (country != null) {
				response = Response.buildResponse(country, StatusType.SUCCESS, HttpStatus.CREATED.value(),
						"Country are updated successfully.");
			} else {
				response = Response.buildResponse(country, StatusType.ERROR, HttpStatus.INTERNAL_SERVER_ERROR.value(),
						"Country are not not updated.");
			}
		} catch (Exception e) {
			logger.error("Exception Occured while executing service", e);
			response = Response.buildResponse(null, StatusType.ERROR, HttpStatus.INTERNAL_SERVER_ERROR.value(),
					e.getMessage());
		}
		return new ResponseEntity<>(response, HttpStatus.valueOf(Integer.parseInt(response.getCode())));
	}

	@DeleteMapping(value = RgtResource.COUNTRY_BY_ID)
	@ApiMetrics(methodName = "DeleteCountry")
	@CalLog
	public ResponseEntity<Response<Country>> deleteCountry(@PathVariable(name = "countryId") Short countryId) {
		Country country = new Country();
		country.setCountryId(countryId);
		Boolean isExgist = countryService.deleteCountry(country);
		Response<Country> response = null;
		if (isExgist != null && isExgist) {
			response = Response.buildResponse(null, StatusType.SUCCESS, HttpStatus.OK.value(),
					"Country are delete successfully.");
		} else {
			response = Response.buildResponse(null, StatusType.ERROR, HttpStatus.INTERNAL_SERVER_ERROR.value(),
					"Country are not found " + countryId + ".");
		}
		return new ResponseEntity<>(response, HttpStatus.valueOf(Integer.parseInt(response.getCode())));
	}

	@GetMapping()
	@ApiMetrics(methodName = "FetchCountrys")
	@CalLog
	public ResponseEntity<Response<List<Country>>> getCountrys(
			@RequestParam(name = "pageNumber", defaultValue = "1") Integer pageNumber,
			@RequestParam(name = "pageSize", defaultValue = "50") Integer limit) {
		Country country = new Country();
		country.setPageNumber(--pageNumber);
		country.setPageSize(limit);
		Response<List<Country>> response = Response.buildResponse(countryService.fetchCountry(country),
				StatusType.SUCCESS, HttpStatus.OK.value(), "Country are fetched successfully.");
		return new ResponseEntity<>(response, HttpStatus.valueOf(Integer.parseInt(response.getCode())));
	}

	@GetMapping(value = RgtResource.COUNTRY_BY_SEARCH)
	@ApiMetrics(methodName = "FetchCountryBySearch")
	@CalLog
	public ResponseEntity<Response<Country>> getCountryByIdOrName(
			@RequestParam(name = "countryId", required = false) Short countryId,
			@RequestParam(name = "countryName", required = false) String countryName) {
		Country country = new Country();
		country.setCountryId(countryId);
		country.setCountryName(countryName);
		if (countryId != null) {
			country = countryService.fetchCountryById(country);
		} else {
			country = countryService.fetchCountryByCountryDetails(country);
		}
		Response<Country> response = null;
		if (country != null) {
			response = Response.buildResponse(country, StatusType.SUCCESS, HttpStatus.OK.value(),
					"Country are fetched successfully.");
		} else {
			response = Response.buildResponse(country, StatusType.ERROR, HttpStatus.INTERNAL_SERVER_ERROR.value(),
					"Country are not found.");
		}
		return new ResponseEntity<>(response, HttpStatus.valueOf(Integer.parseInt(response.getCode())));
	}

	@PostMapping(value = RgtResource.COUNTRY_BY_ATTACHMENT_UPLOAD_URL, consumes = RgtResource.PRODUCES_MULTIPART_FORM_DATA)
	@ApiMetrics(methodName = "AddCountry")
	@CalLog
	public ResponseEntity<CustomRespose<List<Country>>> addCountryThroughExcel(
			@RequestPart(name = "file") MultipartFile multipartFile) {
		CustomRespose<List<Country>> response = null;
		List<Country> listCountry = new ArrayList<>();
		List<Country> errorCountry = new ArrayList<>();
		List<Country> savedCountry = new ArrayList<>();
		try {
			if (BaseRestUtill.checkFileType(multipartFile)) {
				Workbook workbook = WorkbookFactory.create(multipartFile.getInputStream());
				Sheet sheet = workbook.getSheetAt(0);
				countryExcelHelper.createCountryListThroughExcel(sheet, listCountry, errorCountry);
				if (listCountry.isEmpty()) {
					response = CustomRespose.buildResponse(null, errorCountry, null, errorCountry.size(),
							StatusType.ERROR, HttpStatus.INTERNAL_SERVER_ERROR.value(),
							"Country sheet missing required data or Duplicates are not allowed.");
				} else if (errorCountry.isEmpty()) {
					countryService.saveCountry(listCountry, savedCountry);
					response = CustomRespose.buildResponse(savedCountry, null, savedCountry.size(), null,
							StatusType.SUCCESS, HttpStatus.CREATED.value(), "Please check the response.");

				} else if (!listCountry.isEmpty() || !errorCountry.isEmpty()) {
					countryService.saveCountry(listCountry, savedCountry);
					response = CustomRespose.buildResponse(savedCountry, errorCountry, savedCountry.size(),
							errorCountry.size(), StatusType.SUCCESS, HttpStatus.CREATED.value(),
							"Please check the response.");
				}
			}
		} catch (Exception e) {
			logger.error("Exception Occured while executing country", e);
			response = CustomRespose.buildResponse(null, null, null, null, StatusType.ERROR,
					HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage());
		}
		String code = response != null ? response.getCode() : "500";
		return new ResponseEntity<>(response, HttpStatus.valueOf(Integer.parseInt(code)));
	}
}