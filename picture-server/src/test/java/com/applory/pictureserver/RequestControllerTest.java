package com.applory.pictureserver;

import com.applory.pictureserver.domain.error.ApiError;
import com.applory.pictureserver.domain.oauth.AuthDto;
import com.applory.pictureserver.domain.oauth.MyOAuth2Token;
import com.applory.pictureserver.domain.request.RequestDto;
import com.applory.pictureserver.domain.request.RequestRepository;
import com.applory.pictureserver.domain.shared.Constant;
import com.applory.pictureserver.domain.user.User;
import com.applory.pictureserver.domain.user.UserDto;
import com.applory.pictureserver.domain.user.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

import static com.applory.pictureserver.TestConstants.*;
import static com.applory.pictureserver.TestUtil.createValidRequestDto;
import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
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

    @BeforeEach
    public void cleanUp() {
        requestRepository.deleteAll();
        userRepository.deleteAll();
        testRestTemplate.getRestTemplate().getInterceptors().clear();
    }


    @Test
    public void postRequest_withInvalidToken_receiveUnauthorized() {
        authenticate("invalid_token");

        RequestDto.Create dto = createValidRequestDto();
        ResponseEntity<Object> response = postRequest(dto, Object.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    public void postRequest_withValidToken_receiveCreated() {
        signUp(TestUtil.createValidClientUser(TEST_USERNAME), Object.class);

        ResponseEntity<MyOAuth2Token> tokenResponse = login(TestUtil.createValidLoginDto(TEST_USERNAME), MyOAuth2Token.class);
        authenticate(tokenResponse.getBody().getAccess_token());

        RequestDto.Create dto = createValidRequestDto();
        ResponseEntity<Object> response = postRequest(dto, Object.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    }

    @Test
    public void postRequest_withInvalidDto_receiveBadRequest() {
        signUp(TestUtil.createValidClientUser(TEST_USERNAME), Object.class);

        ResponseEntity<MyOAuth2Token> tokenResponse = login(TestUtil.createValidLoginDto(TEST_USERNAME), MyOAuth2Token.class);
        authenticate(tokenResponse.getBody().getAccess_token());

        RequestDto.Create dto = createValidRequestDto();
        dto.setTitle(null);
        ResponseEntity<Object> response = postRequest(dto, Object.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    public void postRequest_withInvalidDto_receiveApiErrorWithValidationErrors() {
        signUp(TestUtil.createValidClientUser(TEST_USERNAME), Object.class);

        ResponseEntity<MyOAuth2Token> tokenResponse = login(TestUtil.createValidLoginDto(TEST_USERNAME), MyOAuth2Token.class);
        authenticate(tokenResponse.getBody().getAccess_token());

        RequestDto.Create dto = createValidRequestDto();
        dto.setTitle(null);
        ResponseEntity<ApiError> response = postRequest(dto, ApiError.class);

        assertThat(response.getBody().getValidationErrors().containsKey("title")).isTrue();
    }

    @Test
    public void postRequest_withValidDto_receiveRequestVM() {
        ResponseEntity<User> userResponse = signUp(TestUtil.createValidClientUser(TEST_USERNAME), User.class);

        ResponseEntity<MyOAuth2Token> tokenResponse = login(TestUtil.createValidLoginDto(TEST_USERNAME), MyOAuth2Token.class);
        authenticate(tokenResponse.getBody().getAccess_token());

        RequestDto.Create dto = createValidRequestDto();
        ResponseEntity<RequestDto.VM> response = postRequest(dto, RequestDto.VM.class);

        assertThat(response.getBody().getUserId()).isEqualTo(userResponse.getBody().getId());
        assertThat(response.getBody().getUserNickname()).isEqualTo(userResponse.getBody().getNickname());
        assertThat(response.getBody().getReadCount()).isEqualTo(0);
        assertThat(response.getBody().getTitle()).isEqualTo("제목입니다");
    }


    @Test
    public void getRequests_withValidToken_receiveOk() {
        signUp(TestUtil.createValidClientUser(TEST_USERNAME), Object.class);

        ResponseEntity<MyOAuth2Token> tokenResponse = login(TestUtil.createValidLoginDto(TEST_USERNAME), MyOAuth2Token.class);
        authenticate(tokenResponse.getBody().getAccess_token());

        postRequest(createValidRequestDto(), RequestDto.VM.class);

        ResponseEntity<Object> response = getRequests(null, new ParameterizedTypeReference<Object>() {});

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    public void getRequests_withValidToken_receivePage() {
        signUp(TestUtil.createValidClientUser(TEST_USERNAME), Object.class);

        ResponseEntity<MyOAuth2Token> tokenResponse = login(TestUtil.createValidLoginDto(TEST_USERNAME), MyOAuth2Token.class);
        authenticate(tokenResponse.getBody().getAccess_token());

        postRequest(createValidRequestDto(), RequestDto.VM.class);

        ResponseEntity<TestPage<RequestDto.VM>> response = getRequests(null, new ParameterizedTypeReference<TestPage<RequestDto.VM>>() {
        });

        assertThat(response.getBody().getContent().get(0).getTitle()).isEqualTo("제목입니다");
    }

    @Test
    public void getRequests_withValidToken_receivePageOrderByDueDateAsc() {
        signUp(TestUtil.createValidClientUser(TEST_USERNAME), Object.class);

        ResponseEntity<MyOAuth2Token> tokenResponse = login(TestUtil.createValidLoginDto(TEST_USERNAME), MyOAuth2Token.class);
        authenticate(tokenResponse.getBody().getAccess_token());

        RequestDto.Create dto1 = createValidRequestDto();
        dto1.setDesiredPrice(10000);
        postRequest(dto1, RequestDto.VM.class);

        RequestDto.Create dto2 = createValidRequestDto();
        postRequest(dto2, RequestDto.VM.class);

        ResponseEntity<TestPage<RequestDto.VM>> response = getRequests(null, new ParameterizedTypeReference<TestPage<RequestDto.VM>>() {}, "&sort=desiredPrice,desc");

        assertThat(response.getBody().getContent().get(0).getDesiredPrice()).isEqualTo(10000);
    }

    @Test
    public void getRequests_withValidToken_receivePageOrderByPriceDesc() {
        signUp(TestUtil.createValidClientUser(TEST_USERNAME), Object.class);

        ResponseEntity<MyOAuth2Token> tokenResponse = login(TestUtil.createValidLoginDto(TEST_USERNAME), MyOAuth2Token.class);
        authenticate(tokenResponse.getBody().getAccess_token());

        RequestDto.Create dto1 = createValidRequestDto();
        dto1.setDueDate(LocalDateTime.of(2025, 12, 31, 12, 12));
        postRequest(dto1, RequestDto.VM.class);

        RequestDto.Create dto2 = createValidRequestDto();
        postRequest(dto2, RequestDto.VM.class);

        ResponseEntity<TestPage<RequestDto.VM>> response = getRequests(null, new ParameterizedTypeReference<TestPage<RequestDto.VM>>() {}, "&sort=dueDate,asc");

        assertThat(response.getBody().getContent().get(0).getDueDate()).isBefore(response.getBody().getContent().get(1).getDueDate());
    }


    @Test
    void getRequests_withValidToken_receiveOnlyRequestsDueDateIsNotOver() {
        signUp(TestUtil.createValidClientUser(TEST_USERNAME), Object.class);

        ResponseEntity<MyOAuth2Token> tokenResponse = login(TestUtil.createValidLoginDto(TEST_USERNAME), MyOAuth2Token.class);
        authenticate(tokenResponse.getBody().getAccess_token());

        RequestDto.Create dto1 = createValidRequestDto();
        dto1.setDueDate(LocalDateTime.of(2020, 12, 31, 12, 12));
        postRequest(dto1, RequestDto.VM.class);

        RequestDto.Create dto2 = createValidRequestDto();
        postRequest(dto2, RequestDto.VM.class);

        ResponseEntity<TestPage<RequestDto.VM>> response = getRequests(null, new ParameterizedTypeReference<TestPage<RequestDto.VM>>() {
        });

        assertThat(response.getBody().getTotalElements()).isEqualTo(1);
    }

    @Test
    void getRequests_searchByRequestType_receiveResultByRequestType() {
        signUp(TestUtil.createValidClientUser(TEST_USERNAME), Object.class);

        ResponseEntity<MyOAuth2Token> tokenResponse = login(TestUtil.createValidLoginDto(TEST_USERNAME), MyOAuth2Token.class);
        authenticate(tokenResponse.getBody().getAccess_token());

        postRequest(createValidRequestDto(), RequestDto.VM.class);

        RequestDto.Search search = new RequestDto.Search();
        search.setSpecialty(Constant.Specialty.BACKGROUND);

        ResponseEntity<TestPage<RequestDto.VM>> response = getRequests(null, new ParameterizedTypeReference<TestPage<RequestDto.VM>>() {
        });

        assertThat(response.getBody().getContent().get(0).getRequestType()).isEqualTo(Constant.Specialty.BACKGROUND);
    }

    @Test
    void getRequests_searchByDueDate_receiveResultByDueDate() {
        signUp(TestUtil.createValidClientUser(TEST_USERNAME), Object.class);

        ResponseEntity<MyOAuth2Token> tokenResponse = login(TestUtil.createValidLoginDto(TEST_USERNAME), MyOAuth2Token.class);
        authenticate(tokenResponse.getBody().getAccess_token());

        RequestDto.Create dto1 = createValidRequestDto();
        dto1.setDueDate(LocalDateTime.of(2022, 12, 31, 12, 12));
        postRequest(dto1, RequestDto.VM.class);

        RequestDto.Create dto2 = createValidRequestDto();
        postRequest(dto2, RequestDto.VM.class);

        RequestDto.Search search = new RequestDto.Search();
        search.setFromForDueDt(LocalDateTime.of(2022, 12, 31, 12, 10));
        search.setToForDueDt(LocalDateTime.of(2022, 12, 31, 12, 15));

        ResponseEntity<TestPage<RequestDto.VM>> response = getRequests(search, new ParameterizedTypeReference<TestPage<RequestDto.VM>>() {
        });

        assertThat(response.getBody().getContent().get(0).getDueDate())
                .isBetween(
                        LocalDateTime.of(2022, 12, 31, 12, 10),
                        LocalDateTime.of(2022, 12, 31, 12, 15)
                );
    }

    @Test
    void getRequest_withInValidToken_receive401() {
        authenticate("invalid_token");

        ResponseEntity<Object> response = getRequest(UUID.randomUUID(), new ParameterizedTypeReference<Object>() {});

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    void getRequest_withValidTokenButNotExistId_receive404() {
        signUp(TestUtil.createValidClientUser(TEST_USERNAME), Object.class);

        ResponseEntity<MyOAuth2Token> tokenResponse = login(TestUtil.createValidLoginDto(TEST_USERNAME), MyOAuth2Token.class);
        authenticate(tokenResponse.getBody().getAccess_token());

        postRequest(createValidRequestDto(), Object.class);
        ResponseEntity<Object> response = getRequest(UUID.randomUUID(), new ParameterizedTypeReference<Object>() {});

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void getRequest_withValid_receive200() {
        signUp(TestUtil.createValidClientUser(TEST_USERNAME), Object.class);

        ResponseEntity<MyOAuth2Token> tokenResponse = login(TestUtil.createValidLoginDto(TEST_USERNAME), MyOAuth2Token.class);
        authenticate(tokenResponse.getBody().getAccess_token());

        ResponseEntity<RequestDto.VM> requestResponse = postRequest(createValidRequestDto(), RequestDto.VM.class);

        ResponseEntity<Object> response = getRequest(requestResponse.getBody().getId(), new ParameterizedTypeReference<Object>() {});

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    void getRequest_withValid_receiveVmWithBasicInfo() {
        signUp(TestUtil.createValidClientUser(TEST_USERNAME), Object.class);

        ResponseEntity<MyOAuth2Token> tokenResponse = login(TestUtil.createValidLoginDto(TEST_USERNAME), MyOAuth2Token.class);
        authenticate(tokenResponse.getBody().getAccess_token());

        ResponseEntity<RequestDto.VM> requestResponse = postRequest(createValidRequestDto(), RequestDto.VM.class);

        ResponseEntity<RequestDto.VM> response = getRequest(requestResponse.getBody().getId(), new ParameterizedTypeReference<RequestDto.VM>() {});

        assertThat(response.getBody().getUserNickname()).isEqualTo("test-nickname");
        assertThat(response.getBody().getDesiredPrice()).isEqualTo(2000);
        assertThat(response.getBody().getTitle()).isEqualTo("제목입니다");
        assertThat(response.getBody().getDescription()).isEqualTo("설명입니다");
        assertThat(response.getBody().getRequestType()).isEqualTo(Constant.Specialty.BACKGROUND);
        assertThat(response.getBody().getReadCount()).isEqualTo(0);
        assertThat(response.getBody().getDueDate()).isEqualTo(LocalDateTime.of(2022, 12, 25, 23, 59));

    }

    @Test
    void getRequest_withValid_receiveVmWithChatCount() {
        signUp(TestUtil.createValidClientUser(TEST_USERNAME), Object.class);

        ResponseEntity<MyOAuth2Token> tokenResponse = login(TestUtil.createValidLoginDto(TEST_USERNAME), MyOAuth2Token.class);
        authenticate(tokenResponse.getBody().getAccess_token());

        postRequest(createValidRequestDto(), RequestDto.VM.class);

        ResponseEntity<RequestDto.VM> response = getRequest(UUID.randomUUID(), new ParameterizedTypeReference<RequestDto.VM>() {});

        assertThat(response.getBody().getChatCount()).isNotNull();
    }

    @Test
    void getRequest_withValid_receiveVmWithUsersAnotherRequests() {
        signUp(TestUtil.createValidClientUser(TEST_USERNAME), Object.class);

        ResponseEntity<MyOAuth2Token> tokenResponse = login(TestUtil.createValidLoginDto(TEST_USERNAME), MyOAuth2Token.class);
        authenticate(tokenResponse.getBody().getAccess_token());
        RequestDto.Create dto1 = createValidRequestDto();
        RequestDto.Create dto2 = createValidRequestDto();
        dto1.setTitle("1");
        dto2.setTitle("2");
        postRequest(dto1, RequestDto.VM.class);
        postRequest(dto2, RequestDto.VM.class);
        ResponseEntity<RequestDto.VM> requestResponse = postRequest(createValidRequestDto(), RequestDto.VM.class);

        ResponseEntity<RequestDto.VM> response = getRequest(requestResponse.getBody().getId(), new ParameterizedTypeReference<RequestDto.VM>() {});

        assertThat(response.getBody().getAnotherRequests()).isNotNull();
    }

    @Test
    void getRequest_withCompleteRequest_receiveVmWithAcceptRate() {
        signUp(TestUtil.createValidClientUser(TEST_USERNAME), Object.class);

        ResponseEntity<MyOAuth2Token> tokenResponse = login(TestUtil.createValidLoginDto(TEST_USERNAME), MyOAuth2Token.class);
        authenticate(tokenResponse.getBody().getAccess_token());

        RequestDto.Create dto1 = createValidRequestDto();
        dto1.setDueDate(LocalDateTime.of(2022,6,1,12,12));
        dto1.setMatchYn("Y");
        dto1.setCompleteYn("Y");

        RequestDto.Create dto2 = createValidRequestDto();
        dto2.setDueDate(LocalDateTime.of(2022,6,1,12,12));
        dto2.setMatchYn("Y");
        dto2.setCompleteYn("N");

        RequestDto.Create dto3 = createValidRequestDto();
        dto3.setDueDate(LocalDateTime.of(2022,6,1,12,12));
        dto3.setMatchYn("Y");
        dto3.setCompleteYn("N");

        RequestDto.Create dto4 = createValidRequestDto();
        dto4.setDueDate(LocalDateTime.of(2022,6,1,12,12));
        dto4.setMatchYn("Y");
        dto4.setCompleteYn("N");

        RequestDto.Create dto5 = createValidRequestDto();
        dto5.setDueDate(LocalDateTime.of(2022,6,1,12,12));
        dto5.setMatchYn("Y");
        dto5.setCompleteYn("N");

        postRequest(dto1, RequestDto.VM.class);
        postRequest(dto2, RequestDto.VM.class);
        postRequest(dto3, RequestDto.VM.class);
        postRequest(dto4, RequestDto.VM.class);
        ResponseEntity<RequestDto.VM> requestResponse = postRequest(dto5, RequestDto.VM.class);

        ResponseEntity<RequestDto.VM> response = getRequest(requestResponse.getBody().getId(), new ParameterizedTypeReference<RequestDto.VM>() {});

        assertThat(response.getBody().getUserAcceptRate()).isEqualTo(20.0);
    }

    @Test
    void getRequest_withoutCompleteRequest_receiveVmWithAcceptRate() {
        signUp(TestUtil.createValidClientUser(TEST_USERNAME), Object.class);

        ResponseEntity<MyOAuth2Token> tokenResponse = login(TestUtil.createValidLoginDto(TEST_USERNAME), MyOAuth2Token.class);
        authenticate(tokenResponse.getBody().getAccess_token());

        RequestDto.Create dto1 = createValidRequestDto();
        dto1.setDueDate(LocalDateTime.of(2022,6,1,12,12));
        dto1.setMatchYn("Y");
        dto1.setCompleteYn("N");

        RequestDto.Create dto2 = createValidRequestDto();
        dto2.setDueDate(LocalDateTime.of(2022,6,1,12,12));
        dto2.setMatchYn("Y");
        dto2.setCompleteYn("N");

        postRequest(dto1, RequestDto.VM.class);
        ResponseEntity<RequestDto.VM> requestResponse = postRequest(dto2, RequestDto.VM.class);

        ResponseEntity<RequestDto.VM> response = getRequest(requestResponse.getBody().getId(), new ParameterizedTypeReference<RequestDto.VM>() {});

        assertThat(response.getBody().getUserAcceptRate()).isEqualTo(-1.0);
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


}
