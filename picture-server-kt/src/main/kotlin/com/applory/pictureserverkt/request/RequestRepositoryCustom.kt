package com.applory.pictureserverkt.request

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface RequestRepositoryCustom {
    fun findRequestBySearchQ(search: RequestDto.Search, pageable: Pageable): Page<Request>
}
