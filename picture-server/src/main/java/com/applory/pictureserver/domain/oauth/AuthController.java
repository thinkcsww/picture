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

    private final LoginService loginService;

    @PostMapping("")
    public OAuth2Token login(@Valid @RequestBody LoginDto.Login dto, HttpServletRequest request) {
        return loginService.login(dto, "http://localhost:" + request.getLocalPort());
    }
}
