package com.applory.pictureserverkt.user

import com.querydsl.core.BooleanBuilder
import com.querydsl.core.types.Predicate
import com.querydsl.jpa.JPQLQuery
import com.querydsl.jpa.impl.JPAQueryFactory
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport
import org.springframework.util.StringUtils

class UserRepositoryCustomImpl(private val jpaQueryFactory: JPAQueryFactory): QuerydslRepositorySupport(User::class.java), UserRepositoryCustom {

    override fun findClientUserBySearch(search: UserDto.SearchClient, pageable: Pageable): Page<User> {
        val qUser = QUser.user
        var query: JPQLQuery<User> = jpaQueryFactory.select(qUser)
            .from(qUser)
            .where(qUser.sellerEnabledYn.eq("N"))

        query = querydsl!!.applyPagination(pageable, query)

        val result = query.fetchResults()

        return PageImpl(result.results, pageable, result.total)

    }

    override fun findSellerUserBySearch(search: UserDto.SearchSeller, pageable: Pageable): Page<User> {
        val qUser = QUser.user
        var query: JPQLQuery<User> = jpaQueryFactory.select(qUser)
            .from(qUser)
            .where(createSellerPredicate(search))

        query = querydsl!!.applyPagination(pageable, query)

        val result = query.fetchResults()

        return PageImpl(result.results, pageable, result.total)
    }

    private fun createSellerPredicate(search: UserDto.SearchSeller): Predicate {
        val qUser = QUser.user

        val booleanBuilder = BooleanBuilder()
        booleanBuilder.and(qUser.sellerEnabledYn.eq("Y"))

        if (StringUtils.hasLength(search.specialty)) {
            booleanBuilder.and(qUser.specialty.contains(search.specialty))
        }

        if (StringUtils.hasLength(search.nickname)) {
            booleanBuilder.and(qUser.nickname.like("%" + search.nickname + "%"))
        }

        return booleanBuilder
    }
}
