package com.applory.pictureserverkt.user

import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface UserRepository: JpaRepository<User, UUID> {

    fun findByUsername(username: String): User?
}