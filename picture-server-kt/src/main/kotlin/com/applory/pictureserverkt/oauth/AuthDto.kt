package com.applory.pictureserverkt.oauth

import javax.validation.constraints.NotBlank

class AuthDto {

    data class Login(
        @field:NotBlank
        val username: String,
        @field:NotBlank
        var kakaoToken: String
    )
}
