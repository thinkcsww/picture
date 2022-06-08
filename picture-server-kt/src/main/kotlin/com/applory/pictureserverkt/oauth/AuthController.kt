package com.applory.pictureserverkt.oauth

import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import javax.servlet.http.HttpServletRequest
import javax.validation.Valid

@RestController
@RequestMapping("/api/v1/auth")
class AuthController(private val authService: AuthService) {

    @PostMapping("/login")
    fun login(@Valid @RequestBody dto: AuthDto.Login, request: HttpServletRequest): Oauth2Token {
        return authService.login(dto, "${request.scheme}://${request.localName}:${request.localPort}");
    }

    @PostMapping("/token/refresh")
    fun refreshToken(@Valid @RequestBody dto: AuthDto.RefreshToken, request: HttpServletRequest): Oauth2Token {
        return authService.refreshToken(dto, "${request.scheme}://${request.localName}:${request.localPort}");
    }

}
