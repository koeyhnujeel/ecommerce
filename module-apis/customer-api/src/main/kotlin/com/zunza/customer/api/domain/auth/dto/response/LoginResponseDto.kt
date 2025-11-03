package com.zunza.customer.api.domain.auth.dto.response

import com.zunza.customer.api.domain.auth.dto.LoginResultDto

data class LoginResponseDto(
    val nickname: String,
    val accessToken: String
) {
    companion object {
        fun from(dto: LoginResultDto) =
            LoginResponseDto(dto.nickname, dto.accessToken)
    }
}
