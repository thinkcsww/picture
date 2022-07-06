package com.applory.pictureserverkt.user

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import javax.validation.Valid

@RestController
@RequestMapping("/api/v1/users")
class UserController(private val userService: UserService) {

    @PostMapping("")
    @ResponseStatus(HttpStatus.CREATED)
    fun createUser(@Valid @RequestBody dto: UserDto.Create): UserDto.VM {
        val newUser: User = userService.createUser(dto)

        return UserDto.VM(
            id = newUser.id,
            username = newUser.username,
            nickname = newUser.nickname,
            description = newUser.description,
            sellerEnabledYn = newUser.sellerEnabledYn,
            snsType = newUser.snsType,
            createdDt = newUser.createdDt,
            updatedDt = newUser.updatedDt,
            workHourToDt = newUser.workHourToDt,
            workHourFromDt = newUser.workHourFromDt,
            specialty = newUser.specialty
        )
    }

    @GetMapping("/me")
    fun getUserMe(): UserDto.VM {
        val user = userService.getUserMe()
        return UserDto.VM(
            id = user.id,
            username = user.username,
            nickname = user.nickname,
            description = user.description,
            sellerEnabledYn = user.sellerEnabledYn,
            snsType = user.snsType,
            createdDt = user.createdDt,
            updatedDt = user.updatedDt,
            workHourToDt = user.workHourToDt,
            workHourFromDt = user.workHourFromDt,
            specialty = user.specialty
        )
    }
}
