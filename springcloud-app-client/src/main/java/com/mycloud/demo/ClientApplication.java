package com.mycloud.demo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableOAuth2Client;

@SpringBootApplication
@EnableOAuth2Client // Enable oauth2 client. The client piece is provided by Spring Security OAuth2 and switched on by a
// different annotation @EnableOAuth2Client. Replace @EnableOAuth2Sso with the lower level annotation.
public class ClientApplication implements CommandLineRunner {

    // If your net is proxy net, you need add this.
    // If not,"java.net.UnknownHostException: github.com" will be occurred.
    static {
        System.setProperty("http.proxySet", "true");
        System.setProperty("http.proxyHost", "webproxy.wlb2.nam.nsroot.net");
        System.setProperty("http.proxyPort", "8080");
        System.setProperty("https.proxyHost", "webproxy.wlb2.nam.nsroot.net");
        System.setProperty("https.proxyPort", "8080");
    }

    @Value("${app.property.env}")
    public String ENVIRONMENT;

    private static final Logger logger = LoggerFactory.getLogger(ClientApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(ClientApplication.class, args);
        logger.info("<<<<<<<<<< Application[springcloud-app-client] Started >>>>>>>>>>");
    }

    @Override
    public void run(String... args) throws Exception {
        logger.info(String.format("<<<<<<<<<< Environment : %s >>>>>>>>>>", ENVIRONMENT.toUpperCase()));
    }
}
