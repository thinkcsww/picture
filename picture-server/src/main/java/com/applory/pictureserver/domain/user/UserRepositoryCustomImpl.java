package com.applory.pictureserver.domain.user;

import com.querydsl.core.QueryResults;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.util.StringUtils;

import static com.applory.pictureserver.domain.user.QUser.user;

@RequiredArgsConstructor
public class UserRepositoryCustomImpl implements UserRepositoryCustom {
    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public Page<User> findClientUserBySearch(UserDto.SearchClient search, Pageable pageable) {
        JPQLQuery<User> query = jpaQueryFactory.select(user)
                .from(user)
                .where(user.sellerEnabledYn.eq("N"))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize());

        QueryResults<User> result = query.fetchResults();

        return new PageImpl<>(result.getResults(), pageable, result.getTotal());
    }

    @Override
    public Page<User> findSellerUserBySearch(UserDto.SearchSeller search, Pageable pageable) {
        JPQLQuery<User> query = jpaQueryFactory
                .select(user)
                .from(user)
                .where(
                        user.sellerEnabledYn.eq("Y"),
                        specialtyContains(search.getSpecialty()),
                        nicknameLike(search.getNickname())
                )
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize());

        QueryResults<User> result = query.fetchResults();

        return new PageImpl<>(result.getResults(), pageable, result.getTotal());
    }

    private BooleanExpression nicknameLike(String nickname) {
        if (!StringUtils.hasText(nickname)) {
            return null;
        }

        return user.nickname.like("%" + nickname + "%");
    }

    private BooleanExpression specialtyContains(String specialty) {
        if (!StringUtils.hasText(specialty)) {
            return null;
        }

        return user.specialty.contains(specialty);
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
