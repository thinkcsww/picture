package com.applory.pictureserverkt.user

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
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
        return UserDto.convertToVM(userService.createUser(dto))
    }

    @GetMapping("/me")
    fun getUserMe(): UserDto.VM {
        return UserDto.convertToVM(userService.getUserMe())
    }

    @GetMapping("/client")
    fun getClientUsers(search: UserDto.SearchClient, pageable: Pageable): Page<UserDto.VM> {
        return userService.getClientUsers(search, pageable).map { UserDto.convertToVM(it) }
    }

    @GetMapping("/seller")
    fun getSellerUsers(@Valid search: UserDto.SearchSeller, pageable: Pageable): Page<UserDto.VM> {
        return userService.getSellerUsers(search, pageable).map { UserDto.convertToVM(it) }
    }

}
