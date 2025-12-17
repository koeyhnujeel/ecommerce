package com.zunza.ecommerce.application.account.provided

import com.zunza.ecommerce.application.account.service.dto.request.AccountRegisterRequest
import com.zunza.ecommerce.application.account.service.dto.response.AccountActivateResponse
import com.zunza.ecommerce.application.account.service.dto.response.AccountDeactivateResponse
import com.zunza.ecommerce.application.account.service.dto.response.AccountRegisterResponse
import jakarta.validation.Valid

interface AccountRegister {
    fun registerCustomerAccount(@Valid registerRequest: AccountRegisterRequest): AccountRegisterResponse

    fun activateCustomerAccount(accountId: Long): AccountActivateResponse

    fun deactivateCustomerAccount(accountId: Long): AccountDeactivateResponse
}