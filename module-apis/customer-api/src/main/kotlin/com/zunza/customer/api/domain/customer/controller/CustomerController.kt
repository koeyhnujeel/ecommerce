package com.zunza.customer.api.domain.customer.controller

import com.zunza.common.support.resopnse.ApiResponse
import com.zunza.customer.api.domain.customer.dto.request.SignupRequestDto
import com.zunza.customer.api.domain.customer.service.CustomerService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/customers")
class CustomerController(
    private val customerService: CustomerService,
) {
    @GetMapping("/validation/email")
    fun checkEmailDuplicate(
        @RequestParam value: String,
    ): ApiResponse<Any> {
        customerService.isEmailAvailable(value)
        return ApiResponse.success()
    }

    @GetMapping("/validation/phone")
    fun checkPhoneDuplicate(
        @RequestParam value: String,
    ): ApiResponse<Any> {
        customerService.isPhoneAvailable(value)
        return ApiResponse.success()
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun signup(
        @Valid @RequestBody request: SignupRequestDto,
    ): ApiResponse<Any> {
        customerService.signup(request)
        return ApiResponse.success()
    }
}
