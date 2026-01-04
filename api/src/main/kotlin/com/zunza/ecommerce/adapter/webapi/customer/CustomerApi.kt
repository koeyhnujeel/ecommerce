package com.zunza.ecommerce.adapter.webapi.customer

import com.zunza.ecommerce.adapter.ApiResponse
import com.zunza.ecommerce.adapter.webapi.customer.dto.request.RegisterAddressRequest
import com.zunza.ecommerce.adapter.webapi.customer.dto.request.UpdateAddressRequest
import com.zunza.ecommerce.adapter.webapi.customer.dto.response.RegisterAddressResponse
import com.zunza.ecommerce.application.customer.provided.DeleteAddressUseCase
import com.zunza.ecommerce.application.customer.provided.RegisterAddressUseCase
import com.zunza.ecommerce.application.customer.provided.UpdateAddressUseCase
import com.zunza.ecommerce.application.customer.provided.UpdateDefaultAddressUseCase
import com.zunza.ecommerce.application.customer.service.dto.command.DeleteAddressCommand
import com.zunza.ecommerce.application.customer.service.dto.command.UpdateDefaultAddressCommand
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/customers")
class CustomerApi(
    private val registerAddressUseCase: RegisterAddressUseCase,
    private val updateAddressUseCase: UpdateAddressUseCase,
    private val deleteAddressUseCase: DeleteAddressUseCase,
    private val updateDefaultAddressUseCase: UpdateDefaultAddressUseCase
) {
    @PostMapping("/me/shipping-addresses")
    @ResponseStatus(HttpStatus.CREATED)
    fun registerAddress(
        @AuthenticationPrincipal accountId: Long,
        @RequestBody @Valid request: RegisterAddressRequest
    ): ApiResponse<RegisterAddressResponse> {
        val customerId = registerAddressUseCase.registerAddress(request.toCommand(accountId))

        return ApiResponse.success(RegisterAddressResponse(customerId))
    }

    @PutMapping("/me/shipping-addresses/{shippingAddressId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun updateAddress(
        @PathVariable shippingAddressId: Long,
        @AuthenticationPrincipal accountId: Long,
        @RequestBody @Valid request: UpdateAddressRequest
    ): ApiResponse<Any> {
        updateAddressUseCase.updateAddress(request.toCommand(accountId, shippingAddressId))

        return ApiResponse.success()
    }

    @PatchMapping("/me/shipping-addresses/{shippingAddressId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun updateDefaultAddress(
        @PathVariable shippingAddressId: Long,
        @AuthenticationPrincipal accountId: Long,
    ): ApiResponse<Any> {
        updateDefaultAddressUseCase.updateDefaultAddress(UpdateDefaultAddressCommand(accountId, shippingAddressId))

        return ApiResponse.success()
    }

    @DeleteMapping("/me/shipping-addresses/{shippingAddressId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun deleteAddress(
        @PathVariable shippingAddressId: Long,
        @AuthenticationPrincipal accountId: Long,
    ): ApiResponse<Any> {
        deleteAddressUseCase.deleteAddress(DeleteAddressCommand(accountId, shippingAddressId))

        return ApiResponse.success()
    }
}