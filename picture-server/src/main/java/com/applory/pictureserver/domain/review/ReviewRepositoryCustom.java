package com.applory.pictureserver.domain.review;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ReviewRepositoryCustom {
    Page<Review> findReviewBySearchQ(ReviewDTO.Search search, Pageable pageable);
}
