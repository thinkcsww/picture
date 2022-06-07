package com.applory.pictureserverkt.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding

@ConfigurationProperties(prefix = "picture")
@ConstructorBinding
data class AppConfiguration(
    val pwSalt: String?,
    val jwtSignKey: String,
    val clientId: String,
    val clientSecret: String,
)
