package com.applory.pictureserver.domain.request;

import com.applory.pictureserver.shared.Constant;
import com.applory.pictureserver.shared.QueryDslUtils;
import com.querydsl.core.QueryResults;
import com.querydsl.core.types.Order;
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
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.applory.pictureserver.domain.request.QRequest.request;

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
                        request.dueDate.after(LocalDateTime.now()),
                        request.matchYN.eq("N"),
                        specialTyEq(search.getSpecialty()),
                        userIdEq(search.getUserId()),
                        exceptId(search.getExceptThisId()),
                        dueDateBetween(search.getFromForDueDt(), search.getToForDueDt())

                )
                .orderBy(getAllOrderSpecifiers(pageable).toArray(new OrderSpecifier[0]))
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
            return null;
        }

        return request.dueDate.between(from, to);
    }

    private final String DESIRED_PRICE = "desiredPrice";
    private final String DUE_DATE = "dueDate";

    private List<OrderSpecifier> getAllOrderSpecifiers(Pageable pageable) {
        List<OrderSpecifier> ORDERS = new ArrayList<>();

        if (!ObjectUtils.isEmpty(pageable.getSort())) {
            for (Sort.Order order : pageable.getSort()) {
                Order direction = order.getDirection().isAscending() ? Order.ASC : Order.DESC;

                switch (order.getProperty()) {
                    case DESIRED_PRICE:
                        OrderSpecifier<?> desiredPrice = QueryDslUtils.getSortedColumn(direction, request, DESIRED_PRICE);
                        ORDERS.add(desiredPrice);
                        break;

                    case DUE_DATE:
                        OrderSpecifier<?> dueDate = QueryDslUtils.getSortedColumn(direction, request, DUE_DATE);
                        ORDERS.add(dueDate);
                        break;
                    default:
                        break;
                }
            }
        }

        return ORDERS;
    }
}
