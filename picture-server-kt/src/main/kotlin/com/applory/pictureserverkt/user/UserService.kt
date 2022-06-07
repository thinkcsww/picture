package com.applory.pictureserverkt.user

import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

@Service
class UserService(private val userRepository: UserRepository, private val passwordEncoder: PasswordEncoder): UserDetailsService {

    override fun loadUserByUsername(username: String): UserDetails {
        val userInDB = userRepository.findByUsername(username)

        if (userInDB == null) {
            throw UsernameNotFoundException("User not found")
        }

        return CustomUserDetails.from(userInDB)
    }

    fun createUser(dto: UserDto.Create): User {
        val password = passwordEncoder.encode(dto.password)
        val user = User(
            username = dto.username,
            password = password
        )
        return userRepository.save(user)
    }
}
