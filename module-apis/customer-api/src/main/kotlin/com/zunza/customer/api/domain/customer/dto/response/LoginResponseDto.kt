package com.zunza.customer.api.domain.customer.dto.response

import com.zunza.customer.api.domain.customer.dto.LoginResultDto

data class LoginResponseDto(
    val nickname: String,
    val accessToken: String
) {
    companion object {
        fun from(dto: LoginResultDto) =
            LoginResponseDto(dto.nickname, dto.accessToken)
    }
}
