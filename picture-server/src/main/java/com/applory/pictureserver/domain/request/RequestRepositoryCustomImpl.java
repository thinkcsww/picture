package com.applory.pictureserver.domain.request;

import com.applory.pictureserver.shared.Constant;
import com.querydsl.core.QueryResults;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.util.ObjectUtils;

import java.time.LocalDateTime;
import java.util.UUID;

import static com.applory.pictureserver.domain.request.QRequest.request;
import static com.applory.pictureserver.shared.Constant.Specialty.PEOPLE;

public class RequestRepositoryCustomImpl implements RequestRepositoryCustom {
    private final JPAQueryFactory jpaQueryFactory;

    public RequestRepositoryCustomImpl(JPAQueryFactory jpaQueryFactory) {
        this.jpaQueryFactory = jpaQueryFactory;
    }

    @Override
    public Page<Request> findRequestBySearchQ(RequestDto.Search search, Pageable pageable) {
        JPQLQuery<Request> query = jpaQueryFactory
                .selectFrom(request)
                .where(
                        request.matchYN.ne("Y"),
                        specialTyEq(search.getSpecialty()),
                        userIdEq(search.getUserId()),
                        exceptId(search.getExceptThisId()),
                        dueDateBetween(search.getFromForDueDt(), search.getToForDueDt())
                )
                .orderBy(getAllOrderSpecifiers(pageable))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize());

        QueryResults<Request> result = query.fetchResults();

        return new PageImpl<>(result.getResults(), pageable, result.getTotal());
    }

    private BooleanExpression specialTyEq(Constant.Specialty value) {
        if (ObjectUtils.isEmpty(value)) {
            return null;
        }

        return request.specialty.eq(value);
    }

    private BooleanExpression userIdEq(UUID value) {
        if (ObjectUtils.isEmpty(value)) {
            return null;
        }

        return request.user.id.eq(value);
    }

    private BooleanExpression exceptId(UUID value) {
        if (ObjectUtils.isEmpty(value)) {
            return null;
        }

        return request.id.ne(value);
    }

    private BooleanExpression dueDateBetween(LocalDateTime from, LocalDateTime to) {
        if (ObjectUtils.isEmpty(from) || ObjectUtils.isEmpty(to)) {
            return request.dueDate.after(LocalDateTime.now());
        }

        return request.dueDate.between(from, to);
    }

    private final String PRICE = "price";
    private final String DUE_DATE = "dueDate";

    private OrderSpecifier getAllOrderSpecifiers(Pageable pageable) {
        OrderSpecifier orderSpecifier = request.createdDt.desc();

        if (!ObjectUtils.isEmpty(pageable.getSort())) {
            for (Sort.Order order : pageable.getSort()) {
                switch (order.getProperty()) {
                    case PRICE:
                        orderSpecifier = request.desiredPrice.desc();
                        break;
                    case DUE_DATE:
                        orderSpecifier = request.dueDate.asc();
                        break;
                    default:
                        break;
                }
            }
        }

        return orderSpecifier;
    }
}
