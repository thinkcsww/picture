package com.applory.pictureserver.domain.review;

import com.applory.pictureserver.domain.user.User;
import com.applory.pictureserver.domain.user.UserRepository;
import com.applory.pictureserver.shared.SecurityUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ReviewService {

    private final ReviewRepository reviewRepository;

    private final UserRepository userRepository;

    public ReviewService(ReviewRepository reviewRepository, UserRepository userRepository) {
        this.reviewRepository = reviewRepository;
        this.userRepository = userRepository;
    }

    public Review save(ReviewDTO.Create create) {

        String username = SecurityUtils.getPrincipal();
        User client = userRepository.findByUsername(username);
        User seller = userRepository.findById(create.getSellerId()).orElseThrow(() -> new IllegalStateException("Seller: " + create.getSellerId() + " is not exist"));

        Review review = new Review();
        review.setClient(client);
        review.setSeller(seller);
        review.setRate(create.getRate());
        review.setContent(create.getContent());

        return reviewRepository.save(review);
    }

    @Transactional(readOnly = true)
    public Page<ReviewDTO.ReviewVM> getReviews(ReviewDTO.Search search, Pageable pageable) {
        return reviewRepository.findReviewBySearchQ(search, pageable).map(ReviewDTO.ReviewVM::new);
    }
}
