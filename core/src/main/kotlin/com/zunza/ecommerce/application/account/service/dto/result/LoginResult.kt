package com.zunza.ecommerce.application.account.service.dto.result


data class LoginResult(
    val accountId: Long,
    val accessToken: String,
    val refreshToken: String
) {
    companion object {
        fun of(
            accountId: Long,
            accessToken: String,
            refreshToken: String
        ) = LoginResult(accountId, accessToken, refreshToken)
    }
}
