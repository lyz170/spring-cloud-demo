package com.mycloud.demo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.config.server.EnableConfigServer;

/**
 * Config支持我们使用的请求的参数规则为：<br>
 * /{应用名}/{环境名}[/{分支名}]<br>
 * /{应用名}-{环境名}.yml<br>
 * /{应用名}-{环境名}.properties<br>
 * /{分支名}/{应用名}-{环境名}.yml<br>
 * /{分支名}/{应用名}-{环境名}.properties<br>
 * 
 * URLs:<br>
 * [app-salary]<br>
 * http://localhost:9111/server-config/app-salary/default::http://localhost:9111/server-config/app-salary.yml<br>
 * http://localhost:9111/server-config/app-salary/dev::http://localhost:9111/server-config/app-salary-dev.yml<br>
 * http://localhost:9111/server-config/app-salary/prod::http://localhost:9111/server-config/app-salary-prod.yml<br>
 * [app-tax-calc]<br>
 * http://localhost:9111/server-config/app-tax-calc/default::http://localhost:9111/server-config/app-tax-calc.yml<br>
 * http://localhost:9111/server-config/app-tax-calc/dev::http://localhost:9111/server-config/app-tax-calc-dev.yml<br>
 * http://localhost:9111/server-config/app-tax-calc/prod::http://localhost:9111/server-config/app-tax-calc-prod.yml<br>
 * [server-eureka]<br>
 * http://localhost:9111/server-config/server-eureka/default::http://localhost:9111/server-config/server-eureka.yml<br>
 * http://localhost:9111/server-config/server-eureka/dev::http://localhost:9111/server-config/server-eureka-dev.yml<br>
 * http://localhost:9111/server-config/server-eureka/prod::http://localhost:9111/server-config/server-eureka-prod.yml<br>
 */
@SpringBootApplication
@EnableConfigServer
public class ConfigServerApplication {

	private static final Logger logger = LoggerFactory.getLogger(ConfigServerApplication.class);

	public static void main(String[] args) {
		SpringApplication.run(ConfigServerApplication.class, args);
		logger.info("<<<<<<<<<< Application[springcloud-server-config] Started >>>>>>>>>>");
	}
}
