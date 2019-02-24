package com.mycloud.demo.service;

import java.util.LinkedHashMap;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mycloud.demo.call.CallAppTaxCalcService;
import com.mycloud.demo.config.AppException;
import com.mycloud.demo.entity.Employee;

@Service
@Transactional
public class SalaryService {

    private static final Logger logger = LoggerFactory.getLogger(SalaryService.class);

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

    public Employee findMe() {

        String name = null;
        try {
            name = getName();
        } catch (Exception e) {
            throw new AppException("Authentication is null!");
        }
        logger.info("Authentication is {}", name);
        String id = name.substring(4);

        return findById(id);
    }

    private String getName() throws Exception {
        OAuth2Authentication auth = (OAuth2Authentication) SecurityContextHolder.getContext().getAuthentication();
        @SuppressWarnings("unchecked")
        LinkedHashMap<String, Object> userDetails = (LinkedHashMap<String, Object>) auth.getUserAuthentication()
                .getPrincipal();
        return (String) userDetails.get("username");
    }
}
