package com.zunza.ecommerce.application.account.service.dto.response

import com.zunza.ecommerce.domain.account.Account
import java.time.LocalDateTime

data class AccountDeactivateResponse(
    val accountId: Long,
    val email: String,
    val deactivatedAt: LocalDateTime?
) {
    companion object {
        fun from(account: Account) =
            AccountDeactivateResponse(account.id, account.email.address, account.deactivatedAt)
    }
}