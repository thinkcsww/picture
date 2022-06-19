package com.applory.pictureserver;

import com.applory.pictureserver.domain.config.AppConfiguration;
import com.applory.pictureserver.domain.error.ApiError;
import com.applory.pictureserver.domain.oauth.AuthDto;
import com.applory.pictureserver.domain.oauth.OAuth2Token;
import com.applory.pictureserver.domain.user.User;
import com.applory.pictureserver.domain.user.UserDto;
import com.applory.pictureserver.domain.user.UserRepository;
import com.applory.pictureserver.domain.user.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
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

import static com.applory.pictureserver.TestConstants.*;
import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class UserControllerTest {


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

    @BeforeEach
    public void cleanUp() {
        userRepository.deleteAll();
        testRestTemplate.getRestTemplate().getInterceptors().clear();
    }

    @Test
    public void postClientUser_withValidUserDto_receive201() {
        UserDto.Create dto = TestUtil.createValidClientUser(TEST_USERNAME);

        ResponseEntity<Object> response = signUp(dto, Object.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    }

    @Test
    public void postClientUser_withValidUserDto_userSavedToDatabase() {
        UserDto.Create dto = TestUtil.createValidClientUser(TEST_USERNAME);

        signUp(dto, Object.class);

        assertThat(userRepository.count()).isEqualTo(1);
    }

    @Test
    public void postClientUser_withValidUserDto_receiveUserVMWithProperValues() {
        UserDto.Create dto = TestUtil.createValidClientUser(TEST_USERNAME);

        ResponseEntity<UserDto.VM> response = signUp(dto, UserDto.VM.class);

        assertThat(response.getBody().getNickname()).isNotNull();
        assertThat(response.getBody().getSellerEnabledYn()).isNotNull();
        assertThat(response.getBody().getSnsType()).isNotNull();
        assertThat(response.getBody().getCreatedDt()).isNotNull();
        assertThat(response.getBody().getUpdatedDt()).isNotNull();
    }

    @Test
    public void postSellerUser_withValidUserDto_receiveUserVMWithProperValues() {
        UserDto.Create dto = TestUtil.createValidSellerUser(TEST_USERNAME);

        ResponseEntity<UserDto.VM> response = signUp(dto, UserDto.VM.class);

        assertThat(response.getBody().getNickname()).isNotNull();
        assertThat(response.getBody().getSellerEnabledYn()).isNotNull();
        assertThat(response.getBody().getWorkHourFromDt()).isNotNull();
        assertThat(response.getBody().getWorkHourToDt()).isNotNull();
        assertThat(response.getBody().getSpecialty()).isNotNull();
        assertThat(response.getBody().getSnsType()).isNotNull();
        assertThat(response.getBody().getCreatedDt()).isNotNull();
        assertThat(response.getBody().getUpdatedDt()).isNotNull();
    }

    @Test
    public void getUserMe_withInvalidToken_receiveUnauthorized() {
        authenticate("adasd");

        ResponseEntity<Object> response = getUserMe(new ParameterizedTypeReference<Object>() {});
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);

    }

    @Test
    public void getUserMe_withValidToken_receiveUserVM() {
        signUp(TestUtil.createValidClientUser(TEST_USERNAME), Object.class);

        ResponseEntity<OAuth2Token> tokenResponse = login(TestUtil.createValidLoginDto(TEST_USERNAME), OAuth2Token.class);
        authenticate(tokenResponse.getBody().getAccess_token());

        ResponseEntity<UserDto.VM> response = getUserMe(new ParameterizedTypeReference<UserDto.VM>() {});
        assertThat(response.getBody().getUsername()).isEqualTo(TEST_USERNAME);

    }

    @Test
    public void getUserMe_withValidToken_receiveUserVMWithoutPassword() {
        signUp(TestUtil.createValidClientUser(TEST_USERNAME), Object.class);

        ResponseEntity<OAuth2Token> tokenResponse = login(TestUtil.createValidLoginDto(TEST_USERNAME), OAuth2Token.class);
        authenticate(tokenResponse.getBody().getAccess_token());

        ResponseEntity<String> response = getUserMe(new ParameterizedTypeReference<String>() {});
        assertThat(response.getBody().contains("password")).isFalse();
    }

    @Test
    public void getClientUser_withValidToken_receivePage() {
        signUp(TestUtil.createValidClientUser(TEST_USERNAME), Object.class);

        ResponseEntity<OAuth2Token> tokenResponse = login(TestUtil.createValidLoginDto(TEST_USERNAME), OAuth2Token.class);
        authenticate(tokenResponse.getBody().getAccess_token());

        ResponseEntity<TestPage<UserDto.VM>> userResponse = getClientUser(new ParameterizedTypeReference<TestPage<UserDto.VM>>() {}, null);
        assertThat(userResponse.getBody().getContent().get(0).getSellerEnabledYn()).isEqualTo("N");
    }


    @Test
    public void getSellerUser_withValidToken_receiveOk() {
        signUp(TestUtil.createValidSellerUser(TEST_USERNAME), Object.class);

        ResponseEntity<OAuth2Token> tokenResponse = login(TestUtil.createValidLoginDto(TEST_USERNAME), OAuth2Token.class);
        authenticate(tokenResponse.getBody().getAccess_token());

        UserDto.SearchSeller search = new UserDto.SearchSeller();
        search.setCurrentTime("1750");

        ResponseEntity<Object> userResponse = getSellerUser(new ParameterizedTypeReference<Object>() {}, search, null);
        assertThat(userResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    public void getSellerUsers_withValidToken_receivePage() {
        signUp(TestUtil.createValidSellerUser(TEST_USERNAME), Object.class);

        ResponseEntity<OAuth2Token> tokenResponse = login(TestUtil.createValidLoginDto(TEST_USERNAME), OAuth2Token.class);
        authenticate(tokenResponse.getBody().getAccess_token());

        UserDto.SearchSeller search = new UserDto.SearchSeller();
        search.setCurrentTime("1750");

        ResponseEntity<TestPage<UserDto.VM>> userResponse = getSellerUser(new ParameterizedTypeReference<TestPage<UserDto.VM>>() {}, search, null);
        assertThat(userResponse.getBody().getContent().get(0).getSellerEnabledYn()).isEqualTo("Y");
    }

    @Test
    public void getSellerUsers_withInvalidWorkTime_receiveApiError() {
        signUp(TestUtil.createValidSellerUser(TEST_USERNAME), Object.class);

        ResponseEntity<OAuth2Token> tokenResponse = login(TestUtil.createValidLoginDto(TEST_USERNAME), OAuth2Token.class);
        authenticate(tokenResponse.getBody().getAccess_token());

        UserDto.SearchSeller search = new UserDto.SearchSeller();
        search.setCurrentTime("5555");

        ResponseEntity<ApiError> userResponse = getSellerUser(new ParameterizedTypeReference<ApiError>() {}, search, null);
        assertThat(userResponse.getBody().getValidationErrors()).isNotNull();
    }

// TODO: 시간에 따른 조회 (TO BE)
//    @Test
//    public void getSellerUsers_withWorkTimeSomeOneIsWorking_receiveOneSellerWithPage() {
//        signUp(TestUtil.createValidSellerUser(COMMON_USERNAME), Object.class);
//
//        ResponseEntity<OAuth2Token> tokenResponse = login(TestUtil.createValidLoginDto(COMMON_USERNAME), OAuth2Token.class);
//        authenticate(tokenResponse.getBody().getAccess_token());
//
//        UserDto.SearchSeller search = new UserDto.SearchSeller();
//        search.setCurrentTime("1730");
//
//        ResponseEntity<TestPage<UserDto.UserVM>> userResponse = getSellerUser(new ParameterizedTypeReference<TestPage<UserDto.UserVM>>() {}, search, PageRequest.of(0, 5));
//        assertThat(userResponse.getBody().getTotalElements()).isEqualTo(1);
//    }

    @Test
    public void getSellerUsers_searchWithName_receiveSellerBySpecialty() {
        UserDto.Create user1 = TestUtil.createValidSellerUser("123123");
        UserDto.Create user2 = TestUtil.createValidSellerUser("1231232");
        UserDto.Create user3 = TestUtil.createValidSellerUser("1231233");
        user1.setNickname("test1");
        user2.setNickname("test2");
        user3.setNickname("test3");

        signUp(user1, Object.class);
        signUp(user2, Object.class);
        signUp(user3, Object.class);

        ResponseEntity<OAuth2Token> tokenResponse = login(TestUtil.createValidLoginDto(TEST_USERNAME), OAuth2Token.class);
        authenticate(tokenResponse.getBody().getAccess_token());

        UserDto.SearchSeller search = new UserDto.SearchSeller();
        search.setCurrentTime("1730");
        search.setNickname(user1.getNickname());

        ResponseEntity<TestPage<UserDto.VM>> userResponse = getSellerUser(new ParameterizedTypeReference<TestPage<UserDto.VM>>() {}, search, PageRequest.of(0, 5));
        assertThat(userResponse.getBody().getContent().get(0).getNickname()).isEqualTo(user1.getNickname());
    }

    @Test
    public void getSellerUsers_searchWithSpecialty_receiveSellerBySpecialty() {
        UserDto.Create user1 = TestUtil.createValidSellerUser("123123");
        UserDto.Create user2 = TestUtil.createValidSellerUser("1231232");
        UserDto.Create user3 = TestUtil.createValidSellerUser("1231233");
        user2.setSpecialty(User.SellerSpecialty.BACKGROUND.toString());
        user3.setSpecialty(User.SellerSpecialty.OFFICIAL.toString());

        signUp(user1, Object.class);
        signUp(user2, Object.class);
        signUp(user3, Object.class);

        ResponseEntity<OAuth2Token> tokenResponse = login(TestUtil.createValidLoginDto(TEST_USERNAME), OAuth2Token.class);
        authenticate(tokenResponse.getBody().getAccess_token());

        UserDto.SearchSeller search = new UserDto.SearchSeller();
        search.setCurrentTime("1730");
        search.setSpecialty(User.SellerSpecialty.OFFICIAL.toString());

        ResponseEntity<TestPage<UserDto.VM>> userResponse = getSellerUser(new ParameterizedTypeReference<TestPage<UserDto.VM>>() {}, search, PageRequest.of(0, 5));
        assertThat(userResponse.getBody().getContent().get(0).getSpecialty()).contains(User.SellerSpecialty.OFFICIAL.toString());
    }

    @Test
    public void getSellerUsers_searchWithSpecialty_receiveSellerWhoHasMultipleSpecialty() {
        UserDto.Create user1 = TestUtil.createValidSellerUser("123123");
        UserDto.Create user2 = TestUtil.createValidSellerUser("1231232");
        UserDto.Create user3 = TestUtil.createValidSellerUser("1231233");
        user2.setSpecialty(User.SellerSpecialty.BACKGROUND.toString());
        user3.setSpecialty(User.SellerSpecialty.OFFICIAL.toString() + "," + User.SellerSpecialty.BACKGROUND);

        signUp(user1, Object.class);
        signUp(user2, Object.class);
        signUp(user3, Object.class);

        ResponseEntity<OAuth2Token> tokenResponse = login(TestUtil.createValidLoginDto(TEST_USERNAME), OAuth2Token.class);
        authenticate(tokenResponse.getBody().getAccess_token());

        UserDto.SearchSeller search = new UserDto.SearchSeller();
        search.setCurrentTime("1730");
        search.setSpecialty(User.SellerSpecialty.OFFICIAL.toString());

        ResponseEntity<TestPage<UserDto.VM>> userResponse = getSellerUser(new ParameterizedTypeReference<TestPage<UserDto.VM>>() {}, search, PageRequest.of(0, 5));
        assertThat(userResponse.getBody().getContent().get(0).getSpecialty()).contains(User.SellerSpecialty.OFFICIAL.toString());
    }

// TODO: 시간에 따른 조회 (TO BE)
//    @Test
//    public void getSellerUsers_withWorkTimeNoOneIsWorking_receiveZeroSellerWithPage() {
//        signUp(TestUtil.createValidSellerUser(COMMON_USERNAME), Object.class);
//
//        ResponseEntity<OAuth2Token> tokenResponse = login(TestUtil.createValidLoginDto(COMMON_USERNAME), OAuth2Token.class);
//        authenticate(tokenResponse.getBody().getAccess_token());
//
//        UserDto.SearchSeller search = new UserDto.SearchSeller();
//        search.setCurrentTime("500");
//
//        ResponseEntity<TestPage<UserDto.UserVM>> userResponse = getSellerUser(new ParameterizedTypeReference<TestPage<UserDto.UserVM>>() {}, search, null);
//        assertThat(userResponse.getBody().getTotalElements()).isEqualTo(0);
//    }

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

    public <T> ResponseEntity<T> getSellerUser(ParameterizedTypeReference<T> responseType, UserDto.SearchSeller search, PageRequest pageRequest) {
        String url = createUrlWithRequestParamsSeller(search, pageRequest);

        return testRestTemplate.exchange(url, HttpMethod.GET, null, responseType);
    }

    public <T> ResponseEntity<T> getClientUser(ParameterizedTypeReference<T> responseType, UserDto.SearchClient search) {
        String url = createUrlWithRequestParamsClient(search);

        return testRestTemplate.exchange(url, HttpMethod.GET, null, responseType);
    }

    private String createUrlWithRequestParamsSeller(UserDto.SearchSeller search, Pageable pageable) {
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl("http://localhost:8080" + API_V_1_USERS);
        MultiValueMap params = new LinkedMultiValueMap<>();
        if (search != null) {
            params.setAll(objectMapper.convertValue(search, Map.class));
            builder.queryParams(params);
        }

        if (pageable != null) {
            params.set("page", pageable.getPageNumber());
            params.set("size", pageable.getPageSize());
        }

        return builder.toUriString().split("8080")[1];
    }

    private String createUrlWithRequestParamsClient(UserDto.SearchClient search) {
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl("http://localhost:8080" + API_V_1_USERS_CLIENT);
        if (search != null) {
            MultiValueMap params = new LinkedMultiValueMap<>();
            params.setAll(objectMapper.convertValue(search, Map.class));
            builder.queryParams(params);
        }

        return builder.toUriString().split("8080")[1];
    }

    private void authenticate(String token) {
        testRestTemplate.getRestTemplate().getInterceptors().add(new RestTemplateInterceptor(token));
    }

}
