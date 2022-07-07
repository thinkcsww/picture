package com.applory.pictureserverkt

import com.applory.pictureserverkt.TestConstant.API_1_0_AUTH_LOGIN
import com.applory.pictureserverkt.TestConstant.API_V_1_REQUESTS
import com.applory.pictureserverkt.TestConstant.API_V_1_USERS
import com.applory.pictureserverkt.TestConstant.TEST_USERNAME
import com.applory.pictureserverkt.TestUtil.Companion.createValidRequestDto
import com.applory.pictureserverkt.error.ApiError
import com.applory.pictureserverkt.oauth.AuthDto
import com.applory.pictureserverkt.oauth.MyOAuth2Token
import com.applory.pictureserverkt.request.Request
import com.applory.pictureserverkt.request.RequestDto
import com.applory.pictureserverkt.request.RequestRepository
import com.applory.pictureserverkt.user.UserDto
import com.applory.pictureserverkt.user.UserRepository
import com.fasterxml.jackson.databind.ObjectMapper
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
import org.springframework.util.LinkedMultiValueMap
import org.springframework.util.MultiValueMap
import org.springframework.web.util.UriComponentsBuilder
import java.time.LocalDateTime
import java.util.*

@ExtendWith(SpringExtension::class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class RequestControllerTest(
    @Autowired private val testRestTemplate: TestRestTemplate,
    @Autowired private val userRepository: UserRepository,
    @Autowired private val objectMapper: ObjectMapper,
    @Autowired private val requestRepository: RequestRepository) {

    @BeforeEach
    fun cleanUp() {
        requestRepository.deleteAll()
        userRepository.deleteAll()
        testRestTemplate.restTemplate.interceptors.clear()
    }

    @Test
    fun postRequest_withInvalidToken_receiveUnauthorized() {
        authenticate("invalid_token")

        val dto: RequestDto.Create = createValidRequestDto()

        val response: ResponseEntity<Any> = postRequest(dto, Any::class.java)

        assertThat(response.statusCode).isEqualTo(HttpStatus.UNAUTHORIZED)
    }

    @Test
    fun postRequest_withValidToken_receiveCreated() {
        signUp(TestUtil.createValidClientUser(TEST_USERNAME), Any::class.java)

        val tokenResponse: ResponseEntity<MyOAuth2Token> = login(TestUtil.createValidLoginDto(TEST_USERNAME), MyOAuth2Token::class.java)
        authenticate(tokenResponse.body!!.access_token)

        val dto = createValidRequestDto()
        val response = postRequest(dto, Any::class.java)

        assertThat(response.statusCode).isEqualTo(HttpStatus.CREATED)
    }

    @Test
    fun postRequest_withInvalidDto_receiveBadRequest() {
        signUp(TestUtil.createValidClientUser(TEST_USERNAME), Any::class.java)

        val tokenResponse = login(TestUtil.createValidLoginDto(TEST_USERNAME), MyOAuth2Token::class.java)
        authenticate(tokenResponse.body!!.access_token)

        val dto = createValidRequestDto()
        dto.title = null
        val response = postRequest(dto, Any::class.java)
        assertThat(response.statusCode).isEqualTo(HttpStatus.BAD_REQUEST)
    }

    @Test
    fun postRequest_withInvalidDto_receiveApiErrorWithValidationErrors() {
        signUp(TestUtil.createValidClientUser(TEST_USERNAME), Any::class.java)

        val tokenResponse = login(TestUtil.createValidLoginDto(TEST_USERNAME), MyOAuth2Token::class.java)
        authenticate(tokenResponse.body!!.access_token)

        val dto = createValidRequestDto()
        dto.title = null
        val response: ResponseEntity<ApiError> = postRequest(dto, ApiError::class.java)
        assertThat(response.body!!.validationErrors.containsKey("title")).isTrue()
    }

    @Test
    fun postRequest_withValidDto_receiveRequestVM() {
        val userResponse: ResponseEntity<UserDto.VM> = signUp(TestUtil.createValidClientUser(TEST_USERNAME), UserDto.VM::class.java)
        val tokenResponse = login(
            TestUtil.createValidLoginDto(TEST_USERNAME),
            MyOAuth2Token::class.java
        )
        authenticate(tokenResponse.body!!.access_token)
        val dto = createValidRequestDto()
        val response = postRequest(dto, RequestDto.VM::class.java)
        assertThat(response.body!!.userId).isEqualTo(userResponse.body!!.id)
        assertThat(response.body!!.userNickname).isEqualTo(userResponse.body!!.nickname)
        assertThat(response.body!!.readCount).isEqualTo(0)
        assertThat(response.body!!.title).isEqualTo("제목입니다")
    }

    @Test
    fun getRequests_withInvalidToken_receiveUnauthorized() {
        val response: ResponseEntity<Any> = getRequests<Any>(null, object : ParameterizedTypeReference<Any>() {})
        assertThat(response.statusCode).isEqualTo(HttpStatus.UNAUTHORIZED)
    }

    @Test
    fun getRequests_withValidToken_receiveOk() {
        signUp(TestUtil.createValidClientUser(TEST_USERNAME), Any::class.java)

        val tokenResponse = login(TestUtil.createValidLoginDto(TEST_USERNAME), MyOAuth2Token::class.java)
        authenticate(tokenResponse.body!!.access_token)

        postRequest(createValidRequestDto(), RequestDto.VM::class.java)

        val response = getRequests(null, object : ParameterizedTypeReference<Any>() {})

        assertThat(response.statusCode).isEqualTo(HttpStatus.OK)
    }

    @Test
    fun getRequests_withValidToken_receivePage() {
        signUp(TestUtil.createValidClientUser(TEST_USERNAME), Any::class.java)

        val tokenResponse = login(TestUtil.createValidLoginDto(TEST_USERNAME), MyOAuth2Token::class.java)
        authenticate(tokenResponse.body!!.access_token)

        postRequest(createValidRequestDto(), RequestDto.VM::class.java)

        val response: ResponseEntity<TestPage<RequestDto.VM>> = getRequests(null, object : ParameterizedTypeReference<TestPage<RequestDto.VM>>() {})

        assertThat(response.body!!.content[0].title).isEqualTo("제목입니다")
    }

    @Test
    fun getRequests_withValidToken_receivePageOrderByDueDateAsc() {
        signUp(TestUtil.createValidClientUser(TEST_USERNAME), Any::class.java)

        val tokenResponse = login(TestUtil.createValidLoginDto(TEST_USERNAME), MyOAuth2Token::class.java)

        authenticate(tokenResponse.body!!.access_token)

        val dto1 = createValidRequestDto()
        dto1.desiredPrice = 10000

        postRequest(dto1, RequestDto.VM::class.java)
        val dto2 = createValidRequestDto()

        postRequest(dto2, RequestDto.VM::class.java)
        val response: ResponseEntity<TestPage<RequestDto.VM>> = getRequests(null, object : ParameterizedTypeReference<TestPage<RequestDto.VM>>() {}, "&sort=desiredPrice,desc")

        assertThat(response.body!!.content[0].desiredPrice).isEqualTo(10000)
    }

    @Test
    fun getRequests_withValidToken_receivePageOrderByPriceDesc() {
        signUp(TestUtil.createValidClientUser(TEST_USERNAME), Any::class.java)

        val tokenResponse = login(TestUtil.createValidLoginDto(TEST_USERNAME), MyOAuth2Token::class.java)

        authenticate(tokenResponse.body!!.access_token)

        val dto1 = createValidRequestDto()
        dto1.dueDate = LocalDateTime.of(2025, 12, 31, 12, 12)

        postRequest(dto1, RequestDto.VM::class.java)
        val dto2 = createValidRequestDto()

        postRequest(dto2, RequestDto.VM::class.java)

        val response: ResponseEntity<TestPage<RequestDto.VM>> = getRequests(null, object : ParameterizedTypeReference<TestPage<RequestDto.VM>>() {}, "&sort=dueDate,asc")
        assertThat(response.body!!.content[0].dueDate).isBefore(response.body!!.content[1].dueDate)
    }


    @Test
    fun getRequests_withValidToken_receiveOnlyRequestsDueDateIsNotOver() {
        signUp(TestUtil.createValidClientUser(TEST_USERNAME), Any::class.java)
        val tokenResponse = login(TestUtil.createValidLoginDto(TEST_USERNAME), MyOAuth2Token::class.java)

        authenticate(tokenResponse.body!!.access_token)

        val dto1 = createValidRequestDto()
        dto1.dueDate = LocalDateTime.of(2020, 12, 31, 12, 12)
        postRequest(dto1, RequestDto.VM::class.java)

        val dto2 = createValidRequestDto()
        postRequest(dto2, RequestDto.VM::class.java)

        val response: ResponseEntity<TestPage<RequestDto.VM>> = getRequests(null, object : ParameterizedTypeReference<TestPage<RequestDto.VM>>() {})
        assertThat(response.body!!.totalElements).isEqualTo(1)
    }

    @Test
    fun getRequests_searchByRequestType_receiveResultByRequestType() {
        signUp(TestUtil.createValidClientUser(TEST_USERNAME), Any::class.java)
        val tokenResponse = login(TestUtil.createValidLoginDto(TEST_USERNAME), MyOAuth2Token::class.java)

        authenticate(tokenResponse.body!!.access_token)

        postRequest(createValidRequestDto(), RequestDto.VM::class.java)
        val search = RequestDto.Search(
            requestType = Request.RequestType.BACKGROUND
        )
        val response: ResponseEntity<TestPage<RequestDto.VM>> = getRequests(null, object : ParameterizedTypeReference<TestPage<RequestDto.VM>>() {})
        assertThat(response.body!!.content[0].requestType).isEqualTo(Request.RequestType.BACKGROUND)
    }

    @Test
    fun getRequests_searchByDueDate_receiveResultByDueDate() {
        signUp(TestUtil.createValidClientUser(TEST_USERNAME), Any::class.java)
        val tokenResponse = login(TestUtil.createValidLoginDto(TEST_USERNAME), MyOAuth2Token::class.java)

        authenticate(tokenResponse.body!!.access_token)

        val dto1 = createValidRequestDto()
        dto1.dueDate = LocalDateTime.of(2022, 12, 31, 12, 12)
        postRequest(dto1, RequestDto.VM::class.java)

        val dto2 = createValidRequestDto()
        postRequest(dto2, RequestDto.VM::class.java)

        val search = RequestDto.Search(
            fromForDueDt = LocalDateTime.of(2022, 12, 31, 12, 10),
            toForDueDt = LocalDateTime.of(2022, 12, 31, 12, 15)
        )

        val response: ResponseEntity<TestPage<RequestDto.VM>> =
            getRequests(search, object : ParameterizedTypeReference<TestPage<RequestDto.VM>>() {})
        assertThat(response.body!!.content[0].dueDate).isBetween(LocalDateTime.of(2022, 12, 31, 12, 10), LocalDateTime.of(2022, 12, 31, 12, 15))
    }

    @Test
    fun getRequest_withInValidToken_receive401() {
        authenticate("invalid_token")
        val response: ResponseEntity<Any> = getRequest<Any>(UUID.randomUUID(), object : ParameterizedTypeReference<Any>() {})
        assertThat(response.statusCode).isEqualTo(HttpStatus.UNAUTHORIZED)
    }

    @Test
    fun getRequest_withValidTokenButNotExistId_receive404() {
        signUp(TestUtil.createValidClientUser(TEST_USERNAME), Any::class.java)

        val tokenResponse = login(TestUtil.createValidLoginDto(TEST_USERNAME), MyOAuth2Token::class.java)
        authenticate(tokenResponse.body!!.access_token)

        postRequest(createValidRequestDto(), Any::class.java)
        val response = getRequest<Any>(UUID.randomUUID(), object : ParameterizedTypeReference<Any>() {})

        assertThat(response.statusCode).isEqualTo(HttpStatus.NOT_FOUND)
    }

    @Test
    fun getRequest_withValid_receive200() {
        signUp(TestUtil.createValidClientUser(TEST_USERNAME), Any::class.java)

        val tokenResponse = login(TestUtil.createValidLoginDto(TEST_USERNAME), MyOAuth2Token::class.java)
        authenticate(tokenResponse.body!!.access_token)

        val requestResponse = postRequest(createValidRequestDto(), RequestDto.VM::class.java)

        val response: ResponseEntity<Any> = getRequest(requestResponse.body!!.id, object : ParameterizedTypeReference<Any>() {})

        assertThat(response.statusCode).isEqualTo(HttpStatus.OK)
    }

    @Test
    fun getRequest_withValid_receiveVmWithBasicInfo() {
        signUp(TestUtil.createValidClientUser(TEST_USERNAME), Any::class.java)

        val tokenResponse = login(
            TestUtil.createValidLoginDto(TEST_USERNAME),
            MyOAuth2Token::class.java
        )
        authenticate(tokenResponse.body!!.access_token)

        val requestResponse = postRequest(createValidRequestDto(), RequestDto.VM::class.java)
        val response: ResponseEntity<RequestDto.VM> = getRequest(requestResponse.body!!.id, object : ParameterizedTypeReference<RequestDto.VM>() {})

        assertThat(response.body!!.userNickname).isEqualTo("test-nickname")
        assertThat(response.body!!.desiredPrice).isEqualTo(2000)
        assertThat(response.body!!.title).isEqualTo("제목입니다")
        assertThat(response.body!!.description).isEqualTo("설명입니다")
        assertThat(response.body!!.requestType).isEqualTo(Request.RequestType.BACKGROUND)
        assertThat(response.body!!.readCount).isEqualTo(0)
        assertThat(response.body!!.dueDate).isEqualTo(LocalDateTime.of(2022, 12, 25, 23, 59))
    }

    @Test
    fun getRequest_withValid_receiveVmWithChatCount() {
        signUp(TestUtil.createValidClientUser(TEST_USERNAME), Any::class.java)

        val tokenResponse = login(
            TestUtil.createValidLoginDto(TEST_USERNAME),
            MyOAuth2Token::class.java
        )
        authenticate(tokenResponse.body!!.access_token)

        val requestResponse = postRequest(createValidRequestDto(), RequestDto.VM::class.java)

        val response: ResponseEntity<RequestDto.VM> = getRequest(requestResponse.body!!.id, object : ParameterizedTypeReference<RequestDto.VM>() {})

        assertThat(response.body!!.chatCount).isNotNull()
    }

    @Test
    fun getRequest_withValid_receiveVmWithUsersAnotherRequests() {
        signUp(TestUtil.createValidClientUser(TEST_USERNAME), Any::class.java)
        val tokenResponse = login(
            TestUtil.createValidLoginDto(TEST_USERNAME),
            MyOAuth2Token::class.java
        )
        authenticate(tokenResponse.body!!.access_token)
        val dto1 = createValidRequestDto()
        val dto2 = createValidRequestDto()
        dto1.title = "1"
        dto2.title = "2"
        postRequest(dto1, RequestDto.VM::class.java)
        postRequest(dto2, RequestDto.VM::class.java)
        val requestResponse = postRequest(createValidRequestDto(), RequestDto.VM::class.java)

        val response: ResponseEntity<RequestDto.VM> = getRequest(requestResponse.body!!.id, object : ParameterizedTypeReference<RequestDto.VM>() {})

        assertThat(response.body!!.anotherRequests?.size).isEqualTo(2)
    }

    @Test
    fun getRequest_withCompleteRequest_receiveVmWithAcceptRate() {
        signUp(TestUtil.createValidClientUser(TEST_USERNAME), Any::class.java)
        val tokenResponse = login(
            TestUtil.createValidLoginDto(TEST_USERNAME),
            MyOAuth2Token::class.java
        )
        authenticate(tokenResponse.body!!.access_token)
        val dto1 = createValidRequestDto()
        dto1.dueDate = LocalDateTime.of(2022, 6, 1, 12, 12)
        dto1.matchYN = "Y"
        dto1.completeYN = "Y"
        val dto2 = createValidRequestDto()
        dto2.dueDate = LocalDateTime.of(2022, 6, 1, 12, 12)
        dto2.matchYN = "Y"
        dto2.completeYN = "N"
        val dto3 = createValidRequestDto()
        dto3.dueDate = LocalDateTime.of(2022, 6, 1, 12, 12)
        dto3.matchYN = "Y"
        dto3.completeYN = "N"
        val dto4 = createValidRequestDto()
        dto4.dueDate = LocalDateTime.of(2022, 6, 1, 12, 12)
        dto4.matchYN = "Y"
        dto4.completeYN = "N"
        val dto5 = createValidRequestDto()
        dto5.dueDate = LocalDateTime.of(2022, 6, 1, 12, 12)
        dto5.matchYN = "Y"
        dto5.completeYN = "N"

        postRequest(dto1, RequestDto.VM::class.java)
        postRequest(dto2, RequestDto.VM::class.java)
        postRequest(dto3, RequestDto.VM::class.java)
        postRequest(dto4, RequestDto.VM::class.java)

        val requestResponse = postRequest(dto5, RequestDto.VM::class.java)

        val response: ResponseEntity<RequestDto.VM> = getRequest(requestResponse.body!!.id, object : ParameterizedTypeReference<RequestDto.VM>() {})

        assertThat(response.body!!.userAcceptRate).isEqualTo(20.0)
    }

    @Test
    fun getRequest_withoutCompleteRequest_receiveVmWithAcceptRateMinusOne() {
        signUp(TestUtil.createValidClientUser(TEST_USERNAME), Any::class.java)
        val tokenResponse = login(
            TestUtil.createValidLoginDto(TEST_USERNAME),
            MyOAuth2Token::class.java
        )
        authenticate(tokenResponse.body!!.access_token)
        val dto1 = createValidRequestDto()
        dto1.dueDate = LocalDateTime.of(2022, 6, 1, 12, 12)
        dto1.matchYN = "Y"
        dto1.completeYN = "N"
        val dto2 = createValidRequestDto()
        dto2.dueDate = LocalDateTime.of(2022, 6, 1, 12, 12)
        dto2.matchYN = "Y"
        dto2.completeYN = "N"
        postRequest(dto1, RequestDto.VM::class.java)
        val requestResponse = postRequest(dto2, RequestDto.VM::class.java)

        val response: ResponseEntity<RequestDto.VM> = getRequest(requestResponse.body!!.id, object : ParameterizedTypeReference<RequestDto.VM>() {})

        assertThat(response.body!!.userAcceptRate).isEqualTo(-1.0)
    }

    fun <T> postRequest(dto: RequestDto.Create?, responseType: Class<T>): ResponseEntity<T> {
        val headers = HttpHeaders()
        headers["Content-Type"] = MediaType.APPLICATION_JSON_VALUE
        val httpEntity = HttpEntity(dto, headers)
        return testRestTemplate.exchange<T>(API_V_1_REQUESTS, HttpMethod.POST, httpEntity, responseType)
    }

    fun <T> getRequest(id: UUID, responseType: ParameterizedTypeReference<T>): ResponseEntity<T> {
        val url = "$API_V_1_REQUESTS/$id"
        return testRestTemplate.exchange(url, HttpMethod.GET, null, responseType)
    }

    fun <T> getRequests(search: RequestDto.Search?, responseType: ParameterizedTypeReference<T>): ResponseEntity<T> {
        var url: String = createUrlWithRequestParams(search)
        url += if (url.contains("?")) {
            "&page=0&size=20"
        } else {
            "?page=0&size=20"
        }
        return testRestTemplate.exchange(url, HttpMethod.GET, null, responseType)
    }

    fun <T> getRequests(search: RequestDto.Search?, responseType: ParameterizedTypeReference<T>, sortString: String): ResponseEntity<T> {
        var url = createUrlWithRequestParams(search)

        url += if (url.contains("?")) {
            "&page=0&size=20"
        } else {
            "?page=0&size=20"
        }

        url += sortString

        return testRestTemplate.exchange(url, HttpMethod.GET, null, responseType)
    }

    private fun createUrlWithRequestParams(search: RequestDto.Search?): String {
        val builder = UriComponentsBuilder.fromHttpUrl("http://localhost:8080$API_V_1_REQUESTS")
        if (search != null) {
            val params: MultiValueMap<String, String> = LinkedMultiValueMap()

            params.setAll(objectMapper.convertValue(search, Map::class.java) as MutableMap<String, String>)
            builder.queryParams(params)
        }
        return builder.toUriString().split("8080".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[1]
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

    private fun authenticate(token: String) {
        testRestTemplate.restTemplate.interceptors.add(RestTemplateInterceptor(token))
    }
}
