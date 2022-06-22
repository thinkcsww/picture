package com.applory.pictureserver.domain.request;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.QueryResults;
import com.querydsl.core.types.Predicate;
import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;

import java.time.LocalDateTime;

public class RequestRepositoryCustomImpl extends QuerydslRepositorySupport implements RequestRepositoryCustom {
    private final JPAQueryFactory jpaQueryFactory;

    public RequestRepositoryCustomImpl(JPAQueryFactory jpaQueryFactory) {
        super(Request.class);
        this.jpaQueryFactory = jpaQueryFactory;
    }

    @Override
    public Page<Request> findRequestBySearchQ(RequestDto.Search search, Pageable pageable) {
        QRequest qRequest = QRequest.request;
        JPQLQuery<Request> query = jpaQueryFactory.select(qRequest)
                .from(qRequest)
                .where(createSearchPredicate(search));

        query = getQuerydsl().applyPagination(pageable, query);
        QueryResults<Request> result = query.fetchResults();

        return new PageImpl<>(result.getResults(), pageable, result.getTotal());
    }

    private Predicate createSearchPredicate(RequestDto.Search search) {
        QRequest qRequest = QRequest.request;
        BooleanBuilder booleanBuilder = new BooleanBuilder();

        booleanBuilder.and(qRequest.dueDate.after(LocalDateTime.now()));
        booleanBuilder.and(qRequest.matchYN.eq("N"));

        if (search.getRequestType() != null) {
            booleanBuilder.and(qRequest.requestType.eq(search.getRequestType()));
        }

        if (search.getFromForDueDt() != null && search.getToForDueDt() != null) {
            booleanBuilder.and(qRequest.dueDate.between(search.getFromForDueDt(), search.getToForDueDt()));
        }

        if (search.getUserId() != null) {
            booleanBuilder.and(qRequest.user.id.eq(search.getUserId()));
        }

        if (search.getExceptThisId() != null) {
            booleanBuilder.and(qRequest.id.ne(search.getExceptThisId()));
        }

        return booleanBuilder;
    }
}
