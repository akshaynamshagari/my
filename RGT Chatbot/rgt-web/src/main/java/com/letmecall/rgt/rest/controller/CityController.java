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
import com.letmecall.rgt.domain.common.City;
import com.letmecall.rgt.model.CustomRespose;
import com.letmecall.rgt.model.Response;
import com.letmecall.rgt.model.StatusType;
import com.letmecall.rgt.rest.helper.BaseRestUtill;
import com.letmecall.rgt.rest.helper.CityExcelHelper;
import com.letmecall.service.common.CityService;

import jakarta.validation.Valid;

@RestController
@RequestMapping(value = RgtResource.CITY_BASE_URL)
public class CityController {

	private static final Logger logger = LoggerFactory.getLogger(CityController.class);

	@Autowired
	CityService cityService;

	@Autowired
	CityExcelHelper cityExcelHelper;

	private static final String SUCCESS_MSG = "City are fetched successfully.";

	@PostMapping()
	@ApiMetrics(methodName = "CreateCity")
	@CalLog
	public ResponseEntity<Response<City>> addCity(@RequestBody @Valid City city) {
		Response<City> response = null;
		try {
			city = cityService.createCity(city);
			if (city != null) {
				response = Response.buildResponse(city, StatusType.SUCCESS, HttpStatus.OK.value(),
						"City are added successfully.");
			} else {
				response = Response.buildResponse(city, StatusType.ERROR, HttpStatus.INTERNAL_SERVER_ERROR.value(),
						"City are not added..");
			}
		} catch (Exception e) {
			logger.error("Exception Occured while executing service", e);
			response = Response.buildResponse(null, StatusType.ERROR, HttpStatus.INTERNAL_SERVER_ERROR.value(),
					e.getMessage());

		}
		return new ResponseEntity<>(response, HttpStatus.valueOf(Integer.parseInt(response.getCode())));
	}

	@PutMapping(value = RgtResource.CITY_BY_ID)
	@ApiMetrics(methodName = "UpdateCity")
	@CalLog
	public ResponseEntity<Response<City>> updateCity(@PathVariable(name = "cityId") Integer cityId,
			@RequestBody @Valid City city) {
		Response<City> response = null;
		try {
			city.setCityId(cityId);
			city = cityService.updateCity(city);
			if (city != null) {
				response = Response.buildResponse(city, StatusType.SUCCESS, HttpStatus.OK.value(),
						"City are updated successfully.");
			} else {
				response = Response.buildResponse(city, StatusType.ERROR, HttpStatus.INTERNAL_SERVER_ERROR.value(),
						"City are not not updated.");
			}
		} catch (Exception e) {
			logger.error("Exception Occured while executing service", e);
			response = Response.buildResponse(null, StatusType.ERROR, HttpStatus.INTERNAL_SERVER_ERROR.value(),
					e.getMessage());

		}
		return new ResponseEntity<>(response, HttpStatus.valueOf(Integer.parseInt(response.getCode())));
	}

	@GetMapping(value = RgtResource.CITY_BY_SEARCH)
	@ApiMetrics(methodName = "FetchCity")
	@CalLog
	public ResponseEntity<Response<City>> getCityBySearch(
			@RequestParam(name = "cityName", required = false) String cityName,
			@RequestParam(name = "cityId", required = false) Integer cityId) {
		Response<City> response = null;
		City city = new City();
		city.setCityId(cityId);
		city.setCityName(cityName);

		city = cityService.fetchCityDetails(city);
		if (city != null) {
			response = Response.buildResponse(city, StatusType.SUCCESS, HttpStatus.OK.value(), SUCCESS_MSG);
		} else {
			response = Response.buildResponse(city, StatusType.ERROR, HttpStatus.INTERNAL_SERVER_ERROR.value(),
					"City are not found " + cityName + ".");
		}
		return new ResponseEntity<>(response, HttpStatus.valueOf(Integer.parseInt(response.getCode())));
	}

