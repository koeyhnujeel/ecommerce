package com.zunza.customer.api.domain.auth.dto

data class RefreshResultDto(
    val newAccessToken: String,
    val newRefreshToken: String
) {
    companion object {
        fun of(newAccessToken: String, newRefreshToken: String) =
            RefreshResultDto(newAccessToken, newRefreshToken)
    }
}
