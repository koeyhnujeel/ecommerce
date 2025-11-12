package com.zunza.customer.api.domain.customer.controller

import com.zunza.common.support.resopnse.ApiResponse
import com.zunza.customer.api.domain.customer.dto.request.AddressRegisterRequestDto
import com.zunza.customer.api.domain.customer.service.AddressService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/customers/me")
class AddressController(
    private val addressService: AddressService,
) {
    @PostMapping("/addresses")
    @ResponseStatus(HttpStatus.CREATED)
    fun registerAddress(
        @AuthenticationPrincipal customerId: Long,
        @Valid @RequestBody request: AddressRegisterRequestDto,
    ): ApiResponse<Any> {
        addressService.registerAddress(customerId, request)
        return ApiResponse.success()
    }
}
