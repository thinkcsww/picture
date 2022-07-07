package com.applory.pictureserverkt.user

import com.applory.pictureserverkt.exception.BadRequestException
import com.applory.pictureserverkt.shared.SecurityUtils
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.util.StringUtils

@Service
class UserService(private val userRepository: UserRepository,
                  private val passwordEncoder: PasswordEncoder): UserDetailsService {



    fun createUser(dto: UserDto.Create): User {
        val password = passwordEncoder.encode(dto.password)
        val user = User(
            username = dto.username,
            password = password
        )

        user.nickname = dto.nickname
        user.useTermAgreeYn = "Y"
        user.personalInfoUseTermAgreeYn = "Y"
        user.sellerEnabledYn = "N"
        user.snsType = dto.snsType

        if (StringUtils.hasLength(dto.description)) {
            user.description = dto.description
        }

        if (StringUtils.hasLength(dto.sellerEnabledYn) && "Y".equals(dto.sellerEnabledYn)) {
            if (dto.workHourFromDt!! > dto.workHourToDt!!) {
                throw BadRequestException("fromDt is bigger than toDt")
            }

            user.sellerEnabledYn = "Y"
            user.workHourFromDt = dto.workHourFromDt
            user.workHourToDt = dto.workHourToDt
            user.specialty = dto.specialty
        }

        return userRepository.save(user)
    }

    fun getUserMe(): User {
        val username = SecurityUtils.getPrincipal()

        return userRepository.findByUsername(username)!!
    }


    override fun loadUserByUsername(username: String): UserDetails {
        val userInDB = userRepository.findByUsername(username)

        if (userInDB == null) {
            throw UsernameNotFoundException("User not found")
        }

        return userInDB
    }

    fun getClientUsers(search: UserDto.SearchClient, pageable: Pageable): Page<User> {
        return userRepository.findClientUserBySearch(search, pageable)
    }

    fun getSellerUsers(search: UserDto.SearchSeller, pageable: Pageable): Page<User> {
        return userRepository.findSellerUserBySearch(search, pageable)
    }
}
