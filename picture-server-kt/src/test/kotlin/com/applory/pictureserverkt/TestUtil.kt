package com.applory.pictureserverkt

import com.applory.pictureserverkt.oauth.AuthDto
import com.applory.pictureserverkt.user.User
import com.applory.pictureserverkt.user.UserDto

class TestUtil {
    companion object {

        @JvmStatic fun createValidLoginDto(username: String): AuthDto.Login {
            return AuthDto.Login(
                username = username,
                kakaoToken = "test"
            )
        }

        @JvmStatic fun createValidClientUser(username: String): UserDto.Create {
            return UserDto.Create(
                username = username,
                password = "${username}durtnlchrhtn@1",
                nickname = "test-nickname",
                useTermAgreeYN = "Y",
                personalInfoUseTermAgreeYn = "Y",
                snsType = User.SnsType.KAKAO
            )
        }

        @JvmStatic fun createValidSellerUser(username: String): UserDto.Create {
            return UserDto.Create(
                username = username,
                password = username + "durtnlchrhtn@1",
                nickname = "test-nickname",
                description = "test-description",
                sellerEnabledYn = "Y",
                workHourFromDt = 1700,
                workHourToDt = 1830,
                specialty = User.SellerSpecialty.PEOPLE.toString(),
                useTermAgreeYN = "Y",
                personalInfoUseTermAgreeYn = "Y",
                snsType = User.SnsType.KAKAO
            )
        }

    }

}
