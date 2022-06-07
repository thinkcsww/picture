package com.applory.pictureserver.domain.oauth;

import com.applory.pictureserver.domain.config.AppConfiguration;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    private final AppConfiguration appConfiguration;

    private final RestTemplate restTemplate;

    private final ObjectMapper objectMapper;

    @PostMapping("/login")
    public OAuth2Token login(@Valid @RequestBody AuthDto.Login dto, HttpServletRequest request) {
        return authService.login(dto, request.getScheme() + "://" + request.getLocalName() + ":" + request.getLocalPort());
    }

    @PostMapping(value = "/token/refresh")
    public OAuth2Token refreshToken(@Valid @RequestBody AuthDto.RefreshToken dto, HttpServletRequest request) throws JsonProcessingException {
        return authService.refreshToken(dto, request.getScheme() + "://" + request.getLocalName() + ":" + request.getLocalPort());
    }
}
