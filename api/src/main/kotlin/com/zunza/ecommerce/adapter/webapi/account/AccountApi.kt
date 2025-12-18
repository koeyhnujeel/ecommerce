package com.zunza.ecommerce.adapter.webapi.account

import com.zunza.ecommerce.adapter.ApiResponse
import com.zunza.ecommerce.adapter.webapi.account.dto.request.AccountRegisterRequest
import com.zunza.ecommerce.adapter.webapi.account.dto.response.AccountRegisterResponse
import com.zunza.ecommerce.application.account.provided.AccountRegister
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/accounts")
class AccountApi(
    private val accountRegister: AccountRegister
) {
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun register(
        @RequestBody @Valid registerRequest: AccountRegisterRequest
    ): ApiResponse<AccountRegisterResponse> {
        val accountId = accountRegister.registerCustomerAccount(registerRequest.toCommand())

        return ApiResponse.success(AccountRegisterResponse.from(accountId))
    }

    @PostMapping("/{accountId}/activation")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun activate(
        @PathVariable accountId: Long
    ): ApiResponse<Any> {
        accountRegister.activateCustomerAccount(accountId)

        return ApiResponse.success()
    }
}