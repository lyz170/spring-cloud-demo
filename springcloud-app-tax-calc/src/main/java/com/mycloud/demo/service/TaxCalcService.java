package com.mycloud.demo.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.stereotype.Service;

import com.mycloud.demo.config.AppException;

@Service
public class TaxCalcService {

	private final static BigDecimal LINE_5000 = new BigDecimal("5000");
	private final static BigDecimal LINE_8000 = new BigDecimal("8000");
	private final static BigDecimal LINE_17000 = new BigDecimal("17000");
	private final static BigDecimal LINE_30000 = new BigDecimal("30000");
	private final static BigDecimal LINE_40000 = new BigDecimal("40000");
	private final static BigDecimal LINE_60000 = new BigDecimal("60000");
	private final static BigDecimal LINE_85000 = new BigDecimal("85000");

	private final static BigDecimal LEVEL1 = new BigDecimal("0.03");
	private final static BigDecimal LEVEL2 = new BigDecimal("0.1");
	private final static BigDecimal LEVEL3 = new BigDecimal("0.2");
	private final static BigDecimal LEVEL4 = new BigDecimal("0.25");
	private final static BigDecimal LEVEL5 = new BigDecimal("0.30");
	private final static BigDecimal LEVEL6 = new BigDecimal("0.35");
	private final static BigDecimal LEVEL7 = new BigDecimal("0.45");

	private final static BigDecimal NUMBER1 = LINE_8000.subtract(LINE_5000).multiply(LEVEL1);
	private final static BigDecimal NUMBER2 = LINE_17000.subtract(LINE_8000).multiply(LEVEL2).add(NUMBER1);
	private final static BigDecimal NUMBER3 = LINE_30000.subtract(LINE_17000).multiply(LEVEL3).add(NUMBER2);
	private final static BigDecimal NUMBER4 = LINE_40000.subtract(LINE_30000).multiply(LEVEL4).add(NUMBER3);
	private final static BigDecimal NUMBER5 = LINE_60000.subtract(LINE_40000).multiply(LEVEL5).add(NUMBER4);
	private final static BigDecimal NUMBER6 = LINE_85000.subtract(LINE_60000).multiply(LEVEL6).add(NUMBER5);

	public List<String> calcAll(List<String> paramList) {

		List<String> result = new ArrayList<>();
		paramList.forEach(salsry -> result.add(calc(salsry)));

		return result;
	}

	public String calc(String salary) {

		if (StringUtils.isEmpty(salary)) {
			throw new AppException("Should not be null.");
		}

		BigDecimal param = null;
		try {
			param = NumberUtils.createBigDecimal(salary);
		} catch (NumberFormatException e) {
			throw new AppException(e);
		}

		return getTax(param).toString();
	}

	private static BigDecimal getTax(BigDecimal salary) {

		BigDecimal result = null;

		if (salary.compareTo(LINE_5000) <= 0) {
			result = BigDecimal.ZERO;
		} else if (salary.compareTo(LINE_5000) > 0 && salary.compareTo(LINE_8000) <= 0) {
			result = salary.subtract(LINE_5000).multiply(LEVEL1);
		} else if (salary.compareTo(LINE_8000) > 0 && salary.compareTo(LINE_17000) <= 0) {
			result = salary.subtract(LINE_8000).multiply(LEVEL2).add(NUMBER1);
		} else if (salary.compareTo(LINE_17000) > 0 && salary.compareTo(LINE_30000) <= 0) {
			result = salary.subtract(LINE_17000).multiply(LEVEL3).add(NUMBER2);
		} else if (salary.compareTo(LINE_30000) > 0 && salary.compareTo(LINE_40000) <= 0) {
			result = salary.subtract(LINE_30000).multiply(LEVEL4).add(NUMBER3);
		} else if (salary.compareTo(LINE_40000) > 0 && salary.compareTo(LINE_60000) <= 0) {
			result = salary.subtract(LINE_40000).multiply(LEVEL5).add(NUMBER4);
		} else if (salary.compareTo(LINE_60000) > 0 && salary.compareTo(LINE_85000) <= 0) {
			result = salary.subtract(LINE_60000).multiply(LEVEL6).add(NUMBER5);
		} else {
			result = salary.subtract(LINE_85000).multiply(LEVEL7).add(NUMBER6);
		}

		return result.setScale(2, BigDecimal.ROUND_DOWN);
	}
}
