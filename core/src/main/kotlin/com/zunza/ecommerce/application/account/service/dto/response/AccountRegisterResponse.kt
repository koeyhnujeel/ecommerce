package com.zunza.ecommerce.application.account.service.dto.response

import com.zunza.ecommerce.domain.account.Account
import java.time.LocalDateTime

data class AccountRegisterResponse(
    val accountId: Long,
    val email: String,
    val registeredAt: LocalDateTime,
) {
    companion object {
        fun from(account: Account) =
            AccountRegisterResponse(account.id, account.email.address, account.registeredAt)
    }
}