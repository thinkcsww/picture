package com.applory.pictureserver.domain.config;

import com.applory.pictureserver.domain.user.CustomUserDetail;
import com.applory.pictureserver.domain.user.CustomUserDetailService;
import com.applory.pictureserver.domain.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.client.RestTemplate;

@EnableWebSecurity
@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    CustomUserDetailService userService;


//        @Override
//    public void configure(HttpSecurity http) throws Exception {
//        http.headers().frameOptions().disable();
//        http.cors().disable();
////        http.addFilterBefore(new JwtAuthenticationFilter(jwtTokenProvider), UsernamePasswordAuthenticationFilter.class);
//        http.authorizeRequests()
//                .antMatchers(HttpMethod.POST, "/api/v1/users").permitAll()
//                .antMatchers(HttpMethod.GET, "/api/v1/users/seller").permitAll()
//                .antMatchers(
//                        "/api/v1/auth/login",
//                        "/api/v1/auth/token/refresh",
//                        "/h2-console/**").permitAll()
//                .anyRequest().authenticated();
//        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
//    }

    /**
     * Oauth2 grant_type password ??????????????? ????????? ??????
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
