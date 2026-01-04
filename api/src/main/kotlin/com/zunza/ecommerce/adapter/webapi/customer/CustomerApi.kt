package com.zunza.ecommerce.adapter.webapi.customer

import com.zunza.ecommerce.adapter.ApiResponse
import com.zunza.ecommerce.adapter.webapi.customer.dto.request.RegisterAddressRequest
import com.zunza.ecommerce.adapter.webapi.customer.dto.request.UpdateAddressRequest
import com.zunza.ecommerce.adapter.webapi.customer.dto.response.RegisterAddressResponse
import com.zunza.ecommerce.application.customer.provided.DeleteShippingAddressUseCase
import com.zunza.ecommerce.application.customer.provided.RegisterShippingAddressUseCase
import com.zunza.ecommerce.application.customer.provided.UpdateDefaultShippingAddressUseCase
import com.zunza.ecommerce.application.customer.provided.UpdateShippingAddressUseCase
import com.zunza.ecommerce.application.customer.service.dto.command.DeleteShippingAddressCommand
import com.zunza.ecommerce.application.customer.service.dto.command.UpdateDefaultShippingAddressCommand
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/customers")
class CustomerApi(
    private val registerShippingAddressUseCase: RegisterShippingAddressUseCase,
    private val updateShippingAddressUseCase: UpdateShippingAddressUseCase,
    private val deleteShippingAddressUseCase: DeleteShippingAddressUseCase,
    private val updateDefaultShippingAddressUseCase: UpdateDefaultShippingAddressUseCase
) {
    @PostMapping("/me/shipping-addresses")
    @ResponseStatus(HttpStatus.CREATED)
    fun registerAddress(
        @AuthenticationPrincipal accountId: Long,
        @RequestBody @Valid request: RegisterAddressRequest
    ): ApiResponse<RegisterAddressResponse> {
        val customerId = registerShippingAddressUseCase.registerShippingAddress(request.toCommand(accountId))

        return ApiResponse.success(RegisterAddressResponse(customerId))
    }

    @PutMapping("/me/shipping-addresses/{shippingAddressId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun updateAddress(
        @PathVariable shippingAddressId: Long,
        @AuthenticationPrincipal accountId: Long,
        @RequestBody @Valid request: UpdateAddressRequest
    ): ApiResponse<Any> {
        updateShippingAddressUseCase.updateShippingAddress(request.toCommand(accountId, shippingAddressId))

        return ApiResponse.success()
    }

    @PatchMapping("/me/shipping-addresses/{shippingAddressId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun updateDefaultAddress(
        @PathVariable shippingAddressId: Long,
        @AuthenticationPrincipal accountId: Long,
    ): ApiResponse<Any> {
        updateDefaultShippingAddressUseCase.updateDefaultShippingAddress(UpdateDefaultShippingAddressCommand(accountId, shippingAddressId))

        return ApiResponse.success()
    }

    @DeleteMapping("/me/shipping-addresses/{shippingAddressId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun deleteAddress(
        @PathVariable shippingAddressId: Long,
        @AuthenticationPrincipal accountId: Long,
    ): ApiResponse<Any> {
        deleteShippingAddressUseCase.deleteShippingAddress(DeleteShippingAddressCommand(accountId, shippingAddressId))

        return ApiResponse.success()
    }
}