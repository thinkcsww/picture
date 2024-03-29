package com.applory.pictureserver.domain.user;

import com.applory.pictureserver.domain.user.querydto.SellerListVM;
import com.applory.pictureserver.shared.Constant;
import com.querydsl.core.QueryResults;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.NumberPath;
import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import java.util.Objects;

import static com.applory.pictureserver.domain.file.QFile.file;
import static com.applory.pictureserver.domain.matching.QMatching.matching;
import static com.applory.pictureserver.domain.review.QReview.review;
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
    public Page<SellerListVM> findSellerUserBySearch(UserDto.SearchSeller search, Pageable pageable) {
        JPQLQuery<SellerListVM> query = jpaQueryFactory
                .select(Projections.constructor(SellerListVM.class,
                                user.id,
                                user.nickname,
                                user.description,
                                review.rate.avg(),
                                review.id.countDistinct(),
                                getPrice(search.getSpecialty()),
                                matching.id.countDistinct(),
                                user.file().storeFileName
                        )
                )
                .from(user)
                .leftJoin(review).on(user.id.eq(review.seller().id))
                .leftJoin(matching).on(user.id.eq(matching.seller().id).and(matching.completeYN.eq("Y")))
                .leftJoin(user.file, file)
                .where(
                        user.sellerEnabledYn.eq("Y"),
                        specialtyContains(search.getSpecialty()),
                        nicknameLike(search.getNickname())
                )
                .groupBy(user)
                .orderBy(getOrderSpecifier(pageable, search.getSpecialty()))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize());

        QueryResults<SellerListVM> result = query.fetchResults();

        return new PageImpl<>(result.getResults(), pageable, result.getTotal());
    }

    private NumberPath<Integer> getPrice(Constant.Specialty specialty) {
        if (Objects.isNull(specialty)) {
            return user.peoplePrice;
        }

        if (specialty.equals(Constant.Specialty.OFFICIAL)) {
            return user.officialPrice;
        } else if (specialty.equals(Constant.Specialty.BACKGROUND)) {
            return user.backgroundPrice;
        } else {
            return user.peoplePrice;
        }
    }

    private BooleanExpression nicknameLike(String nickname) {
        if (!StringUtils.hasText(nickname)) {
            return null;
        }

        return user.nickname.like("%" + nickname + "%");
    }

    private BooleanExpression specialtyContains(Constant.Specialty specialty) {
        if (Objects.isNull(specialty)) {
            return user.specialty.contains(Constant.Specialty.PEOPLE.toString());
        }

        return user.specialty.contains(specialty.toString());
    }

    private final String PRICE = "price";
    private final String REVIEW = "review";
    private final String RATING = "rating";
    private final String MATCHING = "matching";

    private OrderSpecifier getOrderSpecifier(Pageable pageable, Constant.Specialty specialty) {
        OrderSpecifier orderSpecifier = user.createdDt.desc();

        if (!ObjectUtils.isEmpty(pageable.getSort())) {
            for (Sort.Order sortOrder : pageable.getSort()) {
                switch (sortOrder.getProperty()) {
                    case PRICE:
                        if (specialty.equals(Constant.Specialty.PEOPLE)) {
                            orderSpecifier = user.peoplePrice.asc();
                        } else if (specialty.equals(Constant.Specialty.BACKGROUND)) {
                            orderSpecifier = user.backgroundPrice.asc();
                        } else {
                            orderSpecifier = user.officialPrice.asc();
                        }
                        break;

                    case REVIEW:
                        orderSpecifier = review.countDistinct().desc();
                        break;
                    case RATING:
                        orderSpecifier = review.rate.avg().desc();
                        break;
                    case MATCHING:
                        orderSpecifier = matching.countDistinct().desc();
                        break;
                    default:
                        break;
                }
            }
        }

        return orderSpecifier;
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
