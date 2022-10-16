package com.applory.pictureserver.domain.user;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.QueryResults;
import com.querydsl.core.types.Predicate;
import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import org.springframework.util.StringUtils;

import java.awt.*;

public class UserRepositoryCustomImpl extends QuerydslRepositorySupport implements UserRepositoryCustom {
    private final JPAQueryFactory jpaQueryFactory;
    public UserRepositoryCustomImpl(JPAQueryFactory jpaQueryFactory) {
        super(User.class);
        this.jpaQueryFactory = jpaQueryFactory;
    }

    @Override
    public Page<User> findClientUserBySearch(UserDto.SearchClient search, Pageable pageable) {
        QUser qUser = QUser.user;
        JPQLQuery<User> query = jpaQueryFactory.select(qUser)
                .from(qUser)
                .where(qUser.sellerEnabledYn.eq("N"));

        query = getQuerydsl().applyPagination(pageable, query);
        QueryResults<User> result = query.fetchResults();

        return new PageImpl<>(result.getResults(), pageable, result.getTotal());
    }

    @Override
    public Page<User> findSellerUserBySearch(UserDto.SearchSeller search, Pageable pageable) {
        QUser qUser = QUser.user;
        JPQLQuery<User> query = jpaQueryFactory
                .select(qUser)
                .from(qUser)
                .where(createSellerPredicate(search));

        query = getQuerydsl().applyPagination(pageable, query);
        QueryResults<User> result = query.fetchResults();

        return new PageImpl<>(result.getResults(), pageable, result.getTotal());
    }

    private Predicate createSellerPredicate(UserDto.SearchSeller search) {
        QUser qUser = QUser.user;
        BooleanBuilder booleanBuilder = new BooleanBuilder();

        booleanBuilder.and(qUser.sellerEnabledYn.eq("Y"));

        if (StringUtils.hasLength(search.getSpecialty())) {
            booleanBuilder.and(qUser.specialty.contains(search.getSpecialty()));
        }

        if (StringUtils.hasLength(search.getNickname())) {
            booleanBuilder.and(qUser.nickname.like("%" + search.getNickname() + "%"));
        }

        return booleanBuilder;
    }


// TODO: 시간에 따른 조회 (TO BE)
//    private BooleanBuilder isWorkHour(int currentTime) {
//        QUser qUser = QUser.user;
//        BooleanBuilder booleanBuilder = new BooleanBuilder();
//        booleanBuilder.and(qUser.workHourFromDt.loe(currentTime));
//        booleanBuilder.and(qUser.workHourToDt.goe(currentTime));
//
//        return booleanBuilder;
//    }

}
