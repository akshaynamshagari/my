package com.letmecall.rgt.rest.helper;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.letmecall.rgt.domain.common.Country;
import com.letmecall.rgt.domain.common.State;
import com.letmecall.service.common.CountryService;
import com.letmecall.service.common.StateService;

@Component
public class StateExcelHelper {

	private static final Logger logger = LoggerFactory.getLogger(StateExcelHelper.class);

	Map<String, Country> countryMap = new HashMap<>();

	@Autowired
	StateService stateService;

	@Autowired
	CountryService countryService;

	public boolean createStateListThoughExcel(Sheet sheet, List<State> listStates, List<State> errorStates) {
		for (Row row : sheet) {
			if (row.getRowNum() == 0) {
				continue;
			}
			if (row.getRowNum() >= 1 && !stateRowValidation(row)) {
				State state = new State();
				buildState(row, state);
				validateStateAndAddStateList(listStates, errorStates, state);
			}
		}
		return true;
	}

	private void validateStateAndAddStateList(List<State> listStates, List<State> errorStates, State state) {
		if (validateState(state)) {
			errorStates.add(state);
		} else if (!isvalidData(state)) {
			errorStates.add(state);
		} else if (state.getCountry() != null) {
			Country country = countryMap.get(state.getCountry().getCountryName());
			if (country == null) {
				country = countryService.fetchCountryByCountryDetails(state.getCountry());
				if (country == null) {
					errorStates.add(state);
					logger.error("Country is not exists: country={}", state.getCountry().getCountryName());
				} else {
					countryMap.put(state.getCountry().getCountryName(), country);
					state.setCountry(country);
					validateState(listStates, errorStates, state);
				}
			} else {
				state.setCountry(country);
				validateState(listStates, errorStates, state);
			}

		}
	}

	private boolean validateState(State state) {
		return state.getStateName() == null || state.getCountry().getCountryName() == null
				|| state.getCodeAlpha2() == null || state.getCodeAlpha3() == null || state.getCodeNumeric3() == null
				|| state.getActive() == null;
	}

	private void buildState(Row row, State state) {
		Cell cell0 = row.getCell(0);
		cell0.setCellType(Cell.CELL_TYPE_STRING);
		if (isValidCell(cell0, 0)) {
			cell0.setCellType(Cell.CELL_TYPE_STRING);
			state.setStateName(cell0.getStringCellValue());
		}
		Cell cell1 = row.getCell(1);
		cell1.setCellType(Cell.CELL_TYPE_STRING);
		if (isValidCell(cell1, 1)) {
			cell1.setCellType(Cell.CELL_TYPE_STRING);
			state.setCodeAlpha2(cell1.getStringCellValue());
		}
		Cell cell2 = row.getCell(2);
		cell2.setCellType(Cell.CELL_TYPE_STRING);
		if (isValidCell(cell2, 2)) {
			cell2.setCellType(Cell.CELL_TYPE_STRING);
			state.setCodeAlpha3(cell2.getStringCellValue());
		}

		Cell cell3 = row.getCell(3);
		if (isValidCell(cell3, 3)) {
			cell3.setCellType(Cell.CELL_TYPE_STRING);
			state.setCodeNumeric3(cell3.getStringCellValue());
		}
		Cell cell4 = row.getCell(4);
		if (isValidCell(cell4, 4) && cell4.getCellType() == Cell.CELL_TYPE_BOOLEAN) {
			state.setActive(cell4.getBooleanCellValue());
		}
		Cell cell5 = row.getCell(5);
		if (isValidCell(cell5, 5)) {
			Country country = new Country();
			country.setCountryName(cell5.getStringCellValue());
			state.setCountry(country);
		}
	}

	private boolean stateRowValidation(Row row) {
		boolean isRowEmpty = false;
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

	private void validateState(List<State> listStates, List<State> errorStates, State state) {
		if (stateService.fetchStateByStateDetailsCount(state) > 0) {
			errorStates.add(state);
			logger.error("State is exists with us: state={}", state.getStateName());
		} else {
			listStates.add((state));
		}
	}

	private boolean isValidCell(Cell cell, int index) {
		return Objects.nonNull(cell) && cell.getColumnIndex() == index;
	}

	private boolean isvalidData(State state) {
		boolean value = true;
		String codeAlpha3Pattern = "^[a-zA-Z]([\\w -]*[a-zA-Z])?$";
		boolean stateName = StringUtils.isAlphaSpace(state.getStateName());
		boolean countryName = StringUtils.isAlphaSpace(state.getCountry().getCountryName());
		boolean codeAlpha3 = isPatternData(state.getCodeAlpha3(), codeAlpha3Pattern);
		boolean codeAlpha2 = StringUtils.isAlpha(state.getCodeAlpha2());
		boolean codeNumeric = StringUtils.isNumeric(state.getCodeNumeric3());
		if (!stateName || !countryName || !codeAlpha3 || !codeAlpha2 || !codeNumeric) {
			value = false;
		}
		return value;
	}

	public boolean isPatternData(String data, String patternData) {
		Pattern pattern;
		Matcher matcher;
		pattern = Pattern.compile(patternData);
		matcher = pattern.matcher(data);
		return matcher.matches();
	}
}
