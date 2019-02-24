package com.mycloud.demo.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;

@Configuration
public class ResourceServerConfiguration extends ResourceServerConfigurerAdapter {

    @Override
    public void configure(HttpSecurity http) throws Exception {
        // [IMPORTANT] 为什么要提前加antMatcher? 可以看一下antMatcher()的注释
        // Allows configuring the HttpSecurity to only be invoked when matching the provided ant pattern.
        http.antMatcher("/user").authorizeRequests() //
                // .antMatchers("xxx", "xxx").permitAll() //
                .anyRequest().authenticated();
    }
}
