package com.applory.pictureserver.domain.oauth;

import com.applory.pictureserver.config.AppConfiguration;
import com.applory.pictureserver.config.JwtTokenProvider;
import com.applory.pictureserver.exception.NotFoundException;
import com.applory.pictureserver.exception.UnauthorizedException;
import com.applory.pictureserver.domain.user.User;
import com.applory.pictureserver.domain.user.UserRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.springframework.core.env.Environment;
import org.springframework.core.env.Profiles;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;

@Service
@Slf4j
@RequiredArgsConstructor
public class AuthService {
    public static final String KAKAO_VALIDATE_TOKEN_URL = "https://kapi.kakao.com/v1/user/access_token_info";

    private final UserRepository userRepository;

    private final RestTemplate restTemplate;

    private final Environment environment;

    private final AppConfiguration appConfiguration;

    private final ObjectMapper objectMapper;

    private final JwtTokenProvider jwtTokenProvider;

    public MyOAuth2Token login(AuthDto.Login dto, String baseUrl) {

        User userInDB = userRepository.findByUsername(dto.getUsername());

        if (userInDB == null) {
            throw new NotFoundException(dto.getUsername() + " not found");
        }

        checkKakaoToken(dto.getToken());

        MyOAuth2Token oAuth2Token = getToken(dto, baseUrl);

        return oAuth2Token;

    }

    private void checkKakaoToken(String token) {
        if ((environment.acceptsProfiles(Profiles.of("test")) && "test".equals(token)) || "test".equals(token)) {
            return;
        }

        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/x-www-form-urlencoded;charset=utf-8");
        headers.set("Authorization", "Bearer " + token);

        HttpEntity httpEntity = new HttpEntity(headers);

        try {
            restTemplate.exchange(KAKAO_VALIDATE_TOKEN_URL, HttpMethod.GET, httpEntity, HashMap.class);
        } catch (HttpClientErrorException e) {
            log.error("checkKakaoToken: {}", e.getMessage());
            throw new UnauthorizedException(e.getMessage());
        }
    }

    private MyOAuth2Token getToken(AuthDto.Login dto, String baseUrl) {
        String credentials = appConfiguration.getClientId() + ":" + appConfiguration.getPwSalt();
        String encodedCredentials = new String(Base64.encodeBase64(credentials.getBytes()));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.add("Authorization", "Basic " + encodedCredentials);

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "password");
        params.add("username", dto.getUsername());
        params.add("password", dto.getUsername() + appConfiguration.getPwSalt());
        params.add("scope", "read write");

        HttpEntity<MultiValueMap<String,String>> httpEntity = new HttpEntity<>(params, headers);

        ResponseEntity<String> response = restTemplate.postForEntity(baseUrl + "/oauth/token", httpEntity, String.class);
        if(response.getStatusCode() == HttpStatus.OK) {
            try {
                return objectMapper.readValue(response.getBody(), MyOAuth2Token.class);
            } catch (JsonProcessingException e) {
                throw new UnauthorizedException("Invalid Oauth Token");
            }
        }

        throw new UnauthorizedException("Invalid Oauth Token");
    }

    public MyOAuth2Token refreshToken(AuthDto.RefreshToken dto, String baseUrl) {

        if (jwtTokenProvider.validateToken(dto.getRefreshToken())) {

            // 리프레시 토큰이 7일 이상 남았으면 그냥 리프레시
            if (jwtTokenProvider.refreshTokenIsNotExpireIn7Days(dto.getRefreshToken())) {
                String credentials = appConfiguration.getClientId() + ":" + appConfiguration.getClientSecret();
                String encodedCredentials = new String(Base64.encodeBase64(credentials.getBytes()));

                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
                headers.add("Accept", MediaType.APPLICATION_JSON_VALUE);
                headers.add("Authorization", "Basic " + encodedCredentials);

                MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
                params.add("refresh_token", dto.getRefreshToken());
                params.add("grant_type", "refresh_token");

                HttpEntity<MultiValueMap<String, String>> httpEntity = new HttpEntity<>(params, headers);


                try {
                    ResponseEntity<String> response = restTemplate.postForEntity(baseUrl + "/oauth/token", httpEntity, String.class);
                    if (response.getStatusCode() == HttpStatus.OK) {
                        return objectMapper.readValue(response.getBody(), MyOAuth2Token.class);
                    }
                } catch (Exception e) {
                    log.error("refreshToken error: " + e.getMessage());
                    throw new UnauthorizedException("Invalid Oauth Token");
                }
            // 리프레시 토큰이 7일 이하로 남았으면 아예 새로 토큰을 발급
            } else {
                String userName = jwtTokenProvider.getUserName(dto.getRefreshToken());

                AuthDto.Login loginDto = new AuthDto.Login();
                loginDto.setToken("abc");
                loginDto.setUsername(userName);

                return getToken(loginDto, baseUrl);
            }
        }

        throw new UnauthorizedException("Invalid Oauth Token");
    }
}
