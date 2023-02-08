package com.applory.pictureserver.domain.review;

import com.applory.pictureserver.shared.Result;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    @GetMapping("")
    public Result<Page<ReviewDTO.ReviewVM>> getReviews(ReviewDTO.Search search, Pageable pageable) {
        return Result.success(reviewService.getReviews(search, pageable));
    }
}
