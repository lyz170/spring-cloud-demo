package com.mycloud.demo.service;

import com.mycloud.demo.call.CallAppTaxCalcService;
import com.mycloud.demo.entity.Employee;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class SalaryService {

    @Autowired
    private CallAppTaxCalcService callAppTaxCalcService;

    @Autowired
    private EmployeeService employeeService;


    public List<Employee> findAll() {

        List<Employee> results = employeeService.findAll();
        callAppTaxCalcService.fillTax(results);

        return results;
    }

    public Employee findById(String id) {

        Employee result = employeeService.findById(id);
        callAppTaxCalcService.fillTax(result);

        return result;
    }
}
