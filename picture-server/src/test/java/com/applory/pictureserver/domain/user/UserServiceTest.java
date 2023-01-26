package com.applory.pictureserver.domain.user;

import com.applory.pictureserver.TestConstants;
import com.applory.pictureserver.TestUtil;
import com.applory.pictureserver.config.WithMockClientLogin;
import com.applory.pictureserver.config.WithMockSellerLogin;
import com.applory.pictureserver.domain.matching.Matching;
import com.applory.pictureserver.domain.matching.MatchingRepository;
import com.applory.pictureserver.domain.review.ReviewRepository;
import com.applory.pictureserver.domain.user.querydto.SellerListVM;
import org.assertj.core.api.AbstractThrowableAssert;
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

import static com.applory.pictureserver.shared.Constant.Specialty.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class UserServiceTest {
    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MatchingRepository matchingRepository;

    @Autowired
    private ReviewRepository reviewRepository;

    @BeforeEach
    public void setUp() {
        User seller1 = userRepository.save(TestUtil.createSeller(TestConstants.TEST_SELLER_NICKNAME, PEOPLE));
        User seller2 = TestUtil.createSeller("nick2", PEOPLE);
        seller2.setUsername("seller2");
        seller2.setSpecialty(PEOPLE.toString());
        seller2.setPeoplePrice(1000000);
        userRepository.save(seller2);

        User client = userRepository.save(TestUtil.createClient());
        matchingRepository.save(TestUtil.createMatching(seller1, client, Matching.Status.COMPLETE, BACKGROUND, "Y"));
        matchingRepository.save(TestUtil.createMatching(seller1, client, Matching.Status.COMPLETE, PEOPLE, "Y"));
        matchingRepository.save(TestUtil.createMatching(seller1, client, Matching.Status.COMPLETE, OFFICIAL, "Y"));
        matchingRepository.save(TestUtil.createMatching(seller1, client, Matching.Status.COMPLETE, ETC, "Y"));
        matchingRepository.save(TestUtil.createMatching(seller2, client, Matching.Status.COMPLETE, ETC, "Y"));

        reviewRepository.save(TestUtil.createReview(seller1, client, 3));
        reviewRepository.save(TestUtil.createReview(seller1, client, 1));
        reviewRepository.save(TestUtil.createReview(seller1, client, 1));
        reviewRepository.save(TestUtil.createReview(seller2, client, 5));

    }

    @DisplayName("유저 생성")
    @Nested
    class CreateUser {
        @DisplayName("Client 생성 성공")
        @Test
        public void createClient_success() {
            UserDto.Create createDTO = TestUtil.createValidClientUser("username1");
            userService.createUser(createDTO);
        }

        @DisplayName("Seller 생성 성공")
        @Test
        public void createSeller_success() {
            UserDto.Create createDTO = TestUtil.createValidSellerUser("username1");
            userService.createUser(createDTO);
        }

        @DisplayName("Seller 생성 실패 - fromDt가 toDt보다 클경우")
        @Test
        public void createSeller_fail() {
            UserDto.Create createDTO = TestUtil.createValidSellerUser("username1");
            createDTO.setWorkHourFromDt(20000);

            AbstractThrowableAssert<?, ? extends Throwable> error = assertThatThrownBy(() -> userService.createUser(createDTO));
            error.isInstanceOf(IllegalStateException.class);
            error.hasMessageContaining("fromDt is bigger than toDt");

        }

        @DisplayName("닉네임 중복체크 성공")
        @Test
        public void checkNicknameDuplicate_success() {
            userService.checkNickname("fresh nickname");
        }

        @DisplayName("닉네임 중복체크 실패 - 400에러 발생 및 적절한 에러 메세지")
        @Test
        public void checkNicknameDuplicate_fail_get400() {

            AbstractThrowableAssert<?, ? extends Throwable> error = assertThatThrownBy(() -> userService.checkNickname(TestConstants.TEST_SELLER_NICKNAME));

            error.isInstanceOf(IllegalStateException.class);
            error.hasMessageContaining("is already in use");

        }
    }

    @DisplayName("User Me")
    @Nested
    class UserMe {
        @DisplayName("Seller User Me 조회 - 기본 정보 조회")
        @WithMockSellerLogin
        @Test
        public void getUserMe_sellerInfo() {
            UserDto.VM userMe = userService.getUserMe();

            assertThat(userMe.getUsername()).isEqualTo(TestConstants.TEST_SELLER_USERNAME);
            assertThat(userMe.getSpecialty()).isNotNull();
        }

        @DisplayName("Seller User Me 조회 - 매칭 정보 함께 조회")
        @WithMockSellerLogin
        @Test
        public void getUserMe_sellerInfoWithMatching() {
            User seller = userRepository.findByUsername(TestConstants.TEST_SELLER_USERNAME);
            User client = userRepository.findByUsername(TestConstants.TEST_CLIENT_USERNAME);
            matchingRepository.save(TestUtil.createMatching(seller, client, Matching.Status.REQUEST, ETC, "Y"));

            UserDto.VM userMe = userService.getUserMe();

            assertThat(userMe.getUsername()).isEqualTo(TestConstants.TEST_SELLER_USERNAME);
            assertThat(userMe.getSpecialty()).isNotNull();
            assertThat(userMe.getMatchings().get(Matching.Status.REQUEST)).isNotEmpty();
        }

        @DisplayName("Client User Me 조회 - 기본 정보 조회")
        @WithMockClientLogin
        @Test
        public void getUserMe_clientInfo() {
            UserDto.VM userMe = userService.getUserMe();

            assertThat(userMe.getUsername()).isEqualTo(TestConstants.TEST_CLIENT_USERNAME);
            assertThat(userMe.getSpecialty()).isNull();
        }

        @DisplayName("Client User Me 조회 - 매칭 정보 함께 조회")
        @WithMockClientLogin
        @Test
        public void getUserMe_clientInfoWithMatching() {
            User seller = userRepository.findByUsername(TestConstants.TEST_SELLER_USERNAME);
            User client = userRepository.findByUsername(TestConstants.TEST_CLIENT_USERNAME);
            matchingRepository.save(TestUtil.createMatching(seller, client, Matching.Status.REQUEST, ETC, "Y"));

            UserDto.VM userMe = userService.getUserMe();
            assertThat(userMe.getMatchings().get(Matching.Status.REQUEST)).isNotEmpty();
        }
    }

    @DisplayName("Seller 리스트")
    @Nested
    class SellerList {
        @DisplayName("Seller 리스트 조회 - 기본 조회, 최신 회원가입순")
        @Test
        void getSeller_withDefaultOption_success() {
            UserDto.SearchSeller searchSeller = new UserDto.SearchSeller();
            Page<SellerListVM> sellerUsers = userService.getSellerUsers(searchSeller, PageRequest.of(0, 20));

            assertThat(sellerUsers.getContent().get(0).getRateAvg()).isGreaterThan(0);
            assertThat(sellerUsers.getContent().get(0).getReviewCnt()).isGreaterThan(0);
            assertThat(sellerUsers.getContent().get(0).getId()).isNotNull();
            assertThat(sellerUsers.getContent().get(0).getNickname()).isNotNull();
            assertThat(sellerUsers.getContent().get(0).getDescription()).isNotNull();
            assertThat(sellerUsers.getContent().get(1).getCompleteMatchingCnt()).isGreaterThan(0);
        }

        @DisplayName("Seller 리스트 조회 - 리뷰 많은 순")
        @Test
        void getSeller_sortByReviewCnt_success() {
            UserDto.SearchSeller searchSeller = new UserDto.SearchSeller();
            Page<SellerListVM> sellerUsers = userService.getSellerUsers(searchSeller, PageRequest.of(0, 20, Sort.by("review")));

            assertThat(sellerUsers.getContent().get(0).getNickname()).isEqualTo(TestConstants.TEST_SELLER_NICKNAME);
            assertThat(sellerUsers.getContent().get(1).getNickname()).isEqualTo("nick2");
        }

        @DisplayName("Seller 리스트 조회 - 평점 높은 순")
        @Test
        void getSeller_sortByRating_success() {
            UserDto.SearchSeller searchSeller = new UserDto.SearchSeller();
            Page<SellerListVM> sellerUsers = userService.getSellerUsers(searchSeller, PageRequest.of(0, 20, Sort.by("rating")));

            assertThat(sellerUsers.getContent().get(0).getNickname()).isEqualTo("nick2");
            assertThat(sellerUsers.getContent().get(1).getNickname()).isEqualTo(TestConstants.TEST_SELLER_NICKNAME);
        }

        @DisplayName("Seller 리스트 조회 - 가격 낮은 순")
        @Test
        void getSeller_sortByPrice_success() {
            UserDto.SearchSeller searchSeller = new UserDto.SearchSeller();
            searchSeller.setSpecialty(PEOPLE);
            Page<SellerListVM> sellerUsers = userService.getSellerUsers(searchSeller, PageRequest.of(0, 20, Sort.by("price")));

            assertThat(sellerUsers.getContent().get(0).getNickname()).isEqualTo(TestConstants.TEST_SELLER_NICKNAME);
            assertThat(sellerUsers.getContent().get(1).getNickname()).isEqualTo("nick2");
        }

        @DisplayName("Seller 리스트 조회 - 완료 작업 많은 순")
        @Test
        void getSeller_sortByCompleteMatchingCnt_success() {
            UserDto.SearchSeller searchSeller = new UserDto.SearchSeller();
            Page<SellerListVM> sellerUsers = userService.getSellerUsers(searchSeller, PageRequest.of(0, 20, Sort.by("matching")));

            assertThat(sellerUsers.getContent().get(0).getNickname()).isEqualTo(TestConstants.TEST_SELLER_NICKNAME);
            assertThat(sellerUsers.getContent().get(1).getNickname()).isEqualTo("nick2");
        }

        @DisplayName("Seller 리스트 조회 - 닉네임으로 검색")
        @Test
        void getSeller_searchByNickname_success() {
            UserDto.SearchSeller searchSeller = new UserDto.SearchSeller();
            searchSeller.setNickname("nick3");
            Page<SellerListVM> sellerUsers = userService.getSellerUsers(searchSeller, PageRequest.of(0, 20));

            for (SellerListVM sellerListVM: sellerUsers.getContent()) {
                assertThat(sellerListVM.getNickname()).contains("nick3");
            }
        }

        @DisplayName("Seller 리스트 조회 - Specialty로 검색")
        @Test
        void getSeller_searchByPeopleSpecialty_success() {
            userRepository.save(TestUtil.createSeller("nick3", PEOPLE));
            userRepository.save(TestUtil.createSeller("nick4", BACKGROUND));
            userRepository.save(TestUtil.createSeller("nick5", BACKGROUND));
            userRepository.save(TestUtil.createSeller("nick6", OFFICIAL));

            UserDto.SearchSeller searchSeller = new UserDto.SearchSeller();
            searchSeller.setSpecialty(OFFICIAL);
            Page<SellerListVM> sellerUsers = userService.getSellerUsers(searchSeller, PageRequest.of(0, 20));
            assertThat(sellerUsers.getContent().size()).isEqualTo(1);
        }
    }

    @DisplayName("Seller 상세")
    @Nested
    class SellerDetail {
        @DisplayName("Seller 상세 조회 성공 - 최신 리뷰 함께 조회")
        @Test
        public void getSeller_withLatestReview_success() {
            User sellerInDB = userRepository.findByUsername(TestConstants.TEST_SELLER_USERNAME);
            UserDto.SellerVM sellerUser = userService.getSellerDetail(sellerInDB.getId());

            assertThat(sellerUser.getLatestReview()).isNotNull();
        }

        @DisplayName("Seller 상세 조회 성공 - 비밀번호는 빼고 조회")
        @Test
        public void getSeller_withoutPassword_success() {
            User sellerInDB = userRepository.findByUsername(TestConstants.TEST_SELLER_USERNAME);
            UserDto.SellerVM sellerUser = userService.getSellerDetail(sellerInDB.getId());

            assertThat(sellerUser.toString()).doesNotContain("password");
        }

        @DisplayName("Seller 상세 조회 성공 - 리뷰 카운트 함께 조회")
        @Test
        public void getSeller_withReviewCnt_success() {
            User sellerInDB = userRepository.findByUsername(TestConstants.TEST_SELLER_USERNAME);
            UserDto.SellerVM sellerUser = userService.getSellerDetail(sellerInDB.getId());

            assertThat(sellerUser.getReviewCount()).isGreaterThan(0);
        }

        @DisplayName("Seller 상세 조회 성공 - 평점 함께 조회")
        @Test
        public void getSeller_withRating_success() {
            User sellerInDB = userRepository.findByUsername(TestConstants.TEST_SELLER_USERNAME);
            UserDto.SellerVM sellerUser = userService.getSellerDetail(sellerInDB.getId());

            assertThat(sellerUser.getRating()).isGreaterThan(0);
        }

        @DisplayName("Seller 상세 조회 성공 - 작업 타입별 완료수 조회")
        @Test
        public void getSeller_withMatchingCountBySpecialty_success() {
            User sellerInDB = userRepository.findByUsername(TestConstants.TEST_SELLER_USERNAME);
            UserDto.SellerVM sellerUser = userService.getSellerDetail(sellerInDB.getId());

            assertThat(sellerUser.getMatchingCountBySpecialty()).isNotNull();
        }
    }

}
