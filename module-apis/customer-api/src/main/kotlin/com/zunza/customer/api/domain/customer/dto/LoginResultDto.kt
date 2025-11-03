package com.zunza.customer.api.domain.customer.dto

data class LoginResultDto(
    val nickname: String,
    val accessToken: String,
    val refreshToken: String,
) {
    companion object {
        fun of(nickname: String, accessToken: String, refreshToken: String) =
            LoginResultDto(nickname, accessToken, refreshToken)
    }
}
