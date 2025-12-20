package com.zunza.ecommerce.application.auth.service.dto.command

data class LogoutCommand(
    val accountId: Long,
    val accessToken: String,
) {
    companion object {
        fun of(accountId: Long, accessToken: String) =
            LogoutCommand(accountId, accessToken)
    }
}
