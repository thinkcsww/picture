package com.applory.pictureserverkt

import com.applory.pictureserverkt.error.ApiError
import com.applory.pictureserverkt.oauth.AuthDto
import com.applory.pictureserverkt.oauth.Oauth2Token
import com.applory.pictureserverkt.user.UserDto
import com.applory.pictureserverkt.user.UserRepository
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.http.*
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit.jupiter.SpringExtension


@ExtendWith(SpringExtension::class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class AuthControllerTest(@Autowired private val testRestTemplate: TestRestTemplate, @Autowired private val userRepository: UserRepository) {
    private val API_1_0_AUTH = "/api/v1/auth"
    private val API_1_0_AUTH_LOGIN = "$API_1_0_AUTH/login"
    private val API_1_0_AUTH_REFRESH_TOKEN = "$API_1_0_AUTH/token/refresh"

    private val API_V_1_USERS = "/api/v1/users"

    @BeforeEach
    fun cleanUp() {
        userRepository.deleteAll()
    }

    @Test
    fun postLogin_withoutLoginDto_receiveBadRequest() {
        val response: ResponseEntity<Any> = login(null, Any::class.java)
        assertThat(response.statusCode).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    fun postLogin_withoutLoginDto_receiveApiError() {
        val response: ResponseEntity<ApiError> = login(null, ApiError::class.java)
        assertThat(response.body?.url).isEqualTo(API_1_0_AUTH_LOGIN);
    }

    @Test
    fun postLogin_loginDtoWithoutUsername_receiveApiErrorWithValidationErrors() {
        val dto = AuthDto.Login("", "abc")

        val response: ResponseEntity<String> = login(dto, String::class.java)

        assertThat(response.body?.contains("validationErrors")).isTrue()
    }

    @Test
    fun postLogin_loginDtoWithoutKakaoToken_receiveApiErrorWithValidationErrors() {
        val dto = AuthDto.Login("abc", "")

        val response: ResponseEntity<String> = login(dto, String::class.java)

        assertThat(response.body?.contains("validationErrors")).isTrue()
    }

    @Test
    fun postLogin_validLoginDtoButUserNotExist_receiveNotFound404() {
        val dto = TestUtil.createValidLoginDto("123")

        val response: ResponseEntity<Any> = login(dto, Any::class.java)
        assertThat(response.statusCode).isEqualTo(HttpStatus.NOT_FOUND)
    }

    @Test
    fun postLogin_loginDtoWithInvalidKakaoToken_receiveUnauthorized() {

        signUp(TestUtil.createValidUser("123"), Any::class.java)

        val dto = TestUtil.createValidLoginDto("123")
        dto.kakaoToken = "asdasd"

        val response: ResponseEntity<Any> = login(dto, Any::class.java)
        assertThat(response.statusCode).isEqualTo(HttpStatus.UNAUTHORIZED)
    }

    @Test
    fun postLogin_validLoginDto_receiveOk() {
        val username = "123123"

        signUp(TestUtil.createValidUser(username), Any::class.java)

        val dto = TestUtil.createValidLoginDto(username)

        val response: ResponseEntity<Any> = login(dto, Any::class.java)
        assertThat(response.statusCode).isEqualTo(HttpStatus.OK)
    }

    @Test
    fun postLogin_validLoginDto_receiveOauth2Token() {
        val username = "123123"

        signUp(TestUtil.createValidUser(username), Any::class.java)

        val dto = TestUtil.createValidLoginDto(username)

        val response: ResponseEntity<Oauth2Token> = login(dto, Oauth2Token::class.java)
        assertThat(response.body?.expires_in).isEqualTo(86399)
    }

    @Test
    fun getRefreshToken_validLoginDto_receiveOauth2Token() {
        val username = "123123"

        signUp(TestUtil.createValidUser(username), Any::class.java)

        val dto = TestUtil.createValidLoginDto(username)

        val response: ResponseEntity<Oauth2Token> = login(dto, Oauth2Token::class.java)
        assertThat(response.body?.expires_in).isEqualTo(86399)
    }

    @Test
    fun postRefreshToken_withValidRefreshToken_receiveOk() {
        val username = "123123"
        signUp(TestUtil.createValidUser(username), Any::class.java)

        val loginDto = TestUtil.createValidLoginDto(username)
        val tokenResponse = login(loginDto, Oauth2Token::class.java)

        val refreshTokenDto = AuthDto.RefreshToken(tokenResponse.body!!.refresh_token)
        val refreshTokenResponse = getRefreshToken(refreshTokenDto, Any::class.java)

        assertThat(refreshTokenResponse?.statusCode).isEqualTo(HttpStatus.OK)
    }

    @Test
    fun postRefreshToken_withValidRefreshToken_receiveRefreshedOauth2Token() {
        val username = "123123"
        signUp(TestUtil.createValidUser(username), Any::class.java)

        val loginDto = TestUtil.createValidLoginDto(username)
        val tokenResponse = login(loginDto, Oauth2Token::class.java)

        val refreshTokenDto = AuthDto.RefreshToken(tokenResponse.body!!.refresh_token)
        val refreshTokenResponse = getRefreshToken(refreshTokenDto, Oauth2Token::class.java)

        assertThat(refreshTokenResponse?.body?.expires_in).isEqualTo(86399)
    }

    @Test
    fun postRefreshToken_withInValidRefreshToken_receiveUnauthorized() {
        val username = "123123"
        signUp(TestUtil.createValidUser(username), Any::class.java)

        val loginDto = TestUtil.createValidLoginDto(username)
        val tokenResponse = login(loginDto, Oauth2Token::class.java)

        val refreshTokenDto = AuthDto.RefreshToken("abc")
        val refreshTokenResponse = getRefreshToken(refreshTokenDto, Any::class.java)

        assertThat(refreshTokenResponse?.statusCode).isEqualTo(HttpStatus.UNAUTHORIZED)
    }


    private fun <T> login(dto: Any?, responseType: Class<T>): ResponseEntity<T> {
        val headers = HttpHeaders()
        headers.set("Content-Type", MediaType.APPLICATION_JSON_VALUE);

        val httpEntity = HttpEntity(dto, headers)

        return testRestTemplate.postForEntity(API_1_0_AUTH_LOGIN, httpEntity, responseType)
    }

    fun <T> signUp(dto: UserDto.Create, responseType: Class<T>): ResponseEntity<T> {
        return testRestTemplate.postForEntity(API_V_1_USERS, dto, responseType)
    }

    fun <T> getRefreshToken(dto: AuthDto.RefreshToken, responseType: Class<T>): ResponseEntity<T>? {
        val headers = HttpHeaders()
        headers["Content-Type"] = MediaType.APPLICATION_JSON_VALUE
        val httpEntity = HttpEntity(dto, headers)

        return testRestTemplate.postForEntity(API_1_0_AUTH_REFRESH_TOKEN, httpEntity, responseType)
    }
}
