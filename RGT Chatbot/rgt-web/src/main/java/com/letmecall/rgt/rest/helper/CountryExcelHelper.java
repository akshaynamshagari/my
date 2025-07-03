package com.letmecall.rgt.rest.helper;

import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.letmecall.rgt.domain.common.Country;
import com.letmecall.service.common.CountryService;

@Component
public class CountryExcelHelper {

	@Autowired
	private CountryService countryService;

	public boolean createCountryListThroughExcel(Sheet sheet, List<Country> listCountry, List<Country> errorCountry) {
		for (Row row : sheet) {
			if (row.getRowNum() == 0) {
				continue;
			}
			if (row.getRowNum() >= 1) {
				Country country = new Country();
				boolean isRowEmpty = countryRowValidation(row);
				if (!isRowEmpty) {
					buildCountry(row, country);
					validateCountryAndAddCountryList(listCountry, errorCountry, country);
				}
			}
		}
		return true;
	}

	private void validateCountryAndAddCountryList(List<Country> listCountry, List<Country> errorCountry,
			Country country) {
		if (countryValidation(country)) {
			errorCountry.add(country);
		} else if (!isvalidData(country)) {
			errorCountry.add(country);
		} else if (countryService.fetchCountryByCountryDetailsCount(country) > 0) {
			errorCountry.add(country);
		} else {
			listCountry.add(country);
		}
	}

	private boolean countryValidation(Country country) {
		return country.getCountryName() == null || country.getCodeAlpha2() == null || country.getCodeAlpha3() == null
				|| country.getActive() == null || country.getCountryCode() == null
				|| StringUtils.isBlank(country.getCodeAlpha2()) || StringUtils.isBlank(country.getCodeNumeric3())
				|| StringUtils.isBlank(country.getCodeAlpha3()) || StringUtils.isBlank(country.getCountryCode());
	}

	private void buildCountry(Row row, Country country) {
		Cell cell0 = row.getCell(0);
		if (cell0 != null && cell0.getColumnIndex() == 0) {
			cell0.setCellType(Cell.CELL_TYPE_STRING);
			country.setCountryName(cell0.getStringCellValue());
		}
		Cell cell1 = row.getCell(1);
		if (cell1 != null && cell1.getColumnIndex() == 1) {
			cell1.setCellType(Cell.CELL_TYPE_STRING);
			country.setCodeAlpha2(cell1.getStringCellValue());
		}
		Cell cell2 = row.getCell(2);
		if (cell2 != null && cell2.getColumnIndex() == 2) {
			cell2.setCellType(Cell.CELL_TYPE_STRING);
			country.setCodeAlpha3(cell2.getStringCellValue());
		}
		Cell cell3 = row.getCell(3);
		if (cell3 != null && cell3.getColumnIndex() == 3) {
			cell3.setCellType(Cell.CELL_TYPE_STRING);
			country.setCodeNumeric3(cell3.getStringCellValue());
		}
		Cell cell4 = row.getCell(4);
		if (cell4 != null && cell4.getColumnIndex() == 4) {
			cell4.setCellType(Cell.CELL_TYPE_STRING);
			country.setCountryCode(cell4.getStringCellValue());
		}
		Cell cell5 = row.getCell(5);
		if (cell5 != null && cell5.getColumnIndex() == 5 && cell5.getCellType() == Cell.CELL_TYPE_BOOLEAN) {
			country.setActive(cell5.getBooleanCellValue());
		}
	}

	private boolean countryRowValidation(Row row) {
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

	private boolean isvalidData(Country country) {
		String namePattern = "[a-zA-Z]+(\\s+[a-zA-Z]+)*";
		boolean status = true;
		if (!isPatternData(country.getCountryName(), namePattern) || !StringUtils.isAlpha(country.getCodeAlpha2())
				|| !StringUtils.isAlpha(country.getCodeAlpha3()) || !StringUtils.isNumeric(country.getCountryCode())
				|| !StringUtils.isNumeric(country.getCodeNumeric3())) {
			status = false;
		}
		return status;
	}

	public boolean isPatternData(String data, String patternData) {
		Pattern pattern;
		Matcher matcher;
		pattern = Pattern.compile(patternData);
		matcher = pattern.matcher(data);
		return matcher.matches();
	}
}
