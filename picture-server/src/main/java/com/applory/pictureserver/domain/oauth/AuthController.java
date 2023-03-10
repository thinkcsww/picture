package com.applory.pictureserver.domain.oauth;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public MyOAuth2Token login(@Valid @RequestBody AuthDto.Login dto, HttpServletRequest request) {
//        return authService.login(dto, request.getScheme() + "://" + request.getLocalName() + ":" + request.getLocalPort());
        return authService.login(dto, "http://ec2-3-34-1-40.ap-northeast-2.compute.amazonaws.com:8080");
    }

    @PostMapping(value = "/token/refresh")
    public MyOAuth2Token refreshToken(@Valid @RequestBody AuthDto.RefreshToken dto, HttpServletRequest request) {
        return authService.refreshToken(dto, request.getScheme() + "://" + request.getLocalName() + ":" + request.getLocalPort());
    }
}
