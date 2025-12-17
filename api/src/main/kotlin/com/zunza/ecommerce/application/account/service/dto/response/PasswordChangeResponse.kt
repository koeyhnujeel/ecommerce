package com.zunza.ecommerce.application.account.service.dto.response

import com.zunza.ecommerce.domain.account.Account

data class PasswordChangeResponse(
    val accountId: Long,
) {
    companion object {
        fun from(account: Account): PasswordChangeResponse {
            return PasswordChangeResponse(account.id)
        }
    }
}
