package com.zunza.ecommerce.application.account.provided

import com.zunza.ecommerce.application.account.service.dto.request.PasswordChangeRequest
import com.zunza.ecommerce.application.account.service.dto.response.PasswordChangeResponse
import jakarta.validation.Valid

interface AccountManager {
    fun changePassword(accountId: Long, @Valid changeRequest: PasswordChangeRequest): PasswordChangeResponse
}