package com.applory.pictureserverkt

import com.applory.pictureserverkt.config.AppConfiguration
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
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit.jupiter.SpringExtension
import kotlin.math.sign

@ExtendWith(SpringExtension::class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class UserControllerTest(
    @Autowired private val testRestTemplate: TestRestTemplate,
    @Autowired private val userService: UserService,
    @Autowired private val appConfiguration: AppConfiguration,
    @Autowired private val userRepository: UserRepository
) {

    private val API_V_1_USERS = "/api/v1/users"

    @BeforeEach
    fun cleanup() {
        userRepository.deleteAll()
    }

    @Test
    fun postUser_withValidUserDto_receiveCreated201() {
        val response = signUp(TestUtil.createValidUser("123123"), Any::class.java)

        assertThat(response.statusCode).isEqualTo(HttpStatus.CREATED)
    }

    @Test
    fun postUser_withValidUserDto_userSavedToDatabase() {
        val dto = TestUtil.createValidUser("123123")

        signUp(dto, Any::class.java)

        assertThat(userRepository.count()).isEqualTo(1)
    }

    @Test
    fun postUser_withValidUserDto_receiveUserVMWithoutPassword() {
        val dto = TestUtil.createValidUser("123123")

        val response = signUp(dto, String::class.java)

        assertThat(response.body?.contains("password")).isFalse()
    }

    fun <T> signUp(dto: UserDto.Create, responseType: Class<T>): ResponseEntity<T> {
        return testRestTemplate.postForEntity(API_V_1_USERS, dto, responseType)
    }


}
