package com.applory.pictureserverkt.user

import org.springframework.lang.Nullable
import java.time.LocalDateTime
import java.util.*
import javax.validation.constraints.Max
import javax.validation.constraints.Min

class UserDto {

    companion object {
        @JvmStatic fun convertToVM(user: User): VM {
            return VM(
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

    data class VM(
        val id: UUID? = null,
        val username: String? = null,
        val nickname: String? = null,
        val description: String? = null,
        val sellerEnabledYn: String? = null,
        val workHourFromDt: Int? = null,
        val workHourToDt: Int? = null,
        val specialty: String? = null,
        val useTermAgreeYn: String? = null,
        val personalInfoUseTermAgreeYn: String? = null,
        val snsType: User.SnsType? = null,
        val createdDt: LocalDateTime? = null,
        val updatedDt: LocalDateTime? = null)



    data class Create(
        val username: String,
        val password: String,
        var nickname: String,
        val useTermAgreeYN: String,
        val personalInfoUseTermAgreeYn: String,
        val snsType: User.SnsType,
        val description: String? = null,
        val sellerEnabledYn: String? = null,
        val workHourFromDt: Int? = null,
        val workHourToDt: Int? = null,
        var specialty: String? = null
    )

    class SearchClient()

    data class SearchSeller(
        @field:Min(0)
        @field:Max(2400)
        @field:Nullable
        val currentTime: String? = null,

        val specialty: String? = null,

        val nickname: String? = null
    )


}
