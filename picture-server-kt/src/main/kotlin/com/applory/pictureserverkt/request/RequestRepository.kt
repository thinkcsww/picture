package com.applory.pictureserverkt.request

import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface RequestRepository: JpaRepository<Request, UUID>, RequestRepositoryCustom {

    fun findByUser_Id(id: UUID): List<Request>
}
