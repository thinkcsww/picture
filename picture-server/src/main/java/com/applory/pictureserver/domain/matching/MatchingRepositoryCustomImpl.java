package com.applory.pictureserver.domain.matching;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.UUID;

import static com.applory.pictureserver.domain.matching.QMatching.matching;

@RequiredArgsConstructor
public class MatchingRepositoryCustomImpl implements MatchingRepositoryCustom {
    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public List<Matching> findBySearch(MatchingDto.Search search) {
        return jpaQueryFactory
                .selectFrom(matching)
                .where(
                        userIdEq(search.sellerEnabledYn, search.userId),
                        completeYnEq(search.completeYn)
                )
                .fetch();
    }

    private BooleanExpression userIdEq(String sellerEnabledYn, String userId) {
        if (!StringUtils.hasText(sellerEnabledYn)) {
            return null;
        }

        if ("Y".equals(sellerEnabledYn)) {
            return matching.seller.id.eq(userId);
        } else {
            return matching.client.id.eq(userId);
        }
    }

    private BooleanExpression completeYnEq(String completeYn) {
        if (!StringUtils.hasText(completeYn)) {
            return null;
        }

        return matching.completeYN.eq(completeYn);
    }
}
