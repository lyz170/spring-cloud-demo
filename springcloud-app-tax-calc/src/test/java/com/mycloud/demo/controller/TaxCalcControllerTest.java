package com.mycloud.demo.controller;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import net.minidev.json.JSONObject;
import net.minidev.json.JSONValue;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class TaxCalcControllerTest {

	private static final Logger logger = LoggerFactory.getLogger(TaxCalcControllerTest.class);

	@LocalServerPort
	private int port;

	@Autowired
	private TestRestTemplate restTemplate;

	@Test
	public void case1() {
		List<String> paramList = new ArrayList<>();
		paramList.add("5000");
		paramList.add("50000");
		ResponseEntity<String> responseEntity = restTemplate
				.postForEntity("http://localhost:" + port + "/app-tax-calc/tax-calc/all", paramList, String.class);
		String body = responseEntity.getBody();
		logger.info("The Response body is: " + body);
		JSONObject jsonObj = (JSONObject) JSONValue.parse(body);
		assertThat(jsonObj.get("status")).isEqualTo("200");
		assertThat(jsonObj.get("message").toString()).isEqualTo("OK");
		assertThat(jsonObj.get("content").toString()).isEqualTo("[\"0.00\",\"9090.00\"]");
	}
}
