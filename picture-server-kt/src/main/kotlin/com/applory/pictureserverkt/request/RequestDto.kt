package com.applory.pictureserverkt.request

import org.jetbrains.annotations.NotNull
import org.springframework.format.annotation.DateTimeFormat
import java.time.LocalDateTime
import java.util.*
import javax.validation.constraints.NotEmpty

class RequestDto {

    companion object {
        @JvmStatic
        fun convertToVM(request: Request): VM {
            return VM(
                id = request.id!!,
                userId = request.user!!.id!!,
                userNickname = request.user!!.nickname!!,
                requestType = request.requestType!!,
                title = request.title!!,
                description = request.description!!,
                desiredPrice = request.desiredPrice!!,
                dueDate = request.dueDate!!,
                matchYn = request.matchYN!!,
                readCount = request.readCount!!
            )
        }
    }

    data class VM(
        val id: UUID,
        val userId: UUID,
        val userNickname: String,
        val requestType: Request.RequestType,
        val title: String,
        val description: String,
        val desiredPrice: Int,
        val dueDate: LocalDateTime,
        val matchYn: String,
        val readCount: Int,
        val chatCount: Int? = null,
        var anotherRequests: List<VM>? = null,
        var userAcceptRate: Double? = null
    )

    data class Create(
        @field:NotNull
        var requestType: Request.RequestType,

        @field:NotEmpty
        var title: String?,

        @field:NotNull
        var desiredPrice: Int,

        @field:NotNull
        var dueDate: LocalDateTime,

        @field:NotEmpty
        var description: String,

        var matchYN: String? = null,

        var completeYN: String? = null
    )

    data class Search(
        val requestType: Request.RequestType? = null,

        val userId: UUID? = null,

        val exceptThisId: UUID? = null,

        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
        val fromForDueDt: LocalDateTime? = null,

        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
        val toForDueDt: LocalDateTime? = null
    )


}
