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
    @PostMapping("/me/addresses")
    @ResponseStatus(HttpStatus.CREATED)
    fun registerAddress(
        @AuthenticationPrincipal accountId: Long,
        @RequestBody @Valid request: RegisterAddressRequest
    ): ApiResponse<RegisterAddressResponse> {
        val customerId = registerAddressUseCase.registerAddress(request.toCommand(accountId))

        return ApiResponse.success(RegisterAddressResponse(customerId))
    }

    @PutMapping("/me/addresses/{addressId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun updateAddress(
        @PathVariable addressId: Long,
        @AuthenticationPrincipal accountId: Long,
        @RequestBody @Valid request: UpdateAddressRequest
    ): ApiResponse<Any> {
        updateAddressUseCase.updateAddress(request.toCommand(accountId, addressId))

        return ApiResponse.success()
    }

    @PatchMapping("/me/addresses/{addressId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun updateDefaultAddress(
        @PathVariable addressId: Long,
        @AuthenticationPrincipal accountId: Long,
    ): ApiResponse<Any> {
        updateDefaultAddressUseCase.updateDefaultAddress(UpdateDefaultAddressCommand(accountId, addressId))

        return ApiResponse.success()
    }

    @DeleteMapping("/me/addresses/{addressId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun deleteAddress(
        @PathVariable addressId: Long,
        @AuthenticationPrincipal accountId: Long,
    ): ApiResponse<Any> {
        deleteAddressUseCase.deleteAddress(DeleteAddressCommand(accountId, addressId))

        return ApiResponse.success()
    }
}