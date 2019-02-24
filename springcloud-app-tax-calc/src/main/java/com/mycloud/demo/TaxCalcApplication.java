package com.mycloud.demo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * URLs:<br>
 * http://localhost:9221/app-tax-calc/tax-calc/all<br>
 * http://localhost:9221/app-tax-calc/tax-calc/salary/{salary}<br>
 */
@SpringBootApplication
public class TaxCalcApplication implements CommandLineRunner {

    @Value("${app.property.env}")
    public String ENVIRONMENT;

    private static final Logger logger = LoggerFactory.getLogger(TaxCalcApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(TaxCalcApplication.class, args);
        logger.info("<<<<<<<<<< Application[springcloud-app-tax-calc] Started >>>>>>>>>>");
    }

    @Override
    public void run(String... args) throws Exception {
        logger.info(String.format("<<<<<<<<<< Environment : %s >>>>>>>>>>", ENVIRONMENT.toUpperCase()));
    }
}
