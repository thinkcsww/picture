package com.applory.pictureserver.domain.review;

import com.querydsl.core.QueryResults;
import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import javax.persistence.EntityManager;

import static com.applory.pictureserver.domain.review.QReview.review;

public class ReviewRepositoryCustomImpl implements ReviewRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;

    public ReviewRepositoryCustomImpl(EntityManager entityManager) {
        this.jpaQueryFactory = new JPAQueryFactory(entityManager);
    }

    @Override
    public Page<Review> findReviewBySearchQ(ReviewDTO.Search search, Pageable pageable) {
        JPQLQuery<Review> query = jpaQueryFactory
                .selectFrom(review)
                .where(
                        review.seller.id.eq(search.getSellerId())
                )
                .orderBy(review.createdDt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize());

        QueryResults<Review> result = query.fetchResults();

        return new PageImpl<>(result.getResults(), pageable, result.getTotal());
    }
}