	@GetMapping()
	@ApiMetrics(methodName = "FetchCitys")
	@CalLog
	public ResponseEntity<Response<List<City>>> getAllCities(
			@RequestParam(name = "offset", defaultValue = "1") Integer offset,
			@RequestParam(name = "pageSize", defaultValue = "50") Integer limit,
			@RequestParam(name = "stateId", required = false) Short stateId) {
		Response<List<City>> response = Response.buildResponse(cityService.fetchAllCities(stateId, --offset, limit),
				StatusType.SUCCESS, HttpStatus.OK.value(), SUCCESS_MSG);
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	@GetMapping(value = RgtResource.STATE_BY_ACTIVITE_CITIES)
	@ApiMetrics(methodName = "FetchActiveCitys")
	@CalLog
	public ResponseEntity<Response<List<City>>> getAllActiveCities(@PathVariable(name = "stateId") Short stateId,
			@RequestParam(name = "offset", defaultValue = "all") String status) {
		Response<List<City>> response = Response.buildResponse(cityService.fetchActiveCities(stateId, status),
				StatusType.SUCCESS, HttpStatus.OK.value(), SUCCESS_MSG);
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	@DeleteMapping(value = RgtResource.CITY_BY_ID)
	@ApiMetrics(methodName = "DeleteCity")
	@CalLog
	public ResponseEntity<Response<City>> deleteCity(@PathVariable(name = "cityId") Integer cityID) {
		City city = new City();
		city.setCityId(cityID);
		Response<City> response = null;
		try {
			Boolean isExist = cityService.deleteCityById(city);
			if (isExist != null && isExist) {
				response = Response.buildResponse(null, StatusType.SUCCESS, HttpStatus.OK.value(),
						" city delete successfully.");
			} else {
				response = Response.buildResponse(null, StatusType.ERROR, HttpStatus.INTERNAL_SERVER_ERROR.value(),
						" city not found " + cityID + ".");
			}
		} catch (Exception e) {
			logger.error("Exception Occured while executing city", e);
			response = Response.buildResponse(null, StatusType.ERROR, HttpStatus.INTERNAL_SERVER_ERROR.value(),
					e.getMessage());
		}

		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	@PostMapping(value = RgtResource.CITY_BY_ATTACHMENT_UPLOAD_URL, consumes = RgtResource.PRODUCES_MULTIPART_FORM_DATA)
	@ApiMetrics(methodName = "AddCity")
	@CalLog
	public ResponseEntity<CustomRespose<List<City>>> addCityThroughExcel(
			@RequestPart(name = "file") MultipartFile multipartFile) {
		CustomRespose<List<City>> response = null;
		List<City> listCity = new ArrayList<>();
		List<City> errorCity = new ArrayList<>();
		List<City> savedCityList = new ArrayList<>();
		CustomRespose<List<City>> customRespose = CustomRespose.buildResponse(listCity, errorCity, null, null,
				StatusType.ERROR, 0, null);
		try {
			if (BaseRestUtill.checkFileType(multipartFile)) {
				Workbook workbook = WorkbookFactory.create(multipartFile.getInputStream());
				Sheet sheet = workbook.getSheetAt(0);
				if (cityExcelHelper.createCityListThoughExcel(sheet, customRespose)) {
					if (listCity.isEmpty()) {
						response = CustomRespose.buildResponse(null, errorCity, null, errorCity.size(), StatusType.ERROR,
								HttpStatus.INTERNAL_SERVER_ERROR.value(),
								"city data sheet missing required data or Duplicates are not allowed.");

					} else if (errorCity.isEmpty()) {
						cityService.saveCity(listCity, savedCityList);
						response = CustomRespose.buildResponse(savedCityList, null, savedCityList.size(), null,
								StatusType.SUCCESS, HttpStatus.CREATED.value(), "Please check the response.");
					} else if (!errorCity.isEmpty() || !listCity.isEmpty()) {
						cityService.saveCity(listCity, savedCityList);
						response = CustomRespose.buildResponse(savedCityList, errorCity, savedCityList.size(),
								errorCity.size(), StatusType.SUCCESS, HttpStatus.CREATED.value(),
								"Please check the response.");
					}

				} else {
					response = CustomRespose.buildResponse(savedCityList, errorCity, savedCityList.size(),
							errorCity.size(), StatusType.ERROR, HttpStatus.INTERNAL_SERVER_ERROR.value(),
							"Only Excel files are allowed.Provide excel containing city name and state name");
				}
			}
		} catch (Exception e) {
			logger.error("Exception Occured while adding city", e);
			response = CustomRespose.buildResponse(null, null, null, null, StatusType.ERROR,
					HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage());
		}
		String code = response != null ? response.getCode() : "500";
		return new ResponseEntity<>(response, HttpStatus.valueOf(Integer.parseInt(code)));

	}

}
