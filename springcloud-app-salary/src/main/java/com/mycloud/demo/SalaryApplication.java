package com.mycloud.demo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;

/**
 * URLs:<br>
 * http://localhost:9211/app-salary/search-salary/all?access_token=xxx<br>
 * http://localhost:9211/app-salary/search-salary/id/{id}?access_token=xxx<br>
 * http://localhost:9211/app-salary/search-salary/me?access_token=xxx<br>
 */
@SpringBootApplication
@EnableCircuitBreaker // Enable eureka server with a Hystrix circuit breaker
@EnableResourceServer // // Enable OAuth2 Resource Server
public class SalaryApplication implements CommandLineRunner {

    @Value("${app.property.env}")
    public String ENVIRONMENT;

    private static final Logger logger = LoggerFactory.getLogger(SalaryApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(SalaryApplication.class, args);
        logger.info("<<<<<<<<<< Application[springcloud-app-salary] Started >>>>>>>>>>");
    }

    @Override
    public void run(String... args) throws Exception {
        logger.info(String.format("<<<<<<<<<< Environment : %s >>>>>>>>>>", ENVIRONMENT.toUpperCase()));
    }
}
