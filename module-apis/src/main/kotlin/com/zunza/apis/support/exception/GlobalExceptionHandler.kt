package com.zunza.apis.support.exception

import com.zunza.apis.auth.jwt.exception.CustomTokenException
import com.zunza.apis.support.resopnse.ApiResponse
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class GlobalExceptionHandler {
    @ExceptionHandler(BusinessException::class)
    fun customExceptionHandler(e: BusinessException): ResponseEntity<ApiResponse<Any>> {
        val errorResponse = ErrorResponse(e.message)
        return ResponseEntity
            .status(e.errorCode.httpStatus)
            .body(ApiResponse.error(errorResponse))
    }

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun methodArgumentNotValidExceptionHandler(e: MethodArgumentNotValidException): ResponseEntity<ApiResponse<Any>> {
        val errorMessages = e.fieldErrors.map { it.defaultMessage }
        val message = if (errorMessages.count() == 1) errorMessages[0] else null
        val messages = if (errorMessages.count() > 1) errorMessages else null

        val errorResponse = ErrorResponse(message, messages)
        return ResponseEntity
            .status(e.statusCode)
            .body(ApiResponse.error(errorResponse))
    }

    @ExceptionHandler(CustomTokenException::class)
    fun customTokenExceptionHandler(e: CustomTokenException): ResponseEntity<ApiResponse<Any>> {
        val errorResponse = ErrorResponse(e.message)
        return ResponseEntity
            .status(HttpStatus.UNAUTHORIZED)
            .body(ApiResponse.error(errorResponse))
    }
}
