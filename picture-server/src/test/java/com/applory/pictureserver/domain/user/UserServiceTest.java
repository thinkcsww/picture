package com.applory.pictureserver.domain.user;

import com.applory.pictureserver.TestConstants;
import com.applory.pictureserver.TestUtil;
import com.applory.pictureserver.config.WithMockClientLogin;
import com.applory.pictureserver.config.WithMockSellerLogin;
import com.applory.pictureserver.domain.matching.Matching;
import com.applory.pictureserver.domain.matching.MatchingRepository;
import com.applory.pictureserver.domain.review.Review;
import com.applory.pictureserver.domain.review.ReviewRepository;
import com.applory.pictureserver.domain.user.querydto.SellerListVM;
import com.applory.pictureserver.exception.BadRequestException;
import com.applory.pictureserver.shared.Constant;
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
        User seller1 = userRepository.save(TestUtil.createSeller());
        User seller2 = TestUtil.createSeller();
        seller2.setUsername("seller1");
        seller2.setNickname("nick1");
        seller2.setSpecialty(Constant.Specialty.PEOPLE.toString());
        userRepository.save(seller2);

        User client = userRepository.save(TestUtil.createClient());
        matchingRepository.save(TestUtil.createMatching(seller1, client, Matching.Status.COMPLETE, Constant.Specialty.BACKGROUND, "Y"));
        matchingRepository.save(TestUtil.createMatching(seller1, client, Matching.Status.COMPLETE, Constant.Specialty.PEOPLE, "Y"));
        matchingRepository.save(TestUtil.createMatching(seller1, client, Matching.Status.COMPLETE, Constant.Specialty.OFFICIAL, "Y"));
        matchingRepository.save(TestUtil.createMatching(seller1, client, Matching.Status.COMPLETE, Constant.Specialty.ETC, "Y"));

        Review review = new Review();
        review.setContent("content");
        review.setSeller(seller1);
        review.setClient(client);
        review.setRate(3);

        Review review2 = new Review();
        review2.setContent("review2");
        review2.setSeller(seller1);
        review2.setClient(client);
        review2.setRate(1);

        Review review3 = new Review();
        review3.setContent("review3");
        review3.setSeller(seller1);
        review3.setClient(client);
        review3.setRate(1);

        Review review4 = new Review();
        review4.setContent("review4");
        review4.setSeller(seller2);
        review4.setClient(client);
        review4.setRate(5);

        reviewRepository.save(review);
        reviewRepository.save(review2);
        reviewRepository.save(review3);
        reviewRepository.save(review4);
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
            error.isInstanceOf(BadRequestException.class);
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

            error.isInstanceOf(BadRequestException.class);
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
            matchingRepository.save(TestUtil.createMatching(seller, client, Matching.Status.REQUEST, Constant.Specialty.ETC, "Y"));

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
            matchingRepository.save(TestUtil.createMatching(seller, client, Matching.Status.REQUEST, Constant.Specialty.ETC, "Y"));

            UserDto.VM userMe = userService.getUserMe();
            assertThat(userMe.getMatchings().get(Matching.Status.REQUEST)).isNotEmpty();
        }
    }

    @DisplayName("Seller 리스트")
    @Nested
    class SellerList {
        @DisplayName("Seller 리스트 조회 - 기본값 조회")
        @Test
        void getSeller_withDefaultOption_success() {
            UserDto.SearchSeller searchSeller = new UserDto.SearchSeller();
            searchSeller.setSpecialty(Constant.Specialty.PEOPLE.toString());
            Page<SellerListVM> sellerUsers = userService.getSellerUsers(searchSeller, PageRequest.of(0, 20, Sort.by("price")));
            assertThat(sellerUsers.getContent().get(0).getRateAvg()).isGreaterThan(0);

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
