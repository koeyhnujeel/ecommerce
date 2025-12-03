package com.zunza.ecommerce.handler

import com.zunza.ecommerce.support.exception.BusinessException
import com.zunza.ecommerce.support.exception.CustomTokenException
import com.zunza.ecommerce.support.exception.ErrorResponse
import com.zunza.ecommerce.support.resopnse.ApiResponse
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.multipart.support.MissingServletRequestPartException

val logger = KotlinLogging.logger {  }

@RestControllerAdvice
class GlobalExceptionHandler {
    @ExceptionHandler(BusinessException::class)
    fun customExceptionHandler(e: BusinessException): ResponseEntity<ApiResponse<Any>> {
        val errorResponse = ErrorResponse(e.message)
        return ResponseEntity
            .status(e.errorCode.status)
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

    @ExceptionHandler(MissingServletRequestPartException::class)
    fun missingServletRequestPartExceptionHandler(e: MissingServletRequestPartException): ResponseEntity<ApiResponse<Any>> {
        val message = when (e.requestPartName) {
            "mainImage" -> "상품 대표 이미지를 업로드 해주세요."
            "detailImages" -> "상품 상세 이미지를 업로드 해주세요."
            else -> e.message
        }

        val errorResponse = ErrorResponse(message)

        return ResponseEntity
            .status(e.statusCode)
            .body(ApiResponse.error(errorResponse))
    }

    @ExceptionHandler(Exception::class)
    fun internalServerErrorHandler(e: Exception): ResponseEntity<ApiResponse<Any>> {
        logger.warn { "Unhandled exception: ${e.message}" }

        val message = "서버 오류가 발생했습니다."
        val errorResponse = ErrorResponse(message)

        return ResponseEntity
            .status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(ApiResponse.error(errorResponse))
    }
}
