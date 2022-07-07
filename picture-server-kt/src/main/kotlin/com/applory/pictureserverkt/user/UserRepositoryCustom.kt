package com.applory.pictureserverkt.user

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Repository

@Repository
interface UserRepositoryCustom {
    fun findClientUserBySearch(search: UserDto.SearchClient, pageable: Pageable): Page<User>

    fun findSellerUserBySearch(search: UserDto.SearchSeller, pageable: Pageable): Page<User>
}
