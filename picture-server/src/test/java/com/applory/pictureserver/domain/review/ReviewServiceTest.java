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
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

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
        User seller = userRepository.save(TestUtil.createSeller());
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
}
