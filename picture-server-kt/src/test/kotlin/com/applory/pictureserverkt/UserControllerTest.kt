package com.applory.pictureserverkt

import com.applory.pictureserverkt.TestConstant.API_1_0_AUTH_LOGIN
import com.applory.pictureserverkt.TestConstant.API_V_1_USERS
import com.applory.pictureserverkt.TestConstant.API_V_1_USERS_CLIENT
import com.applory.pictureserverkt.TestConstant.API_V_1_USERS_ME
import com.applory.pictureserverkt.TestConstant.API_V_1_USERS_SELLER
import com.applory.pictureserverkt.TestConstant.TEST_USERNAME
import com.applory.pictureserverkt.config.AppConfiguration
import com.applory.pictureserverkt.error.ApiError
import com.applory.pictureserverkt.oauth.AuthDto
import com.applory.pictureserverkt.oauth.MyOAuth2Token
import com.applory.pictureserverkt.user.User
import com.applory.pictureserverkt.user.UserDto
import com.applory.pictureserverkt.user.UserRepository
import com.applory.pictureserverkt.user.UserService
import com.fasterxml.jackson.databind.ObjectMapper
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.boot.web.server.LocalServerPort
import org.springframework.core.ParameterizedTypeReference
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.http.*
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.util.LinkedMultiValueMap
import org.springframework.util.MultiValueMap
import org.springframework.web.util.UriComponentsBuilder

