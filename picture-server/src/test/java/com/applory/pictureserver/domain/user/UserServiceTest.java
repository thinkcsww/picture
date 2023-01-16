package com.applory.pictureserver.domain.user;

import com.applory.pictureserver.TestConstants;
import com.applory.pictureserver.TestUtil;
import com.applory.pictureserver.config.WithMockClientLogin;
import com.applory.pictureserver.config.WithMockSellerLogin;
import com.applory.pictureserver.domain.matching.Matching;
import com.applory.pictureserver.domain.matching.MatchingRepository;
import com.applory.pictureserver.domain.review.Review;
import com.applory.pictureserver.domain.review.ReviewRepository;
import com.applory.pictureserver.exception.BadRequestException;
import org.assertj.core.api.AbstractThrowableAssert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
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
        User seller = userRepository.save(TestUtil.createSeller());
        User client = userRepository.save(TestUtil.createClient());
        Matching matching = matchingRepository.save(TestUtil.createMatching(seller, client, Matching.Status.REQUEST));
        Review review = new Review();
        review.setContent("content");
        review.setSeller(seller);
        review.setClient(client);
        review.setRate(3);

        reviewRepository.save(review);
    }

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
        UserDto.VM userMe = userService.getUserMe();
        assertThat(userMe.getMatchings().get(Matching.Status.REQUEST)).isNotEmpty();
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

    @DisplayName("Seller 상세 조회 성공 - 최신 리뷰와 리뷰 카운트 함께 조회")
    @Test
    public void getSeller_withReviewCntAndLatestReview_success() {
        User sellerInDB = userRepository.findByUsername(TestConstants.TEST_SELLER_USERNAME);
        UserDto.SellerVM sellerUser = userService.getSellerUser(sellerInDB.getId());

        assertThat(sellerUser.getReviewCount()).isGreaterThan(0);
        assertThat(sellerUser.getLatestReview()).isNotNull();
    }

}
