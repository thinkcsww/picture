package com.applory.pictureserverkt.request

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import java.util.*
import javax.validation.Valid

@RestController()
@RequestMapping("/api/v1/requests")
class RequestController(private val requestService: RequestService) {

    @PostMapping("")
    @ResponseStatus(HttpStatus.CREATED)
    fun createRequest(@Valid @RequestBody dto: RequestDto.Create): RequestDto.VM {
        return RequestDto.convertToVM(requestService.createRequest(dto))
    }

    @GetMapping("")
    fun getRequests(search: RequestDto.Search, pageable: Pageable): Page<RequestDto.VM> {
        return requestService.getRequests(search, pageable).map { RequestDto.convertToVM(it) }
    }

    @GetMapping("/{id}")
    fun getRequests(@PathVariable id: UUID): RequestDto.VM {
        return requestService.getRequest(id)
    }

}
