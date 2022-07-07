package com.applory.pictureserverkt.config

import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter

@Configuration
@EnableResourceServer
class OauthResourceServerConfig: ResourceServerConfigurerAdapter() {



    override fun configure(http: HttpSecurity) {
        http.headers().frameOptions().disable()
        http.authorizeRequests()
            .antMatchers(HttpMethod.POST, "/api/v1/users").permitAll()
            .antMatchers(HttpMethod.GET, "/api/v1/users/seller").permitAll()
            .antMatchers(
                "/api/v1/auth/login",
                "/api/v1/auth/token/refresh",
                "/h2-console/**"
            ).permitAll()
            .anyRequest().authenticated()
    }
}
