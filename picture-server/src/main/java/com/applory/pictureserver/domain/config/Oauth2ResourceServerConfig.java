package com.applory.pictureserver.domain.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;

@Configuration
@EnableResourceServer
public class Oauth2ResourceServerConfig extends ResourceServerConfigurerAdapter {

    @Override
    public void configure(HttpSecurity http) throws Exception {
        http.headers().frameOptions().disable();
        http.authorizeRequests()
                .antMatchers(HttpMethod.POST, "/api/v1/users").permitAll()
                .antMatchers(
                        "/api/v1/auth/login",
                        "/api/v1/auth/token/refresh",
                        "/h2-console/**").permitAll()
                .anyRequest().authenticated();
    }
}
