package com.applory.pictureserver.domain.user;

import com.applory.pictureserver.RestTemplateInterceptor;
import com.applory.pictureserver.TestPage;
import com.applory.pictureserver.TestUtil;
import com.applory.pictureserver.config.AppConfiguration;
import com.applory.pictureserver.domain.oauth.AuthDto;
import com.applory.pictureserver.domain.oauth.MyOAuth2Token;
import com.applory.pictureserver.error.ApiError;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.applory.pictureserver.TestConstants.*;
import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class UserControllerTest {

    @LocalServerPort
    private Integer port;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    TestRestTemplate testRestTemplate;

    @Autowired
    UserService userService;

    @Autowired
    AppConfiguration appConfiguration;

    @Autowired
    UserRepository userRepository;

    @AfterEach
    public void cleanUp() {
        userRepository.deleteAll();
        testRestTemplate.getRestTemplate().getInterceptors().clear();
    }

    @Test
    public void postClientUser_withValidUserDto_receive201() {
        UserDto.Create dto = TestUtil.createValidClientUser(TEST_SELLER_USERNAME);

        ResponseEntity<Object> response = signUp(dto, Object.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    }

    @Test
    public void postUser_withDuplicateNickname_receiveApiError() {
        UserDto.Create user1 = new UserDto.Create();
        user1.setUsername("username1");
        user1.setNickname("nickname1");
        user1.setSnsType(User.SnsType.KAKAO);

        UserDto.Create user2 = new UserDto.Create();
        user2.setUsername("username1");
        user2.setNickname("nickname1");
        user2.setSnsType(User.SnsType.KAKAO);

        signUp(user1, Object.class);

        ResponseEntity<ApiError> response = signUp(user2, ApiError.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody().getValidationErrors().get("nickname")).isNotNull();
    }

    @Test
    public void getUserMe_withInvalidToken_receiveUnauthorized() {
        authenticate("adasd");

        ResponseEntity<Object> response = getUserMe(new ParameterizedTypeReference<Object>() {});
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    public void getUserMe_withValidToken_receive200() {
        signUp(TestUtil.createValidClientUser(TEST_SELLER_USERNAME), Object.class);

        ResponseEntity<MyOAuth2Token> tokenResponse = login(TestUtil.createValidLoginDto(TEST_SELLER_USERNAME), MyOAuth2Token.class);
        authenticate(tokenResponse.getBody().getAccess_token());

        ResponseEntity<UserDto.VM> response = getUserMe(new ParameterizedTypeReference<UserDto.VM>() {});
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    }

    @Test
    public void getSellerUsers_withValidRequest_receive200() {
        signUp(TestUtil.createValidSellerUser(TEST_SELLER_USERNAME), Object.class);

        UserDto.SearchSeller search = new UserDto.SearchSeller();
        search.setCurrentTime("1750");

        ResponseEntity<TestPage<UserDto.SellerVM>> response = getSellerUsers(new ParameterizedTypeReference<TestPage<UserDto.SellerVM>>() {}, search, null);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    public void getSellerUsers_withInvalidWorkTime_receiveApiError() {
        signUp(TestUtil.createValidSellerUser(TEST_SELLER_USERNAME), Object.class);

        UserDto.SearchSeller search = new UserDto.SearchSeller();
        search.setCurrentTime("5555");

        ResponseEntity<ApiError> userResponse = getSellerUsers(new ParameterizedTypeReference<ApiError>() {}, search, null);
        assertThat(userResponse.getBody().getValidationErrors()).isNotNull();
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
    public void getSellerUser_withUnExistId_receive404() {
        ResponseEntity<UserDto.VM> userResponse = signUp(TestUtil.createValidSellerUser("123123"), UserDto.VM.class);
        ResponseEntity<Object> response = getSellerUser(new ParameterizedTypeReference<Object>() {}, UUID.randomUUID().toString());

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    public void getSellerUser_withExistId_receive200() {
        ResponseEntity<UserDto.VM> userResponse = signUp(TestUtil.createValidSellerUser("123123"), UserDto.VM.class);
        ResponseEntity<Object> response = getSellerUser(new ParameterizedTypeReference<Object>() {}, userResponse.getBody().getId());
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }


    public <T> ResponseEntity<T> checkNickname(String nickname, Class<T> responseType) {
        String url = API_V_1_USERS_CHECK_NICK_NAME + "?nickname=" + nickname;
        return testRestTemplate.exchange(url, HttpMethod.GET, null, responseType);
    }

    public <T> ResponseEntity<T> signUp(UserDto.Create dto, Class<T> responseType) {
        return testRestTemplate.postForEntity(API_V_1_USERS, dto, responseType);
    }

    public <T> ResponseEntity<T> login(AuthDto.Login dto, Class<T> responseType) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", MediaType.APPLICATION_JSON_VALUE);
        HttpEntity<AuthDto.Login> httpEntity = new HttpEntity<>(dto, headers);

        return testRestTemplate.postForEntity(API_1_0_AUTH_LOGIN, httpEntity, responseType);
    }

    public <T> ResponseEntity<T> getUserMe(ParameterizedTypeReference<T> responseType) {
        return testRestTemplate.exchange(API_V_1_USERS_ME, HttpMethod.GET, null, responseType);
    }

    public <T> ResponseEntity<T> getSellerUsers(ParameterizedTypeReference<T> responseType, UserDto.SearchSeller search, PageRequest pageRequest) {
        String url = createUrlWithRequestParamsSeller(search, pageRequest);

        return testRestTemplate.exchange(url, HttpMethod.GET, null, responseType);
    }

    public <T> ResponseEntity<T> getSellerUser(ParameterizedTypeReference<T> responseType, String id) {
        String url = API_V_1_USERS_SELLER + "/" + id;
        return testRestTemplate.exchange(url, HttpMethod.GET, null, responseType);
    }

    public <T> ResponseEntity<T> getClientUser(ParameterizedTypeReference<T> responseType, UserDto.SearchClient search) {
        String url = createUrlWithRequestParamsClient(search);

        return testRestTemplate.exchange(url, HttpMethod.GET, null, responseType);
    }

    private String createUrlWithRequestParamsSeller(UserDto.SearchSeller search, Pageable pageable) {
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl("http://localhost" + port + API_V_1_USERS_SELLER);
        MultiValueMap params = new LinkedMultiValueMap<>();

        if (pageable != null) {
            params.set("page", String.valueOf(pageable.getPageNumber()));
            params.set("size", String.valueOf(pageable.getPageSize()));
            if (!pageable.getSort().get().collect(Collectors.toList()).isEmpty()) {
                params.set("sort", pageable.getSort().get().collect(Collectors.toList()).get(0).getProperty() + "," + pageable.getSort().get().collect(Collectors.toList()).get(0).getDirection().toString());
            }
        }

        if (search != null) {
            params.setAll(objectMapper.convertValue(search, Map.class));

        }

        if (!params.keySet().isEmpty()) {
            builder.queryParams(params);
        }


        return builder.toUriString().split(port + "")[1];
    }

    private String createUrlWithRequestParamsClient(UserDto.SearchClient search) {
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl("http://localhost:" + port + API_V_1_USERS_CLIENT);
        if (search != null) {
            MultiValueMap params = new LinkedMultiValueMap<>();
            params.setAll(objectMapper.convertValue(search, Map.class));
            builder.queryParams(params);
        }

        return builder.toUriString().split(port + "")[1];
    }

    private void authenticate(String token) {
        testRestTemplate.getRestTemplate().getInterceptors().add(new RestTemplateInterceptor(token));
    }

}
