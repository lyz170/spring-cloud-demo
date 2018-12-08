package com.mycloud.demo.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mycloud.demo.call.CallAppTaxCalcService;
import com.mycloud.demo.config.AppException;
import com.mycloud.demo.model.Employee;

@Service
public class SalaryService {

	@Autowired
	private CallAppTaxCalcService callAppTaxCalcService;

	public List<Employee> findAll() {

		List<Employee> results = Employee.getEmployees();
		callAppTaxCalcService.fillTax(results);

		return results;
	}

	public Employee findById(String id) {
		Long idLong = null;
		try {
			idLong = Long.valueOf(id);
		} catch (NumberFormatException e) {
			throw new AppException(e);
		}
		Employee result = Employee.getEmployee(idLong);

		if (result == null) {
			throw new AppException(String.format("Can not find the employee which id = %s.", id));
		}

		callAppTaxCalcService.fillTax(result);

		return result;
	}
}
