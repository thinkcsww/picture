package com.applory.pictureserverkt

import com.applory.pictureserverkt.oauth.AuthDto
import com.applory.pictureserverkt.request.Request
import com.applory.pictureserverkt.request.RequestDto
import com.applory.pictureserverkt.user.User
import com.applory.pictureserverkt.user.UserDto
import java.time.LocalDateTime

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

        @JvmStatic fun createValidRequestDto(): RequestDto.Create {
            return RequestDto.Create(
                requestType = Request.RequestType.BACKGROUND,
                title = "제목입니다",
                description = "설명입니다",
                desiredPrice = 2000,
                dueDate = LocalDateTime.of(2022, 12, 25, 23, 59)
            )
        }

    }

}
