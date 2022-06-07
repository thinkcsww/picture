package com.applory.pictureserverkt.error

import org.springframework.http.HttpStatus
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
    fun handleValidationException(exception: MethodArgumentNotValidException, request: HttpServletRequest): ApiError {
        val apiError = ApiError(status = HttpStatus.BAD_REQUEST.value(), message = "Validation Error", url = request.servletPath)

        val bindingResult: BindingResult = exception.bindingResult

        var validationErrors = HashMap<String, String?>()

        for (fieldError in bindingResult.fieldErrors) {
            validationErrors.put(fieldError.field, fieldError.defaultMessage)
        }

        apiError.validationErrors = validationErrors

        return apiError
    }
}
