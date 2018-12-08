package com.mycloud.demo.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.mycloud.demo.model.AppResponse;
import com.mycloud.demo.model.Employee;
import com.mycloud.demo.service.SalaryService;

@RestController
public class SalaryController {

	@Autowired
	private SalaryService service;

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
}