@ExtendWith(SpringExtension::class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class UserControllerTest(
    @Autowired private val testRestTemplate: TestRestTemplate,
    @Autowired private val userService: UserService,
    @Autowired private val appConfiguration: AppConfiguration,
    @Autowired private val userRepository: UserRepository,
    @Autowired private val objectMapper: ObjectMapper,
    @LocalServerPort private val port: Int
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

    @Test
    fun getUserMe_withValidToken_receiveUserVMWithoutPassword() {
        signUp(TestUtil.createValidClientUser(TEST_USERNAME), Any::class.java)

        val tokenResponse: ResponseEntity<MyOAuth2Token> = login(TestUtil.createValidLoginDto(TEST_USERNAME), MyOAuth2Token::class.java)
        authenticate(tokenResponse.body!!.access_token)

        val response = getUserMe<String>(object : ParameterizedTypeReference<String>() {})
        assertThat(response.body!!.contains("password")).isFalse
    }

    @Test
    fun getClientUser_withValidToken_receivePage() {
        signUp(TestUtil.createValidClientUser(TEST_USERNAME), Any::class.java)

        val tokenResponse: ResponseEntity<MyOAuth2Token> = login(TestUtil.createValidLoginDto(TEST_USERNAME), MyOAuth2Token::class.java)
        authenticate(tokenResponse.body!!.access_token)

        val userResponse: ResponseEntity<TestPage<UserDto.VM>> = getClientUser(object : ParameterizedTypeReference<TestPage<UserDto.VM>>() {}, UserDto.SearchClient())
        assertThat(userResponse.body!!.content[0].sellerEnabledYn).isEqualTo("N")
    }

    @Test
    fun getSellerUsers_withValidRequest_receivePage() {
        signUp(TestUtil.createValidSellerUser(TEST_USERNAME), Any::class.java)

        val search: UserDto.SearchSeller = UserDto.SearchSeller(currentTime = "1750")

        val userResponse: ResponseEntity<TestPage<UserDto.VM>> = getSellerUser(object : ParameterizedTypeReference<TestPage<UserDto.VM>>() {}, search, null)
        assertThat(userResponse.body!!.content[0].sellerEnabledYn).isEqualTo("Y")
    }

    @Test
    fun getSellerUsers_withInvalidWorkTime_receiveBadRequest() {
        signUp(TestUtil.createValidSellerUser(TEST_USERNAME), Any::class.java)
        val search = UserDto.SearchSeller(currentTime = "5555")
        val userResponse: ResponseEntity<ApiError> = getSellerUser(object : ParameterizedTypeReference<ApiError>() {}, search, null)
        assertThat(userResponse.statusCode).isEqualTo(HttpStatus.BAD_REQUEST)
    }

    @Test
    fun getSellerUsers_withInvalidWorkTime_receiveApiError() {
        signUp(TestUtil.createValidSellerUser(TEST_USERNAME), Any::class.java)
        val search = UserDto.SearchSeller(currentTime = "5555")
        val userResponse: ResponseEntity<ApiError> = getSellerUser(object : ParameterizedTypeReference<ApiError>() {}, search, null)
        assertThat(userResponse.body!!.validationErrors.size).isGreaterThan(0)
    }

    // TODO: 시간에 따른 조회 (TO BE)
    //    @Test
    //    public void getSellerUsers_withWorkTimeSomeOneIsWorking_receiveOneSellerWithPage() {
    //        signUp(TestUtil.createValidSellerUser(COMMON_USERNAME), Object.class);
    //
    //        UserDto.SearchSeller search = new UserDto.SearchSeller();
    //        search.setCurrentTime("1730");
    //
    //        ResponseEntity<TestPage<UserDto.UserVM>> userResponse = getSellerUser(new ParameterizedTypeReference<TestPage<UserDto.UserVM>>() {}, search, PageRequest.of(0, 5));
    //        assertThat(userResponse.getBody().getTotalElements()).isEqualTo(1);
    //    }
    @Test
    fun getSellerUsers_searchWithNickname_receiveSellerByNickname() {
        val user1 = TestUtil.createValidSellerUser("123123")
        val user2 = TestUtil.createValidSellerUser("1231232")
        val user3 = TestUtil.createValidSellerUser("1231233")
        user1.nickname = "test1"
        user2.nickname = "test2"
        user3.nickname = "test3"

        signUp(user1, Any::class.java)
        signUp(user2, Any::class.java)
        signUp(user3, Any::class.java)

        val search = UserDto.SearchSeller(currentTime = "1730", nickname = user1.nickname)

        val userResponse: ResponseEntity<TestPage<UserDto.VM>> = getSellerUser(object : ParameterizedTypeReference<TestPage<UserDto.VM>>() {}, search, PageRequest.of(0, 5))

        assertThat(userResponse.body!!.content[0].nickname).isEqualTo(user1.nickname)
    }

    @Test
    fun getSellerUsers_searchWithSpecialty_receiveSellerBySpecialty() {
        val user1 = TestUtil.createValidSellerUser("123123")
        val user2 = TestUtil.createValidSellerUser("1231232")
        val user3 = TestUtil.createValidSellerUser("1231233")
        user2.specialty = User.SellerSpecialty.BACKGROUND.toString()
        user3.specialty = User.SellerSpecialty.OFFICIAL.toString()

        signUp(user1, Any::class.java)
        signUp(user2, Any::class.java)
        signUp(user3, Any::class.java)

        val search = UserDto.SearchSeller(currentTime = "1730", specialty = User.SellerSpecialty.OFFICIAL.toString())

        val userResponse: ResponseEntity<TestPage<UserDto.VM>> = getSellerUser(object : ParameterizedTypeReference<TestPage<UserDto.VM>>() {}, search, PageRequest.of(0, 5))

        assertThat(userResponse.body!!.content[0].specialty).isEqualTo(User.SellerSpecialty.OFFICIAL.toString())
    }

    @Test
    fun getSellerUsers_searchWithSpecialty_receiveSellerWhoHasMultipleSpecialty() {
        val user1 = TestUtil.createValidSellerUser("123123")
        val user2 = TestUtil.createValidSellerUser("1231232")
        val user3 = TestUtil.createValidSellerUser("1231233")
        user2.specialty = User.SellerSpecialty.BACKGROUND.toString()
        user3.specialty = "${User.SellerSpecialty.OFFICIAL},${User.SellerSpecialty.BACKGROUND}"

        signUp(user1, Any::class.java)
        signUp(user2, Any::class.java)
        signUp(user3, Any::class.java)

        val search = UserDto.SearchSeller(currentTime = "1730", specialty = User.SellerSpecialty.OFFICIAL.toString())

        val userResponse: ResponseEntity<TestPage<UserDto.VM>> = getSellerUser(object : ParameterizedTypeReference<TestPage<UserDto.VM>>() {}, search, PageRequest.of(0, 5))

        assertThat(userResponse.body!!.content[0].specialty).contains(User.SellerSpecialty.OFFICIAL.toString())
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

    fun <T> getClientUser(responseType: ParameterizedTypeReference<T>, search: UserDto.SearchClient): ResponseEntity<T> {
        val url: String = createUrlWithRequestParamsClient(search)
        return testRestTemplate.exchange(url, HttpMethod.GET, null, responseType)
    }

    private fun createUrlWithRequestParamsClient(search: UserDto.SearchClient): String {
        val builder = UriComponentsBuilder.fromHttpUrl("http://localhost:$port$API_V_1_USERS_CLIENT")
        if (search != null) {
            val params: MultiValueMap<String, String> = LinkedMultiValueMap()
            params.setAll(objectMapper.convertValue(search, Map::class.java) as MutableMap<String, String>)
            builder.queryParams(params)
        }
        return builder.toUriString().split("$port".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[1]
    }

    fun <T> getSellerUser(
        responseType: ParameterizedTypeReference<T>,
        search: UserDto.SearchSeller,
        pageRequest: PageRequest?
    ): ResponseEntity<T> {
        val url: String = createUrlWithRequestParamsSeller(search, pageRequest)
        return testRestTemplate.exchange(url, HttpMethod.GET, null, responseType)
    }

    private fun createUrlWithRequestParamsSeller(search: UserDto.SearchSeller?, pageable: Pageable?): String {
        val builder = UriComponentsBuilder.fromHttpUrl("http://localhost:$port$API_V_1_USERS_SELLER")
        val params: MultiValueMap<String, String> = LinkedMultiValueMap()
        if (search != null) {
            params.setAll(objectMapper.convertValue(search, Map::class.java) as MutableMap<String, String>)
            builder.queryParams(params)
        }
        if (pageable != null) {
            params.set("page", pageable.pageNumber.toString())
            params.set("size", pageable.pageSize.toString())
        }
        return builder.toUriString().split("$port".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[1]
    }

    private fun authenticate(token: String) {
        testRestTemplate.restTemplate.interceptors.add(RestTemplateInterceptor(token))
    }


}
