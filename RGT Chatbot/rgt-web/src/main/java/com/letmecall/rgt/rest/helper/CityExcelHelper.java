package com.letmecall.rgt.rest.helper;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.letmecall.rgt.domain.common.City;
import com.letmecall.rgt.domain.common.Country;
import com.letmecall.rgt.domain.common.State;
import com.letmecall.rgt.model.CustomRespose;
import com.letmecall.service.common.CityService;
import com.letmecall.service.common.StateService;

@Component
public class CityExcelHelper {

	private static final Logger logger = LoggerFactory.getLogger(CityExcelHelper.class);

	Map<String, State> stateMap = new HashMap<>();

	@Autowired
	private CityService cityService;
	@Autowired
	private StateService stateService;

	public boolean createCityListThoughExcel(Sheet sheet, CustomRespose<List<City>> customRespose) {
		List<City> listCity = customRespose.getSuccessResponse();
		List<City> errorCity = customRespose.getErrorRepose();
		for (Row row : sheet) {
			if (row.getRowNum() == 0) {
				continue;
			}
			City city = new City();
			boolean isRowEmpty = false;
			if (row.getRowNum() >= 1) {
				isRowEmpty = cityRowValidation(row, isRowEmpty);
				if (!isRowEmpty) {
					buildCity(row, city);
					if (cityValidation(city)) {
						errorCity.add(city);
					} else {
						validateCityAndAddCityList(listCity, errorCity, city);
					}
				}
			}
		}
		return true;
	}

	private void validateCityAndAddCityList(List<City> listCity, List<City> errorCity, City city) {
		State state = stateMap.get(city.getState().getStateName());
		if (state != null) {
			city.setState(state);
		} else {
			state = stateService.fetchStateByStateDetails(city.getState());
			if (state == null)
				logger.error("State is not exists: State={},Country={}", city.getState().getStateName(),
						city.getState().getCountry().getCountryName());
			else
				stateMap.put(city.getState().getStateName(), state);
		}
		if (state != null)
			validateCity(listCity, errorCity, city);
	}

	private boolean cityValidation(City city) {
		return city.getCityName() == null || !isAlpha(city.getCityName()) || city.getState().getStateName() == null;
	}

	private boolean cityRowValidation(Row row, boolean isRowEmpty) {
		int cellCount = 0;
		int nullCount = 0;
		Iterator<Cell> cellIterator = row.cellIterator();
		while (cellIterator.hasNext()) {
			cellCount++;
			Cell cell = cellIterator.next();
			if ((cell == null) || (cell.getCellType() == Cell.CELL_TYPE_BLANK)) {
				nullCount++;
			}
		}
		if ((cellCount != 0) && (nullCount != 0) && (cellCount == nullCount)) {
			isRowEmpty = true;
		}
		return isRowEmpty;
	}

	private City buildCity(Row row, City city) {
		Cell cell0 = row.getCell(0);
		if (cell0 != null && cell0.getColumnIndex() == 0) {
			city.setCityName(cell0.getStringCellValue());
		}
		Cell cell1 = row.getCell(1);
		if (cell1 != null && cell1.getColumnIndex() == 1) {
			State state = new State();
			state.setCountry(new Country());
			state.setStateName(cell1.getStringCellValue());
			// country
			Cell cell2 = row.getCell(2);
			if (cell2 != null && cell2.getColumnIndex() == 2) {
				state.getCountry().setCountryName(cell2.getStringCellValue());
			}
			city.setState(state);
		}

		Cell cell3 = row.getCell(3);
		if (cell3 != null && cell3.getColumnIndex() == 3) {
			city.setActive(cell3.getBooleanCellValue());
		}
		return city;
	}

	private void validateCity(List<City> listCity, List<City> errorCity, City city) {
		if (cityService.fetchCityDetails(city) != null) {
			errorCity.add(city);
		} else {
			listCity.add(city);
		}
	}

	public boolean isAlpha(String string) {
		Pattern pattern = Pattern.compile("^[a-zA-Z0-9 -]*$");
		Matcher matcher = pattern.matcher(string);
		return matcher.matches();
	}
}
