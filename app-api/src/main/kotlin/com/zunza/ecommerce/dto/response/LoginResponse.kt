package com.zunza.ecommerce.dto.response

data class LoginResponse(
    val accessToken: String
) {
    companion object {
        fun of(accessToken: String) = LoginResponse(accessToken)
    }
}
