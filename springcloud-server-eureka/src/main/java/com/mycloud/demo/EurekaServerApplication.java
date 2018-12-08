package com.mycloud.demo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

/**
 * URLs:<br>
 * http://localhost:9121/server-eureka<br>
 * http://localhost:9121/server-eureka/eureka/apps/app-salary<br>
 * http://localhost:9121/server-eureka/eureka/apps/app-tax-calc<br>
 */
@SpringBootApplication
@EnableEurekaServer
public class EurekaServerApplication implements CommandLineRunner {

    @Value("${app.property.env}")
    public String ENVIRONMENT;

    private static final Logger logger = LoggerFactory.getLogger(EurekaServerApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(EurekaServerApplication.class, args);
        logger.info("<<<<<<<<<< Application[springcloud-server-eureka] Started >>>>>>>>>>");
    }

    @Override
    public void run(String... args) throws Exception {
        logger.info(String.format("<<<<<<<<<< Environment : %s >>>>>>>>>>", ENVIRONMENT.toUpperCase()));
    }
}