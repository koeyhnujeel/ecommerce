package com.zunza.ecommerce.adapter

import com.zunza.ecommerce.domain.account.AccountNotFoundException
import com.zunza.ecommerce.domain.account.DuplicateEmailException
import org.springframework.http.HttpStatus
import org.springframework.http.ProblemDetail
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler
import java.time.LocalDateTime

@RestControllerAdvice
class ApiControllerAdvice : ResponseEntityExceptionHandler() {
    @ExceptionHandler(Exception::class)
    fun handleException(exception: Exception) =
        getProblemDetail(HttpStatus.INTERNAL_SERVER_ERROR, exception)

    @ExceptionHandler(DuplicateEmailException::class)
    fun duplicateEmailExceptionHandler(exception: DuplicateEmailException) =
        getProblemDetail(HttpStatus.CONFLICT, exception)

    @ExceptionHandler(AccountNotFoundException::class)
    fun customerNotFoundExceptionHandler(exception: AccountNotFoundException) =
        getProblemDetail(HttpStatus.NOT_FOUND, exception)


    private fun getProblemDetail(status: HttpStatus, exception: Exception): ProblemDetail {
        val problemDetail = ProblemDetail.forStatusAndDetail(status, exception.message)

        problemDetail.setProperty("exception", exception.javaClass.simpleName)
        problemDetail.setProperty("timestamp", LocalDateTime.now())

        return problemDetail
    }
}