package com.mycloud.demo.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.mycloud.demo.model.AppResponse;
import com.mycloud.demo.service.TaxCalcService;

@RestController
public class TaxCalcController {

	@Autowired
	private TaxCalcService service;

	@PostMapping(value = "tax-calc/all")
	@ResponseBody
	public AppResponse<List<String>> search(@RequestBody List<String> paramList) {
		List<String> result = service.calcAll(paramList);
		return AppResponse.buildSuccessResponse(result);
	}

	@GetMapping(value = "tax-calc/salary/{salary}")
	@ResponseBody
	public AppResponse<String> search(@PathVariable String salary) {
		String result = service.calc(salary);
		return AppResponse.buildSuccessResponse(result);
	}
}
