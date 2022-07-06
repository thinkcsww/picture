package com.applory.pictureserverkt.user

import java.time.LocalDateTime
import java.util.UUID

class UserDto {

    data class VM(
        val id: UUID? = null,
        val username: String? = null,
        var nickname: String? = null,
        var description: String? = null,
        var sellerEnabledYn: String? = null,
        var workHourFromDt: Int? = null,
        var workHourToDt: Int? = null,
        var specialty: String? = null,
        var useTermAgreeYn: String? = null,
        var personalInfoUseTermAgreeYn: String? = null,
        var snsType: User.SnsType? = null,
        var createdDt: LocalDateTime? = null,
        var updatedDt: LocalDateTime? = null)

//    data class VM(val user: User) {
//        var id = user.id
//        var username = user.username
//        var nickname = user.nickname
//        var description = user.description
//        var sellerEnabledYn = user.sellerEnabledYn
//        var workHourFromDt = user.workHourFromDt
//        var workHourToDt = user.workHourToDt
//        var specialty = user.specialty
//        var useTermAgreeYn = user.useTermAgreeYn
//        var personalInfoUseTermAgreeYn = user.personalInfoUseTermAgreeYn
//        var snsType = user.snsType
//        var createdDt = user.createdDt
//        var updatedDt = user.updatedDt
//    }

    data class Create(
        val username: String,
        val password: String,
        val nickname: String,
        val useTermAgreeYN: String,
        val personalInfoUseTermAgreeYn: String,
        val snsType: User.SnsType,
        val description: String? = null,
        val sellerEnabledYn: String? = null,
        val workHourFromDt: Int? = null,
        val workHourToDt: Int? = null,
        val specialty: String? = null
    )


}
