package com.applory.pictureserver.domain.review;

import com.applory.pictureserver.TestConstants;
import com.applory.pictureserver.TestUtil;
import com.applory.pictureserver.config.WithMockClientLogin;
import com.applory.pictureserver.domain.user.User;
import com.applory.pictureserver.domain.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static com.applory.pictureserver.shared.Constant.Specialty.PEOPLE;
import static org.assertj.core.api.Assertions.assertThat;

@Transactional
@SpringBootTest
@ActiveProfiles("test")
class ReviewServiceTest {

    @Autowired
    private ReviewService reviewService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ReviewRepository reviewRepository;

    @BeforeEach
    public void setUp() {
        User seller = userRepository.save(TestUtil.createSeller(TestConstants.TEST_SELLER_NICKNAME, PEOPLE));
        User client = userRepository.save(TestUtil.createClient());
    }

    @WithMockClientLogin
    @DisplayName("리뷰 생성 성공")
    @Test
    public void createReview() {
        User seller = userRepository.findByUsername(TestConstants.TEST_SELLER_USERNAME);

        ReviewDTO.Create create = new ReviewDTO.Create();
        create.setContent("테스트 콘텐츠");
        create.setSellerId(seller.getId());
        create.setRate(3);

        reviewService.save(create);
        assertThat(reviewRepository.findAll().size()).isEqualTo(1);
    }

    @WithMockClientLogin
    @DisplayName("리뷰 리스트 조회 - 최신순")
    @Test
    public void getReviews() {
        User seller2 = TestUtil.createSeller("Seller-nickname-2", PEOPLE);
        seller2.setUsername("seller2-username");
        userRepository.save(seller2);

        User seller = userRepository.findByUsername(TestConstants.TEST_SELLER_USERNAME);

        ReviewDTO.Create create = new ReviewDTO.Create();
        create.setContent("테스트 콘텐츠");
        create.setSellerId(seller.getId());
        create.setRate(3);
        reviewService.save(create);

        ReviewDTO.Create create2 = new ReviewDTO.Create();
        create2.setContent("테스트 콘텐츠");
        create2.setSellerId(seller.getId());
        create2.setRate(5);
        reviewService.save(create2);

        ReviewDTO.Create create3 = new ReviewDTO.Create();
        create3.setContent("테스트 콘텐츠");
        create3.setSellerId(seller2.getId());
        create3.setRate(3);
        reviewService.save(create3);

        ReviewDTO.Search search = new ReviewDTO.Search();
        search.setSellerId(seller.getId());

        Page<ReviewDTO.ReviewVM> reviews = reviewService.getReviews(search, PageRequest.of(0, 20));
        assertThat(reviewRepository.findAll().size()).isEqualTo(3);
        assertThat(reviews.getTotalElements()).isEqualTo(2);
        assertThat(reviews.getContent().get(0).getCreatedDt()).isAfter(reviews.getContent().get(1).getCreatedDt());
    }
}
