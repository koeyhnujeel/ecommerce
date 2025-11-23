package com.zunza.ecommerce.dto.result

data class AuthenticateResult(
    val accessToken: String,
    val refreshToken: String
) {
    companion object {
        fun of(accessToken: String, refreshToken: String) =
            AuthenticateResult(accessToken, refreshToken)
    }
}
