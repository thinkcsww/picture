package com.applory.pictureserverkt.oauth

data class Oauth2Token(
    val access_token: String,
    val token_type: String,
    val refresh_token: String,
    val expires_in: Long,
    val scope: String,
    val jti: String
)
