package com.zunza.customer.api.domain.auth.dto.response

data class RefreshResponseDto(
    val accessToken: String
) {
    companion object {
        fun from(newAccessToken: String) =
            RefreshResponseDto(newAccessToken)
    }
}
