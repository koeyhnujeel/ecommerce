package com.zunza.ecommerce.application.account.service.dto.response

import com.zunza.ecommerce.domain.account.Account
import java.time.LocalDateTime

data class AccountActivateResponse(
    val accountId: Long,
    val email: String,
    val activatedAt: LocalDateTime?
) {
    companion object {
        fun from(account: Account) =
            AccountActivateResponse(account.id, account.email.address, account.activatedAt)
    }
}