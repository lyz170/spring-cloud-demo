package com.mycloud.demo.service;

import com.mycloud.demo.call.CallAppTaxCalcService;
import com.mycloud.demo.config.AppException;
import com.mycloud.demo.entity.Employee;
import com.mycloud.demo.repository.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class SalaryService {

    @Autowired
    private CallAppTaxCalcService callAppTaxCalcService;

    @Autowired
    private EmployeeRepository employeeRepository;

    public List<Employee> findAll() {

        List<Employee> results = employeeRepository.findAll();
        callAppTaxCalcService.fillTax(results);

        return results;
    }

    public Employee findById(String id) {

        Optional<Employee> optResult = employeeRepository.findById(id);

        if (!optResult.isPresent()) {
            throw new AppException(String.format("Can not find the employee which id = %s.", id));
        }

        Employee result = optResult.get();
        callAppTaxCalcService.fillTax(result);

        return result;
    }
}
