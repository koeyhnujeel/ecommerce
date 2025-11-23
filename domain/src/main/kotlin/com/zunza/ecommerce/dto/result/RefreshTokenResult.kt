package com.zunza.ecommerce.dto.result

data class RefreshTokenResult(
    val newAccessToken: String,
    val newRefreshToken: String
) {
    companion object {
        fun of(newAccessToken: String, newRefreshToken: String) =
            RefreshTokenResult(newAccessToken, newRefreshToken)
    }
}
