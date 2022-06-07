package com.applory.pictureserverkt.error

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.web.error.ErrorAttributeOptions
import org.springframework.boot.web.servlet.error.ErrorAttributes
import org.springframework.boot.web.servlet.error.ErrorController
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.context.request.WebRequest

@RestController
class ErrorHandler(private val errorAttributes: ErrorAttributes): ErrorController {
    @RequestMapping("/error")
    fun handleError(webRequest: WebRequest): ApiError {
        val attributes: Map<String, Any> = errorAttributes.getErrorAttributes(webRequest, ErrorAttributeOptions.of(ErrorAttributeOptions.Include.MESSAGE))

        val message = attributes["message"] as String
        val url = attributes["path"] as String
        val status = attributes["status"] as Int
        return ApiError(url = url, status = status, message = message)
    }
}
