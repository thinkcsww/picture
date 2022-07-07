package com.applory.pictureserverkt.request

import com.applory.pictureserverkt.exception.NotFoundException
import com.applory.pictureserverkt.shared.SecurityUtils
import com.applory.pictureserverkt.user.UserRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import java.text.DecimalFormat
import java.time.LocalDateTime
import java.util.*

@Service
class RequestService(private val requestRepository: RequestRepository, private val userRepository: UserRepository) {
    fun createRequest(dto: RequestDto.Create): Request {
        val request = Request()
        request.requestType = dto.requestType
        request.title = dto.title
        request.description = dto.description
        request.dueDate = dto.dueDate
        request.desiredPrice = dto.desiredPrice
        request.matchYN = if (dto.matchYN != null) dto.matchYN else "N"
        request.completeYN = if (dto.completeYN != null) dto.completeYN else "N"
        request.readCount = 0

        val username = SecurityUtils.getPrincipal()
        val user = userRepository.findByUsername(username)
        request.user = user

        return requestRepository.save(request)
    }

    fun getRequests(search: RequestDto.Search, pageable: Pageable): Page<Request> {
        return requestRepository.findRequestBySearchQ(search, pageable)
    }

    fun getRequest(id: UUID): RequestDto.VM {
        val optionalRequest = requestRepository.findById(id)

        if (optionalRequest.isPresent) {
            val requestVM = RequestDto.convertToVM(optionalRequest.get())

            val search = RequestDto.Search(
                userId = requestVM.userId,
                exceptThisId = requestVM.id
            )

            val anotherRequests = requestRepository.findRequestBySearchQ(search, PageRequest.of(0, 4, Sort.Direction.ASC, "dueDate"))
            requestVM.anotherRequests = anotherRequests.content.map { RequestDto.convertToVM(it) }

            var completeCount = 0
            var closedCount = 0

            var usersAllRequest = requestRepository.findByUser_Id(requestVM.userId)

            for (r: Request in usersAllRequest) {
                if ("Y" == r.completeYN || "Y" == r.matchYN && r.dueDate!!.isBefore(LocalDateTime.now())) {
                    closedCount++
                }
                if ("Y" == r.completeYN) {
                    completeCount++
                }
            }

            if (completeCount != 0 && closedCount != 0) {
                val decimalFormat = DecimalFormat("#.0")
                val acceptRate = completeCount.toDouble() / closedCount * 100.0
                val formattedAcceptRate = decimalFormat.format(acceptRate).toDouble()
                requestVM.userAcceptRate = formattedAcceptRate
            } else {
                requestVM.userAcceptRate = -1.0
            }

            return requestVM
        } else {
            throw NotFoundException("Request not exists: $id")
        }
    }
}
