package com.mycloud.demo.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;

@Configuration
public class ResourceServerConfiguration extends ResourceServerConfigurerAdapter {

    @Override
    public void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests() //
                .antMatchers("/search-salary/all", "/search-salary/id/**").hasAnyRole("ADMIN") //
                .antMatchers("/search-salary/me").hasAnyRole("ADMIN", "USER") //
                .antMatchers("/test/hello").permitAll() //
                .anyRequest().authenticated();
    }
}
