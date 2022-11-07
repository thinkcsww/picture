package com.applory.pictureserver.config;

import com.applory.pictureserver.domain.user.CustomUserDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.client.RestTemplate;

@EnableWebSecurity
@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    CustomUserDetailService userService;


    @Override
    public void configure(final WebSecurity web) {
        web.ignoring()
                .antMatchers(HttpMethod.GET, "/api/v1/requests")
                .antMatchers(HttpMethod.GET, "/api/v1/users/seller")
                .antMatchers(HttpMethod.GET, "/api/v1/users/seller/{id}")
                .antMatchers(HttpMethod.POST, "/api/v1/auth/token/refresh")
                .antMatchers(HttpMethod.GET, "/swagger-ui/**")
                .antMatchers(HttpMethod.GET, "/v3/api-docs")
                .antMatchers(HttpMethod.POST, "/api/v1/users");
    }

    /**
     * Oauth2 grant_type password 사용하려면 있어야 한다
     *
     * @return
     * @throws Exception
     */
    @Override
    @Bean
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userService).passwordEncoder(passwordEncoder());
    }


    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }


    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
