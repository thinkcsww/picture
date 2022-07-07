package com.applory.pictureserverkt.request

import com.querydsl.core.BooleanBuilder
import com.querydsl.core.types.Predicate
import com.querydsl.jpa.JPQLQuery
import com.querydsl.jpa.impl.JPAQueryFactory
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport
import java.time.LocalDateTime

class RequestRepositoryCustomImpl(private val jpaQueryFactory: JPAQueryFactory): QuerydslRepositorySupport(Request::class.java), RequestRepositoryCustom {

    override fun findRequestBySearchQ(search: RequestDto.Search, pageable: Pageable): Page<Request> {
        val qRequest = QRequest.request
        var query: JPQLQuery<Request> = jpaQueryFactory.select(qRequest)
            .from(qRequest)
            .where(createSearchPredicate(search))

        query = querydsl!!.applyPagination(pageable, query)

        val result = query.fetchResults()

        return PageImpl(result.results, pageable, result.total)
    }

    private fun createSearchPredicate(search: RequestDto.Search): Predicate {
        val qRequest = QRequest.request

        val booleanBuilder = BooleanBuilder()
        booleanBuilder.and(qRequest.dueDate.after(LocalDateTime.now()))
        booleanBuilder.and(qRequest.matchYN.eq("N"))

        if (search.requestType != null) {
            booleanBuilder.and(qRequest.requestType.eq(search.requestType))
        }

        if (search.fromForDueDt != null && search.fromForDueDt != null) {
            booleanBuilder.and(qRequest.dueDate.between(search.fromForDueDt, search.toForDueDt))
        }

        if (search.userId != null) {
            booleanBuilder.and(qRequest.user.id.eq(search.userId))
        }

        if (search.exceptThisId != null) {
            booleanBuilder.and(qRequest.id.ne(search.exceptThisId))
        }

        return booleanBuilder
    }
}
