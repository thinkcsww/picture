package com.applory.pictureserver.domain.request;

import com.applory.pictureserver.RestTemplateInterceptor;
import com.applory.pictureserver.TestPage;
import com.applory.pictureserver.TestUtil;
import com.applory.pictureserver.domain.oauth.AuthDto;
import com.applory.pictureserver.domain.oauth.MyOAuth2Token;
import com.applory.pictureserver.domain.user.UserDto;
import com.applory.pictureserver.domain.user.UserRepository;
import com.applory.pictureserver.error.ApiError;
import com.applory.pictureserver.shared.Constant;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

import static com.applory.pictureserver.TestConstants.*;
import static com.applory.pictureserver.TestUtil.createValidRequestDto;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class RequestControllerTest {

    @Autowired
    private TestRestTemplate testRestTemplate;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RequestRepository requestRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @AfterEach
    public void cleanUp() {
        requestRepository.deleteAll();
        userRepository.deleteAll();
        logout();
    }

    @DisplayName("Request 생성 - 유효하지 않은 토큰 사용시 401")
    @Test
    public void postRequest_withInvalidToken_receive401() {
        authenticate("invalid_token");

        RequestDto.Create dto = createValidRequestDto();
        ResponseEntity<Object> response = postRequest(dto, Object.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @DisplayName("Request 생성 - 성공시 201")
    @Test
    public void postRequest_withValidToken_receive201() {
        signUp(TestUtil.createValidClientUser(TEST_SELLER_USERNAME), Object.class);

        ResponseEntity<MyOAuth2Token> tokenResponse = login(TestUtil.createValidLoginDto(TEST_SELLER_USERNAME), MyOAuth2Token.class);
        authenticate(tokenResponse.getBody().getAccess_token());

        RequestDto.Create dto = createValidRequestDto();
        ResponseEntity<Object> response = postRequest(dto, Object.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    }

    @DisplayName("Request 생성 - 유효성 체크 실패시 400")
    @Test
    public void postRequest_withInvalidDto_receiveBadRequest() {
        signUp(TestUtil.createValidClientUser(TEST_SELLER_USERNAME), Object.class);

        ResponseEntity<MyOAuth2Token> tokenResponse = login(TestUtil.createValidLoginDto(TEST_SELLER_USERNAME), MyOAuth2Token.class);
        authenticate(tokenResponse.getBody().getAccess_token());

        RequestDto.Create dto = createValidRequestDto();
        dto.setTitle(null);
        ResponseEntity<Object> response = postRequest(dto, Object.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @DisplayName("Request 생성 - 유효성 체크 실패시 에러 메세지")
    @Test
    public void postRequest_withInvalidDto_receiveApiErrorWithValidationErrors() {
        signUp(TestUtil.createValidClientUser(TEST_SELLER_USERNAME), Object.class);

        ResponseEntity<MyOAuth2Token> tokenResponse = login(TestUtil.createValidLoginDto(TEST_SELLER_USERNAME), MyOAuth2Token.class);
        authenticate(tokenResponse.getBody().getAccess_token());

        RequestDto.Create dto = createValidRequestDto();
        dto.setTitle(null);
        ResponseEntity<ApiError> response = postRequest(dto, ApiError.class);

        assertThat(response.getBody().getValidationErrors().containsKey("title")).isTrue();
    }

    @Test
    void getRequest_withInValidToken_receive401() {
        authenticate("invalid_token");

        ResponseEntity<Object> response = getRequest(UUID.randomUUID(), new ParameterizedTypeReference<Object>() {});

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @DisplayName("Request 상세 조회 - 존재하지 않는 id로 조회시 404")
    @Test
    void getRequest_withValidTokenButNotExistId_receive404() {
        signUp(TestUtil.createValidClientUser(TEST_SELLER_USERNAME), Object.class);

        ResponseEntity<MyOAuth2Token> tokenResponse = login(TestUtil.createValidLoginDto(TEST_SELLER_USERNAME), MyOAuth2Token.class);
        authenticate(tokenResponse.getBody().getAccess_token());

        postRequest(createValidRequestDto(), Object.class);
        ResponseEntity<Object> response = getRequest(UUID.randomUUID(), new ParameterizedTypeReference<Object>() {});

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    public <T> ResponseEntity<T> postRequest(RequestDto.Create dto, Class<T> responseType) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", MediaType.APPLICATION_JSON_VALUE);

        HttpEntity<RequestDto.Create> httpEntity = new HttpEntity<>(dto, headers);

        return testRestTemplate.exchange(API_V_1_REQUESTS, HttpMethod.POST, httpEntity, responseType);
    }

    public <T> ResponseEntity<T> getRequests(RequestDto.Search search, ParameterizedTypeReference<T> responseType) {

        String url = createUrlWithRequestParams(search);
        if (url.contains("?")) {
            url += "&page=0&size=20";
        } else {
            url += "?page=0&size=20";
        }

        return testRestTemplate.exchange(url, HttpMethod.GET, null, responseType);
    }

    public <T> ResponseEntity<T> getRequests(RequestDto.Search search, ParameterizedTypeReference<T> responseType, String sortString) {

        String url = createUrlWithRequestParams(search);
        if (url.contains("?")) {
            url += "&page=0&size=20";
        } else {
            url += "?page=0&size=20";
        }

        url += sortString;

        return testRestTemplate.exchange(url, HttpMethod.GET, null, responseType);
    }

    public <T> ResponseEntity<T> getRequest(UUID id, ParameterizedTypeReference<T> responseType) {
        String url = API_V_1_REQUESTS + "/" + id;
        return testRestTemplate.exchange(url, HttpMethod.GET, null, responseType);
    }

    private String createUrlWithRequestParams(RequestDto.Search search) {
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl("http://localhost:8080" + API_V_1_REQUESTS);
        if (search != null) {
            MultiValueMap params = new LinkedMultiValueMap<>();
            params.setAll(objectMapper.convertValue(search, Map.class));
            builder.queryParams(params);
        }

        return builder.toUriString().split("8080")[1];
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

    private void authenticate(String token) {
        testRestTemplate.getRestTemplate().getInterceptors().add(new RestTemplateInterceptor(token));
    }

    private void logout() {
        testRestTemplate.getRestTemplate().getInterceptors().clear();
    }

}
