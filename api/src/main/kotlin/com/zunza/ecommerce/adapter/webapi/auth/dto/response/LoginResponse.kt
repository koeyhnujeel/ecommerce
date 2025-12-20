package com.zunza.ecommerce.adapter.webapi.auth.dto.response


data class LoginResponse(
    val accountId: Long,
) {
    companion object {
        fun from(accountId: Long) = LoginResponse(accountId)
    }
}