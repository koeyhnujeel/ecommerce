package com.zunza.ecommerce.adapter.webapi

import com.zunza.ecommerce.adapter.ApiResponse
import com.zunza.ecommerce.application.account.provided.AccountRegister
import com.zunza.ecommerce.application.account.service.dto.request.AccountActivateRequest
import com.zunza.ecommerce.application.account.service.dto.request.AccountRegisterRequest
import com.zunza.ecommerce.application.account.service.dto.response.AccountActivateResponse
import com.zunza.ecommerce.application.account.service.dto.response.AccountRegisterResponse
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/accounts")
class CustomerApi(
    private val accountRegister: AccountRegister
) {
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun register(
        @RequestBody @Valid registerRequest: AccountRegisterRequest
    ): ApiResponse<AccountRegisterResponse> {
        val registerResponse = accountRegister.registerCustomerAccount(registerRequest)

        return ApiResponse.success(registerResponse)
    }

    @PostMapping("/activation")
    fun activate(
        @RequestBody @Valid activateRequest: AccountActivateRequest
    ): ApiResponse<AccountActivateResponse> {
        val activateResponse = accountRegister.activateCustomerAccount(activateRequest.accountId)

        return ApiResponse.success(activateResponse)
    }
}