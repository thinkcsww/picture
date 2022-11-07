package com.applory.pictureserver.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;

@Configuration
@EnableResourceServer
@RequiredArgsConstructor
public class Oauth2ResourceServerConfig extends ResourceServerConfigurerAdapter {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Override
    public void configure(HttpSecurity http) throws Exception {
        http.headers().frameOptions().disable();
        http.csrf().disable();
//        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        http.authorizeRequests()
                .antMatchers(HttpMethod.GET, "/api/v1/users/check-nickname").permitAll()
                .antMatchers(HttpMethod.GET, "/api/v1/users/seller/{id}").permitAll()
                .antMatchers(
                        "/api/v1/requests",
                        "/api/v1/users/seller",
                        "/api/v1/auth/login",
                        "/api/v1/auth/token/refresh",
                        "/ws/**",
                        "/swagger-resources/**",
                        "/swagger-ui.html",
                        "/swagger/**",
                        "/v3/api-docs",
                        "/webjars/**",
                        "/h2-console/**").permitAll()
                .anyRequest().authenticated();



        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
    }
}
