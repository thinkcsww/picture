package com.applory.pictureserver.domain.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "picture")
@Data
public class AppConfiguration {
    private String pwSalt;

    private String jwtSignKey;

    private String clientId;

    private String clientSecret;
}
