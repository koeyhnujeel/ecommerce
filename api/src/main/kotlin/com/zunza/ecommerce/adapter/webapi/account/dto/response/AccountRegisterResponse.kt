package com.zunza.ecommerce.adapter.webapi.account.dto.response


data class AccountRegisterResponse(
    val accountId: Long,
) {
    companion object {
        fun from(accountId: Long) = AccountRegisterResponse(accountId)
    }
}