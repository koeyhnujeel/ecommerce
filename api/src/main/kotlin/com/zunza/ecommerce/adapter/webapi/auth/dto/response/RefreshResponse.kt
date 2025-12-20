package com.zunza.ecommerce.adapter.webapi.auth.dto.response


data class RefreshResponse(
    val accountId: Long,
) {
    companion object {
        fun from(accountId: Long) = RefreshResponse(accountId)
    }
}