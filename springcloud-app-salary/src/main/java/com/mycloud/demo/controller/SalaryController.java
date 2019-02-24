package com.mycloud.demo.controller;

import com.mycloud.demo.entity.Employee;
import com.mycloud.demo.model.AppResponse;
import com.mycloud.demo.service.SalaryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class SalaryController {

    @Autowired
    private SalaryService service;

    @GetMapping(value = "test/hello")
    @ResponseBody
    public AppResponse<String> hello() {
        return AppResponse.buildSuccessResponse("Hello, this is app-salary.");
    }

    @GetMapping(value = "search-salary/all")
    @ResponseBody
    public AppResponse<List<Employee>> search() {
        List<Employee> results = service.findAll();
        return AppResponse.buildSuccessResponse(results);
    }

    @GetMapping(value = "search-salary/id/{id}")
    @ResponseBody
    public AppResponse<Employee> search(@PathVariable String id) {
        Employee result = service.findById(id);
        return AppResponse.buildSuccessResponse(result);
    }

    @GetMapping(value = "search-salary/me")
    @ResponseBody
    public AppResponse<Employee> searchMe() {
        Employee result = service.findMe();
        return AppResponse.buildSuccessResponse(result);
    }
}
