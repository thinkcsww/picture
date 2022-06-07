package com.applory.pictureserverkt.user

import java.util.*

class UserDto {
    class UserVM(user: User) {
        val id: UUID = user.id!!
    }

    data class Create(
        var username: String,
        var password: String
    )


}
