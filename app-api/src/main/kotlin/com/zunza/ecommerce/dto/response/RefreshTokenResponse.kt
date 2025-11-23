package com.zunza.ecommerce.dto.response

data class RefreshTokenResponse(
    val accessToken: String
) {
    companion object {
        fun of(accessToken: String) = RefreshTokenResponse(accessToken)
    }
}
