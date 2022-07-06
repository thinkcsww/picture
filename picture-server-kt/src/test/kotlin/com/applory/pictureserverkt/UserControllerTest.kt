package com.applory.pictureserverkt

import com.applory.pictureserverkt.TestConstant.API_1_0_AUTH_LOGIN
import com.applory.pictureserverkt.TestConstant.API_V_1_USERS
import com.applory.pictureserverkt.TestConstant.API_V_1_USERS_ME
import com.applory.pictureserverkt.TestConstant.TEST_USERNAME
import com.applory.pictureserverkt.config.AppConfiguration
import com.applory.pictureserverkt.oauth.AuthDto
import com.applory.pictureserverkt.oauth.MyOAuth2Token
import com.applory.pictureserverkt.user.UserDto
import com.applory.pictureserverkt.user.UserRepository
import com.applory.pictureserverkt.user.UserService
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.core.ParameterizedTypeReference
import org.springframework.http.*
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit.jupiter.SpringExtension

@ExtendWith(SpringExtension::class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class UserControllerTest(
    @Autowired private val testRestTemplate: TestRestTemplate,
    @Autowired private val userService: UserService,
    @Autowired private val appConfiguration: AppConfiguration,
    @Autowired private val userRepository: UserRepository
) {

    @BeforeEach
    fun cleanup() {
        userRepository.deleteAll()
        testRestTemplate.restTemplate.interceptors.clear()
    }

    @Test
    fun postClientUser_withValidUserDto_receiveCreated201() {
        val response = signUp(TestUtil.createValidClientUser(TEST_USERNAME), Any::class.java)

        assertThat(response.statusCode).isEqualTo(HttpStatus.CREATED)
    }

    @Test
    fun postClientUser_withValidUserDto_userSavedToDatabase() {
        val dto = TestUtil.createValidClientUser(TEST_USERNAME)

        signUp(dto, Any::class.java)

        assertThat(userRepository.count()).isEqualTo(1)
    }

    @Test
    fun postClientUser_withValidUserDto_receiveUserVMWithoutPassword() {
        val dto = TestUtil.createValidClientUser(TEST_USERNAME)

        val response = signUp(dto, String::class.java)

        assertThat(response.body?.contains("password")).isFalse()
    }

    @Test
    fun postClientUser_withValidUserDto_receiveUserVMWithProperValues() {
        val dto = TestUtil.createValidClientUser(TEST_USERNAME)

        val response = signUp(dto, UserDto.VM::class.java)

        assertThat(response.body?.id).isNotNull
        assertThat(response.body?.nickname).isNotNull
        assertThat(response.body?.sellerEnabledYn).isNotNull
        assertThat(response.body?.snsType).isNotNull
        assertThat(response.body?.createdDt).isNotNull
        assertThat(response.body?.updatedDt).isNotNull
    }

    @Test
    fun postSellerUser_withValidUserDto_receiveUserVMWithProperValues() {
        val dto: UserDto.Create = TestUtil.createValidSellerUser(TEST_USERNAME)
        val response = signUp(dto, UserDto.VM::class.java)

        assertThat(response.body?.id).isNotNull
        assertThat(response.body?.nickname).isNotNull
        assertThat(response.body?.sellerEnabledYn).isNotNull
        assertThat(response.body?.snsType).isNotNull
        assertThat(response.body?.createdDt).isNotNull
        assertThat(response.body?.updatedDt).isNotNull
        assertThat(response.body?.workHourFromDt).isNotNull
        assertThat(response.body?.workHourToDt).isNotNull
        assertThat(response.body?.specialty).isNotNull
    }

    @Test
    fun getUserMe_withInvalidToken_receiveUnauthorized() {
        authenticate("adasd")
        val response: ResponseEntity<Any> = getUserMe<Any>(object : ParameterizedTypeReference<Any>() {})
        assertThat(response.statusCode).isEqualTo(HttpStatus.UNAUTHORIZED)
    }

    @Test
    fun getUserMe_withValidToken_receiveUserVM() {
        signUp(TestUtil.createValidClientUser(TEST_USERNAME), Any::class.java)

        val tokenResponse: ResponseEntity<MyOAuth2Token> = login(TestUtil.createValidLoginDto(TEST_USERNAME), MyOAuth2Token::class.java)
        authenticate(tokenResponse.body!!.access_token)

        val response: ResponseEntity<UserDto.VM> = getUserMe(object : ParameterizedTypeReference<UserDto.VM>() {})
        assertThat(response.body!!.username).isEqualTo(TEST_USERNAME)
    }

    fun <T> signUp(dto: UserDto.Create, responseType: Class<T>): ResponseEntity<T> {
        return testRestTemplate.postForEntity(API_V_1_USERS, dto, responseType)
    }

    fun <T> login(dto: AuthDto.Login, responseType: Class<T>): ResponseEntity<T> {
        val headers = HttpHeaders()
        headers["Content-Type"] = MediaType.APPLICATION_JSON_VALUE
        val httpEntity: HttpEntity<AuthDto.Login> = HttpEntity<AuthDto.Login>(dto, headers)
        return testRestTemplate.postForEntity<T>(API_1_0_AUTH_LOGIN, httpEntity, responseType)
    }

    fun <T> getUserMe(responseType: ParameterizedTypeReference<T>): ResponseEntity<T> {
        return testRestTemplate.exchange<T>(API_V_1_USERS_ME, HttpMethod.GET, null, responseType)
    }

    private fun authenticate(token: String) {
        testRestTemplate.restTemplate.interceptors.add(RestTemplateInterceptor(token))
    }


}
