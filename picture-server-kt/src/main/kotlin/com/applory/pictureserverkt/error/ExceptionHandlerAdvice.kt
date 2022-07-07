package com.applory.pictureserverkt.error

import org.springframework.http.HttpStatus
import org.springframework.validation.BindException
import org.springframework.validation.BindingResult
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestControllerAdvice
import javax.servlet.http.HttpServletRequest

@RestControllerAdvice
class ExceptionHandlerAdvice {
    @ExceptionHandler(MethodArgumentNotValidException::class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun handleRequestBodyValidationException(exception: MethodArgumentNotValidException, request: HttpServletRequest): ApiError {
        val apiError = ApiError(status = HttpStatus.BAD_REQUEST.value(), message = "Validation Error", url = request.servletPath)

        val bindingResult: BindingResult = exception.bindingResult

        val validationErrors = HashMap<String, String?>()

        for (fieldError in bindingResult.fieldErrors) {
            validationErrors[fieldError.field] = fieldError.defaultMessage
        }

        apiError.validationErrors = validationErrors

        return apiError
    }

    @ExceptionHandler(BindException::class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun handleRequestParamValidationException(exception: BindException, request: HttpServletRequest): ApiError? {
        val apiError = ApiError(status = HttpStatus.BAD_REQUEST.value(), message = "Validation Error", url = request.servletPath)

        val bindingResult = exception.bindingResult

        val validationErrors: MutableMap<String, String?> = java.util.HashMap()

        for (fieldError in bindingResult.fieldErrors) {
            validationErrors[fieldError.field] = fieldError.defaultMessage
        }

        apiError.validationErrors = validationErrors

        return apiError
    }
}

