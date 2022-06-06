package com.applory.pictureserver;

import com.applory.pictureserver.domain.error.ApiError;
import com.applory.pictureserver.domain.oauth.LoginDto;
import com.applory.pictureserver.domain.oauth.OAuth2Token;
import com.applory.pictureserver.domain.user.UserDto;
import com.applory.pictureserver.domain.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class AuthControllerTest {

    public static final String API_1_0_AUTH = "/api/v1/auth";
    public static final String API_1_0_AUTH_LOGIN = API_1_0_AUTH + "/login";
    public static final String API_1_0_AUTH_REFRESH_TOKEN = API_1_0_AUTH + "/token/refresh";

    @Autowired
    TestRestTemplate testRestTemplate;

    @Autowired
    UserRepository userRepository;

    @BeforeEach
    public void cleanUp() {
        userRepository.deleteAll();
    }

    @Test
    public void postLogin_withoutLoginDto_receiveBadRequest() {
        ResponseEntity<Object> response = login(null, Object.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    public void postLogin_withoutLoginDto_receiveApiError() {
        ResponseEntity<ApiError> response = login(null, ApiError.class);
        assertThat(response.getBody().getUrl()).isEqualTo(API_1_0_AUTH_LOGIN);
    }

    @Test
    public void postLogin_loginDtoWithoutUsername_receiveApiErrorWithValidationErrors() {
        LoginDto.Login loginDto = new LoginDto.Login();
        loginDto.setKakaoToken("abc");

        ResponseEntity<String> response = login(loginDto, String.class);
        assertThat(response.getBody().contains("validationErrors")).isTrue();
    }

    @Test
    public void postLogin_loginDtoWithoutKakaoToken_receiveApiErrorWithValidationErrors() {
        LoginDto.Login loginDto = new LoginDto.Login();
        loginDto.setUsername("abc");

        ResponseEntity<String> response = login(loginDto, String.class);
        assertThat(response.getBody().contains("validationErrors")).isTrue();
    }

    @Test
    public void postLogin_validLoginDtoButUserNotExist_receiveNotFound404() {
        LoginDto.Login loginDto = new LoginDto.Login();
        loginDto.setUsername("123");
        loginDto.setKakaoToken("abc");

        ResponseEntity<String> response = login(loginDto, String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    public void postLogin_validLoginDtoWithInvalidKakaoToken_receiveUnauthorized() {
        String username = "123123";
        signUp(TestUtil.createValidUser(username), Object.class);

        LoginDto.Login loginDto = new LoginDto.Login();
        loginDto.setUsername(username);
        loginDto.setKakaoToken("abc");

        ResponseEntity<String> response = login(loginDto, String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    public void postLogin_validLoginDto_receiveOk() {
        String username = "123123";

        signUp(TestUtil.createValidUser(username), Object.class);

        LoginDto.Login loginDto = TestUtil.createValidLoginDto(username);

        ResponseEntity<OAuth2Token> response = login(loginDto, OAuth2Token.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    public void postLogin_validLoginDto_receiveOauth2Token() {
        String username = "123123";

        signUp(TestUtil.createValidUser(username), Object.class);

        LoginDto.Login loginDto = TestUtil.createValidLoginDto(username);

        ResponseEntity<OAuth2Token> response = login(loginDto, OAuth2Token.class);
        assertThat(response.getBody().getExpires_in()).isEqualTo(86399);
    }

    @Test
//    public void postLogin_withValidUsername_receiveOk() {
//        ResponseEntity<String> response = login("123123", String.class);
//        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
//    }

    public <T> ResponseEntity<T> login(LoginDto.Login loginDto, Class<T> responseType) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", MediaType.APPLICATION_JSON_VALUE);
        HttpEntity<LoginDto.Login> httpEntity = new HttpEntity<>(loginDto, headers);

        return testRestTemplate.postForEntity(API_1_0_AUTH_LOGIN, httpEntity, responseType);
    }

    public <T>ResponseEntity<T> signUp(UserDto.Create dto, Class<T> responseType) {
        return testRestTemplate.postForEntity("/api/v1/users", dto, responseType);
    }
}
