package com.zunza.ecommerce.dto.result

data class LoginResult(
    val accessToken: String,
    val refreshToken: String
) {
    companion object {
        fun of(accessToken: String, refreshToken: String) =
            LoginResult(accessToken, refreshToken)
    }
}
