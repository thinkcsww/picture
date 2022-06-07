package com.applory.pictureserverkt

import com.applory.pictureserverkt.oauth.AuthDto
import com.applory.pictureserverkt.user.UserDto

class TestUtil {
    companion object {

        @JvmStatic fun createValidLoginDto(username: String): AuthDto.Login {
            return AuthDto.Login(
                username = username,
                kakaoToken = "test"
            )
        }

        @JvmStatic fun createValidUser(username: String): UserDto.Create {
            return UserDto.Create(
                username = username,
                password = "${username}durtnlchrhtn@1"
            )
        }

    }

}
