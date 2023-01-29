package com.applory.pictureserver.domain.request;

import com.applory.pictureserver.TestConstants;
import com.applory.pictureserver.TestUtil;
import com.applory.pictureserver.config.WithMockClientLogin;
import com.applory.pictureserver.domain.user.User;
import com.applory.pictureserver.domain.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static com.applory.pictureserver.shared.Constant.Specialty.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class RequestServiceTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RequestRepository requestRepository;

    @Autowired
    private RequestService requestService;

    @BeforeEach
    public void setUp() {
        userRepository.save(TestUtil.createSeller(TestConstants.TEST_SELLER_NICKNAME, PEOPLE));
        userRepository.save(TestUtil.createClient());
    }

    @DisplayName("Request 생성 - 성공")
    @WithMockClientLogin
    @Test
    void createRequest_success() {
        RequestDto.Create create = TestUtil.createRequestDto(LocalDateTime.now().plusHours(10), "title", "desc", PEOPLE, 1000);

        RequestDto.VM request = requestService.createRequest(create);

        assertThat(request.getId()).isNotNull();
    }

    @DisplayName("Request 생성 - 일정이 과거일 경우 실패")
    @WithMockClientLogin
    @Test
    void createRequest_whenDueDateIsInvalid_fail() {
        RequestDto.Create create = TestUtil.createRequestDto(LocalDateTime.now().minusHours(10), "title", "desc", PEOPLE, 1000);

        assertThatThrownBy(() -> requestService.createRequest(create))
                .isInstanceOf(IllegalStateException.class);
    }

    @DisplayName("Request 상세 조회")
    @Nested
    class GetReview {
        @DisplayName("Request 상세 조회 - 기본 정보 조회")
        @WithMockClientLogin
        @Test
        void getRequest_withBasicInfo() {
            RequestDto.Create create = TestUtil.createRequestDto(LocalDateTime.now().plusHours(10), "title", "desc", PEOPLE, 1000);
            RequestDto.VM newRequest = requestService.createRequest(create);

            RequestDto.VM requestInDB = requestService.getRequest(newRequest.getId());
            assertThat(requestInDB.getUserNickname()).isEqualTo(TestConstants.TEST_CLIENT_NICKNAME);
            assertThat(requestInDB.getId()).isNotNull();
            assertThat(requestInDB.getUserId()).isNotNull();
            assertThat(requestInDB.getDesiredPrice()).isEqualTo(create.getDesiredPrice());
            assertThat(requestInDB.getTitle()).isEqualTo(create.getTitle());
            assertThat(requestInDB.getDescription()).isEqualTo(create.getDescription());
            assertThat(requestInDB.getSpecialty()).isEqualTo(create.getSpecialty());
            assertThat(requestInDB.getReadCount()).isEqualTo(1);
            assertThat(requestInDB.getDueDate()).isEqualTo(create.getDueDate());
            assertThat(requestInDB.getChatCount()).isNotNull();
        }

        @DisplayName("Request 상세 조회 - 게시자의 또 다른 요청 리스트 함께 조회")
        @WithMockClientLogin
        @Test
        void getRequest_withAnotherRequest() {
            RequestDto.Create create = TestUtil.createRequestDto(LocalDateTime.now().plusHours(10), "title", "desc", PEOPLE, 1000);
            RequestDto.Create create2 = TestUtil.createRequestDto(LocalDateTime.now().plusHours(5), "title2", "desc2", OFFICIAL, 2000);
            RequestDto.Create create3 = TestUtil.createRequestDto(LocalDateTime.now().plusHours(15), "title3", "desc3", BACKGROUND, 3000);
            RequestDto.VM newRequest = requestService.createRequest(create);
            requestService.createRequest(create);
            requestService.createRequest(create2);
            requestService.createRequest(create3);

            RequestDto.VM requestInDB = requestService.getRequest(newRequest.getId());
            assertThat(requestInDB.getAnotherRequests()).isNotEmpty();
        }

        @DisplayName("Request 상세 조회 - 게시자의 또 다른 요청이 없을 경우 랜덤한 Request 리스트를 조회")
        @WithMockClientLogin
        @Test
        void getRequest_withRandomAnotherRequestWhenAnotherReuqestIsEmpty() {
            RequestDto.Create create = TestUtil.createRequestDto(LocalDateTime.now().plusHours(10), "title", "desc", PEOPLE, 1000);
            RequestDto.VM newRequest = requestService.createRequest(create);

            User anotherUser = userRepository.save(TestUtil.createClient());
            Request anotherUsersReqeust = TestUtil.createRequest(anotherUser, LocalDateTime.now().plusHours(10), "title", "desc", PEOPLE, 1000);
            requestRepository.save(anotherUsersReqeust);

            RequestDto.VM requestInDB = requestService.getRequest(newRequest.getId());
            assertThat(requestInDB.getAnotherRequests()).isNotEmpty();
        }

        @DisplayName("Request 상세 조회 - 채택률 조회")
        @WithMockClientLogin
        @Test
        void getRequest_withAcceptRate() {
            RequestDto.Create create = TestUtil.createRequestDto(LocalDateTime.now().plusHours(10), "title", "desc", PEOPLE, 1000);
            RequestDto.VM newRequest = requestService.createRequest(create);

            RequestDto.VM requestInDB = requestService.getRequest(newRequest.getId());
            assertThat(requestInDB.getUserAcceptRate()).isNotEqualTo(0);
        }
    }

    @DisplayName("Request 리스트 조회")
    @Nested
    class GetReviewList {
        @DisplayName("Request 리스트 조회 - 기본 조회, 가장 최근 생성된 순")
        @WithMockClientLogin
        @Test
        void getRequests_success() {
            RequestDto.Create create1 = TestUtil.createRequestDto(LocalDateTime.now().plusHours(1), "title", "desc", PEOPLE, 1000);
            RequestDto.Create create2 = TestUtil.createRequestDto(LocalDateTime.now().plusHours(2), "title", "desc", PEOPLE, 2000);
            RequestDto.Create create3 = TestUtil.createRequestDto(LocalDateTime.now().plusHours(3), "title", "desc", PEOPLE, 3000);
            requestService.createRequest(create1);
            requestService.createRequest(create2);
            requestService.createRequest(create3);


            RequestDto.Search search = new RequestDto.Search();
            search.setSpecialty(PEOPLE);
            Page<RequestDto.VM> requests = requestService.getRequests(search, PageRequest.of(0, 20));
            assertThat(requests.getContent().get(0).getDesiredPrice()).isEqualTo(create3.getDesiredPrice());
        }

        @DisplayName("Request 리스트 조회 - 가격 높은 순")
        @WithMockClientLogin
        @Test
        void getRequests_orderByPrice_success() {
            RequestDto.Create create1 = TestUtil.createRequestDto(LocalDateTime.now().plusHours(1), "title", "desc", PEOPLE, 1000);
            RequestDto.Create create2 = TestUtil.createRequestDto(LocalDateTime.now().plusHours(2), "title", "desc", PEOPLE, 2000);
            RequestDto.Create create3 = TestUtil.createRequestDto(LocalDateTime.now().plusHours(3), "title", "desc", PEOPLE, 3000);
            requestService.createRequest(create1);
            requestService.createRequest(create2);
            requestService.createRequest(create3);


            RequestDto.Search search = new RequestDto.Search();
            search.setSpecialty(PEOPLE);
            Page<RequestDto.VM> requests = requestService.getRequests(search, PageRequest.of(0, 20, Sort.by("price")));
            assertThat(requests.getContent().get(0).getDesiredPrice()).isEqualTo(create3.getDesiredPrice());
        }

        @DisplayName("Request 리스트 조회 - 마감 가까운 순")
        @WithMockClientLogin
        @Test
        void getRequests_orderByDueDate_success() {
            RequestDto.Create create1 = TestUtil.createRequestDto(LocalDateTime.now().plusHours(1), "title", "desc", PEOPLE, 1000);
            RequestDto.Create create2 = TestUtil.createRequestDto(LocalDateTime.now().plusHours(2), "title", "desc", PEOPLE, 2000);
            RequestDto.Create create3 = TestUtil.createRequestDto(LocalDateTime.now().plusHours(3), "title", "desc", PEOPLE, 3000);
            requestService.createRequest(create1);
            requestService.createRequest(create2);
            requestService.createRequest(create3);


            RequestDto.Search search = new RequestDto.Search();
            search.setSpecialty(PEOPLE);
            Page<RequestDto.VM> requests = requestService.getRequests(search, PageRequest.of(0, 20, Sort.by("dueDate")));
            assertThat(requests.getContent().get(0).getDesiredPrice()).isEqualTo(create1.getDesiredPrice());
        }

        @DisplayName("Request 리스트 조회 - 마감 기한 지나지 않은 것들만")
        @WithMockClientLogin
        @Test
        void getRequests_onlyWithDueDateIsNotDone() {
            User client = userRepository.findByUsername(TestConstants.TEST_CLIENT_USERNAME);
            Request create1 = TestUtil.createRequest(client, LocalDateTime.now().minusHours(10), "title", "desc", PEOPLE, 1000);
            Request create2 = TestUtil.createRequest(client, LocalDateTime.now().plusHours(2), "title", "desc", PEOPLE, 2000);
            Request create3 = TestUtil.createRequest(client, LocalDateTime.now().plusHours(3), "title", "desc", PEOPLE, 3000);

            requestRepository.save(create1);
            requestRepository.save(create2);
            requestRepository.save(create3);

            RequestDto.Search search = new RequestDto.Search();
            search.setSpecialty(PEOPLE);
            Page<RequestDto.VM> requests = requestService.getRequests(search, PageRequest.of(0, 20));
            assertThat(requests.getTotalElements()).isEqualTo(2);
        }

        @DisplayName("Request 리스트 조회 - MatchYN이 N인 것들만")
        @WithMockClientLogin
        @Test
        void getRequests_onlyWithMatchYnIsN() {
            User client = userRepository.findByUsername(TestConstants.TEST_CLIENT_USERNAME);
            Request create1 = TestUtil.createRequest(client, LocalDateTime.now().minusHours(10), "title", "desc", PEOPLE, 1000);
            create1.setMatchYN("Y");
            Request create2 = TestUtil.createRequest(client, LocalDateTime.now().plusHours(2), "title", "desc", PEOPLE, 2000);
            Request create3 = TestUtil.createRequest(client, LocalDateTime.now().plusHours(3), "title", "desc", PEOPLE, 3000);

            requestRepository.save(create1);
            requestRepository.save(create2);
            requestRepository.save(create3);


            RequestDto.Search search = new RequestDto.Search();
            search.setSpecialty(PEOPLE);
            Page<RequestDto.VM> requests = requestService.getRequests(search, PageRequest.of(0, 20));
            assertThat(requests.getTotalElements()).isEqualTo(2);
        }

        @DisplayName("Request 리스트 조회 - Specialty로 검색")
        @WithMockClientLogin
        @Test
        void getRequests_findBySpecialty() {
            User client = userRepository.findByUsername(TestConstants.TEST_CLIENT_USERNAME);
            Request create1 = TestUtil.createRequest(client, LocalDateTime.now().minusHours(10), "title", "desc", PEOPLE, 1000);
            create1.setMatchYN("Y");
            Request create2 = TestUtil.createRequest(client, LocalDateTime.now().plusHours(2), "title", "desc", PEOPLE, 2000);
            Request create3 = TestUtil.createRequest(client, LocalDateTime.now().plusHours(3), "official", "desc", OFFICIAL, 3000);

            requestRepository.save(create1);
            requestRepository.save(create2);
            requestRepository.save(create3);


            RequestDto.Search search = new RequestDto.Search();
            search.setSpecialty(OFFICIAL);
            Page<RequestDto.VM> requests = requestService.getRequests(search, PageRequest.of(0, 20));
            assertThat(requests.getContent().get(0).getTitle()).isEqualTo(create3.getTitle());
        }

        @DisplayName("Request 리스트 조회 - 마감일 Range 검색")
        @WithMockClientLogin
        @Test
        void getRequests_findByDueDateRange() {
            User client = userRepository.findByUsername(TestConstants.TEST_CLIENT_USERNAME);
            Request create1 = TestUtil.createRequest(client, LocalDateTime.now().minusHours(10), "searchMe", "desc", PEOPLE, 1000);
            create1.setMatchYN("Y");
            Request create2 = TestUtil.createRequest(client, LocalDateTime.now().plusHours(2), "title", "desc", PEOPLE, 2000);
            Request create3 = TestUtil.createRequest(client, LocalDateTime.now().plusHours(10), "official", "desc", PEOPLE, 3000);

            requestRepository.save(create1);
            requestRepository.save(create2);
            requestRepository.save(create3);


            RequestDto.Search search = new RequestDto.Search();
            search.setSpecialty(PEOPLE);
            search.setFromForDueDt(LocalDateTime.now());
            search.setToForDueDt(LocalDateTime.now().plusHours(3));
            Page<RequestDto.VM> requests = requestService.getRequests(search, PageRequest.of(0, 20));
            assertThat(requests.getContent().get(0).getTitle()).isEqualTo(create2.getTitle());
        }

    }

}
