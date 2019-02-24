package com.mycloud.demo.config;

import java.util.Collections;
import java.util.List;

import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.web.client.RestTemplate;

import com.mycloud.demo.usercontext.UserContextInterceptor;

@Configuration
public class AppConfiguration {

    /**
     * 详细请看：CallAppTaxCalcService -> (2)使用带有Ribbon功能的Spring RestTemplate调用服务<br>
     */
    @LoadBalanced
    @Bean
    public RestTemplate loadbalancedRestTemplate() {

        RestTemplate template = new RestTemplate();

        List<ClientHttpRequestInterceptor> interceptors = template.getInterceptors();
        if (interceptors == null) {
            template.setInterceptors(Collections.singletonList(new UserContextInterceptor()));
        } else {
            interceptors.add(new UserContextInterceptor());
            template.setInterceptors(interceptors);
        }

        return template;
    }
}
