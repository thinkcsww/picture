package com.applory.pictureserver.controller;

import com.applory.pictureserver.RestTemplateInterceptor;
import com.applory.pictureserver.TestPage;
import com.applory.pictureserver.TestUtil;
import com.applory.pictureserver.config.AppConfiguration;
import com.applory.pictureserver.error.ApiError;
import com.applory.pictureserver.domain.oauth.AuthDto;
import com.applory.pictureserver.domain.oauth.MyOAuth2Token;
import com.applory.pictureserver.shared.Constant;
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
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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

    @BeforeEach
    public void cleanUp() {
        userRepository.deleteAll();
        testRestTemplate.getRestTemplate().getInterceptors().clear();
    }

    @Test
    public void checkNickname_withDuplicateNickname_receive400() {
        UserDto.Create dto = TestUtil.createValidClientUser(TEST_USERNAME);

        signUp(dto, Object.class);

        ResponseEntity<Object> response = checkNickname(dto.getNickname(), Object.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);

    }

    @Test
    public void checkNickname_withDuplicateNickname_receiveMessage() {
        UserDto.Create dto = TestUtil.createValidClientUser(TEST_USERNAME);

        signUp(dto, Object.class);

        ResponseEntity<ApiError> response = checkNickname(dto.getNickname(), ApiError.class);

        assertThat(response.getBody().getMessage()).contains("is already in use");

    }

    @Test
    public void checkNickname_withValidNickname_receive200() {
        ResponseEntity<Object> response = checkNickname("fresh-nickname", Object.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    public void postClientUser_withValidUserDto_receive201() {
        UserDto.Create dto = TestUtil.createValidClientUser(TEST_USERNAME);

        ResponseEntity<Object> response = signUp(dto, Object.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    }

    @Test
    public void postUser_withDuplicateNickname_receiveApiError() {
        UserDto.Create user1 = TestUtil.createValidClientUser(TEST_USERNAME);
        UserDto.Create user2 = TestUtil.createValidClientUser(TEST_USERNAME);

        signUp(user1, Object.class);
        ResponseEntity<ApiError> response = signUp(user2, ApiError.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody().getValidationErrors().get("nickname")).isNotNull();
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
        assertThat(response.getBody().getSellerEnabledYN()).isNotNull();
        assertThat(response.getBody().getSnsType()).isNotNull();
        assertThat(response.getBody().getCreatedDt()).isNotNull();
        assertThat(response.getBody().getUpdatedDt()).isNotNull();
    }

    @Test
    public void postSellerUser_withValidUserDto_receiveUserVMWithProperValues() {
        UserDto.Create dto = TestUtil.createValidSellerUser(TEST_USERNAME);

        ResponseEntity<UserDto.VM> response = signUp(dto, UserDto.VM.class);

        assertThat(response.getBody().getNickname()).isEqualTo(dto.getNickname());
        assertThat(response.getBody().getSellerEnabledYN()).isEqualTo(dto.getSellerEnabledYN());
        assertThat(response.getBody().getWorkHourFromDt()).isEqualTo(dto.getWorkHourFromDt());
        assertThat(response.getBody().getWorkHourToDt()).isEqualTo(dto.getWorkHourToDt());
        assertThat(response.getBody().getSpecialty()).isEqualTo(dto.getSpecialty());
        assertThat(response.getBody().getSnsType()).isEqualTo(dto.getSnsType());
        assertThat(response.getBody().getCreatedDt()).isNotNull();
        assertThat(response.getBody().getUpdatedDt()).isNotNull();
        assertThat(response.getBody().getPeoplePrice()).isEqualTo(dto.getPeoplePrice());
        assertThat(response.getBody().getBackgroundPrice()).isEqualTo(dto.getBackgroundPrice());
        assertThat(response.getBody().getOfficialPrice()).isEqualTo(dto.getOfficialPrice());
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

        ResponseEntity<MyOAuth2Token> tokenResponse = login(TestUtil.createValidLoginDto(TEST_USERNAME), MyOAuth2Token.class);
        authenticate(tokenResponse.getBody().getAccess_token());

        ResponseEntity<UserDto.VM> response = getUserMe(new ParameterizedTypeReference<UserDto.VM>() {});
        assertThat(response.getBody().getUsername()).isEqualTo(TEST_USERNAME);

    }

    @Test
    public void getUserMe_withValidToken_receiveUserVMWithoutPassword() {
        signUp(TestUtil.createValidClientUser(TEST_USERNAME), Object.class);

        ResponseEntity<MyOAuth2Token> tokenResponse = login(TestUtil.createValidLoginDto(TEST_USERNAME), MyOAuth2Token.class);
        authenticate(tokenResponse.getBody().getAccess_token());

        ResponseEntity<String> response = getUserMe(new ParameterizedTypeReference<String>() {});
        assertThat(response.getBody().contains("password")).isFalse();
    }

    @Test
    public void getClientUser_withValidToken_receivePage() {
        signUp(TestUtil.createValidClientUser(TEST_USERNAME), Object.class);

        ResponseEntity<MyOAuth2Token> tokenResponse = login(TestUtil.createValidLoginDto(TEST_USERNAME), MyOAuth2Token.class);
        authenticate(tokenResponse.getBody().getAccess_token());

        ResponseEntity<TestPage<UserDto.VM>> userResponse = getClientUser(new ParameterizedTypeReference<TestPage<UserDto.VM>>() {}, null);
        assertThat(userResponse.getBody().getContent().get(0).getSellerEnabledYN()).isEqualTo("N");
    }

    @Test
    public void getSellerUsers_withValidRequest_receivePage() {
        signUp(TestUtil.createValidSellerUser(TEST_USERNAME), Object.class);

        UserDto.SearchSeller search = new UserDto.SearchSeller();
        search.setCurrentTime("1750");

        ResponseEntity<TestPage<UserDto.SellerVM>> userResponse = getSellerUsers(new ParameterizedTypeReference<TestPage<UserDto.SellerVM>>() {}, search, null);
        assertThat(userResponse.getBody().getContent().get(0).getSellerEnabledYN()).isEqualTo("Y");
    }

    @Test
    public void getSellerUsers_withInvalidWorkTime_receiveApiError() {
        signUp(TestUtil.createValidSellerUser(TEST_USERNAME), Object.class);

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
    public void getSellerUsers_withValidRequest_receiveWithBasicValues() {
        UserDto.Create dto = TestUtil.createValidSellerUser(TEST_USERNAME);
        signUp(dto, Object.class);

        UserDto.SearchSeller search = new UserDto.SearchSeller();
        search.setCurrentTime("1750");

        ResponseEntity<TestPage<UserDto.SellerVM>> userResponse = getSellerUsers(new ParameterizedTypeReference<TestPage<UserDto.SellerVM>>() {}, search, null);

        assertThat(userResponse.getBody().getContent().get(0).getSellerEnabledYN()).isEqualTo("Y");
        assertThat(userResponse.getBody().getContent().get(0).getNickname()).isEqualTo(dto.getNickname());
        assertThat(userResponse.getBody().getContent().get(0).getDescription()).isEqualTo(dto.getDescription());
        assertThat(userResponse.getBody().getContent().get(0).getSpecialty()).isEqualTo(Constant.Specialty.PEOPLE.toString());
        assertThat(userResponse.getBody().getContent().get(0).getPeoplePrice()).isEqualTo(dto.getPeoplePrice());
        assertThat(userResponse.getBody().getContent().get(0).getOfficialPrice()).isEqualTo(dto.getOfficialPrice());
        assertThat(userResponse.getBody().getContent().get(0).getBackgroundPrice()).isEqualTo(dto.getBackgroundPrice());
    }

    @Test
    public void getSellerUsers_searchWithNickname_receiveSellerByNickname() {
        UserDto.Create user1 = TestUtil.createValidSellerUser("123123");
        UserDto.Create user2 = TestUtil.createValidSellerUser("1231232");
        UserDto.Create user3 = TestUtil.createValidSellerUser("1231233");
        user1.setNickname("test1");
        user2.setNickname("test2");
        user3.setNickname("test3");

        signUp(user1, Object.class);
        signUp(user2, Object.class);
        signUp(user3, Object.class);


        UserDto.SearchSeller search = new UserDto.SearchSeller();
        search.setCurrentTime("1730");
        search.setNickname(user1.getNickname());

        ResponseEntity<TestPage<UserDto.SellerVM>> userResponse = getSellerUsers(new ParameterizedTypeReference<TestPage<UserDto.SellerVM>>() {}, search, PageRequest.of(0, 5));
        assertThat(userResponse.getBody().getContent().get(0).getNickname()).isEqualTo(user1.getNickname());
    }

    @Test
    public void getSellerUsers_searchWithSpecialty_receiveSellerBySpecialty() {
        UserDto.Create user1 = TestUtil.createValidSellerUser("123123");
        UserDto.Create user2 = TestUtil.createValidSellerUser("1231232");
        user2.setNickname("test-nickname2");
        user2.setSpecialty(Constant.Specialty.BACKGROUND.toString());
        UserDto.Create user3 = TestUtil.createValidSellerUser("1231233");
        user3.setNickname("test-nickname3");
        user3.setSpecialty(Constant.Specialty.OFFICIAL.toString());


        signUp(user1, Object.class);
        signUp(user2, Object.class);
        signUp(user3, Object.class);

        UserDto.SearchSeller search = new UserDto.SearchSeller();
        search.setCurrentTime("1730");
        search.setSpecialty(Constant.Specialty.OFFICIAL.toString());

        ResponseEntity<TestPage<UserDto.SellerVM>> userResponse = getSellerUsers(new ParameterizedTypeReference<TestPage<UserDto.SellerVM>>() {}, search, PageRequest.of(0, 5));
        assertThat(userResponse.getBody().getContent().get(0).getSpecialty()).contains(Constant.Specialty.OFFICIAL.toString());
    }

    @Test
    public void getSellerUsers_searchWithSpecialty_receiveSellerWhoHasMultipleSpecialty() {
        UserDto.Create user1 = TestUtil.createValidSellerUser("123123");
        UserDto.Create user2 = TestUtil.createValidSellerUser("1231232");
        user2.setNickname("test-nickname2");
        user2.setSpecialty(Constant.Specialty.BACKGROUND.toString());
        UserDto.Create user3 = TestUtil.createValidSellerUser("1231233");
        user3.setNickname("test-nickname3");
        user3.setSpecialty(Constant.Specialty.OFFICIAL.toString() + "," + Constant.Specialty.BACKGROUND);

        signUp(user1, Object.class);
        signUp(user2, Object.class);
        signUp(user3, Object.class);

        UserDto.SearchSeller search = new UserDto.SearchSeller();
        search.setCurrentTime("1730");
        search.setSpecialty(Constant.Specialty.OFFICIAL.toString());

        ResponseEntity<TestPage<UserDto.SellerVM>> userResponse = getSellerUsers(new ParameterizedTypeReference<TestPage<UserDto.SellerVM>>() {}, search, PageRequest.of(0, 5));
        assertThat(userResponse.getBody().getContent().get(0).getSpecialty()).contains(Constant.Specialty.OFFICIAL.toString());
    }

    @Test
    public void getSellerUsers_orderByPriceDesc_receiveSellerOrderByPriceAsc() {
        UserDto.Create user1 = TestUtil.createValidSellerUser("123123");
        UserDto.Create user2 = TestUtil.createValidSellerUser("1231232");
        UserDto.Create user3 = TestUtil.createValidSellerUser("1231233");
        user2.setSpecialty(Constant.Specialty.PEOPLE.toString());
        user2.setPeoplePrice(3000);
        user2.setNickname("test-nickname2");
        user3.setSpecialty(Constant.Specialty.PEOPLE.toString() + "," + Constant.Specialty.BACKGROUND);
        user3.setPeoplePrice(5000);
        user3.setNickname("test-nickname3");

        signUp(user1, Object.class);
        signUp(user2, Object.class);
        signUp(user3, Object.class);

        UserDto.SearchSeller search = new UserDto.SearchSeller();
        search.setCurrentTime("1730");
        search.setSpecialty(Constant.Specialty.PEOPLE.toString());

        ResponseEntity<TestPage<UserDto.SellerVM>> userResponse = getSellerUsers(new ParameterizedTypeReference<TestPage<UserDto.SellerVM>>() {}, search, PageRequest.of(0, 5, Sort.by("peoplePrice").ascending()));
        assertThat(userResponse.getBody().getContent().get(0).getNickname()).isEqualTo(user1.getNickname());
        assertThat(userResponse.getBody().getContent().get(1).getNickname()).isEqualTo(user2.getNickname());
        assertThat(userResponse.getBody().getContent().get(2).getNickname()).isEqualTo(user3.getNickname());
    }

    @Test
    public void getSellerUsers_orderByPriceAsc_receiveSellerOrderByPriceDesc() {
        UserDto.Create user1 = TestUtil.createValidSellerUser("123123");
        UserDto.Create user2 = TestUtil.createValidSellerUser("1231232");
        UserDto.Create user3 = TestUtil.createValidSellerUser("1231233");
        user2.setNickname("test-nickname2");
        user2.setSpecialty(Constant.Specialty.PEOPLE.toString());
        user2.setPeoplePrice(3000);
        user3.setNickname("test-nickname3");
        user3.setSpecialty(Constant.Specialty.PEOPLE.toString() + "," + Constant.Specialty.BACKGROUND);
        user3.setPeoplePrice(5000);

        signUp(user1, Object.class);
        signUp(user2, Object.class);
        signUp(user3, Object.class);

        UserDto.SearchSeller search = new UserDto.SearchSeller();
        search.setCurrentTime("1730");
        search.setSpecialty(Constant.Specialty.PEOPLE.toString());

        ResponseEntity<TestPage<UserDto.SellerVM>> userResponse = getSellerUsers(new ParameterizedTypeReference<TestPage<UserDto.SellerVM>>() {}, search, PageRequest.of(0, 5, Sort.by("peoplePrice").descending()));
        assertThat(userResponse.getBody().getContent().get(0).getNickname()).isEqualTo(user3.getNickname());
        assertThat(userResponse.getBody().getContent().get(1).getNickname()).isEqualTo(user2.getNickname());
        assertThat(userResponse.getBody().getContent().get(2).getNickname()).isEqualTo(user1.getNickname());
    }

    @Test
    public void getSellerUser_withUnExistId_receive404() {
        ResponseEntity<Object> response = getSellerUser(new ParameterizedTypeReference<Object>() {}, UUID.randomUUID());
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    public void getSellerUser_withExistId_receive200() {
        ResponseEntity<UserDto.VM> userResponse = signUp(TestUtil.createValidSellerUser("123123"), UserDto.VM.class);
        ResponseEntity<Object> response = getSellerUser(new ParameterizedTypeReference<Object>() {}, userResponse.getBody().getId());
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    public void getSellerUser_withExistId_receiveSellerVM() {
        ResponseEntity<UserDto.VM> userResponse = signUp(TestUtil.createValidSellerUser("123123"), UserDto.VM.class);
        ResponseEntity<UserDto.SellerVM> response = getSellerUser(new ParameterizedTypeReference<UserDto.SellerVM>() {}, userResponse.getBody().getId());
        assertThat(response.getBody().getReviewCount()).isEqualTo(0);
    }

    @Test
    public void getSellerUser_withExistId_receiveSellerVMWithBasicValue() {
        UserDto.Create user1 = TestUtil.createValidSellerUser("123123");
        ResponseEntity<UserDto.VM> userResponse = signUp(user1, UserDto.VM.class);
        ResponseEntity<UserDto.SellerVM> response = getSellerUser(new ParameterizedTypeReference<UserDto.SellerVM>() {}, userResponse.getBody().getId());


        assertThat(response.getBody().getId()).isEqualTo(response.getBody().getId());
        assertThat(response.getBody().getSellerEnabledYN()).isEqualTo("Y");
        assertThat(response.getBody().getWorkHourFromDt()).isEqualTo(user1.getWorkHourFromDt());
        assertThat(response.getBody().getWorkHourToDt()).isEqualTo(user1.getWorkHourToDt());
        assertThat(response.getBody().getNickname()).isEqualTo(user1.getNickname());
        assertThat(response.getBody().getDescription()).isEqualTo(user1.getDescription());
        assertThat(response.getBody().getSpecialty()).isEqualTo(Constant.Specialty.PEOPLE.toString());
        assertThat(response.getBody().getPeoplePrice()).isEqualTo(user1.getPeoplePrice());
        assertThat(response.getBody().getOfficialPrice()).isEqualTo(user1.getOfficialPrice());
        assertThat(response.getBody().getBackgroundPrice()).isEqualTo(user1.getBackgroundPrice());
    }

    @Test
    public void getSellerUser_withExistId_receiveSellerVMWithReviewCount() {
        assertThat(true).isFalse();
    }

    @Test
    public void getSellerUser_withExistId_receiveSellerVMWithReviewSummary() {
        assertThat(true).isFalse();
    }

    @Test
    public void getSellerUser_withExistId_receiveSellerVMWithLatest1Review() {
        assertThat(true).isFalse();
    }

    @Test
    public void getSellerUser_withExistId_receiveSellerVMWithCompleteWorkSummary() {
        assertThat(true).isFalse();
    }

    @Test
    public void getSellerUser_withExistId_receiveSellerVMWithCompleteWorkCount() {
        assertThat(true).isFalse();
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

    public <T> ResponseEntity<T> getSellerUser(ParameterizedTypeReference<T> responseType, UUID id) {
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
