package com.applory.pictureserverkt.user

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import javax.validation.Valid

@RestController
@RequestMapping("/api/v1/users")
class UserController(private val userService: UserService) {

    @PostMapping("")
    fun createUser(@Valid @RequestBody dto: UserDto.Create): UserDto.UserVM {
        val newUser: User = userService.createUser(dto)
        return UserDto.UserVM(newUser)
    }
}
