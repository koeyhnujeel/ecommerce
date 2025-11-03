package com.zunza.customer.api.domain.auth.dto.request

import jakarta.validation.constraints.NotBlank

data class LoginRequestDto(
    @field:NotBlank(message = "이메일을 입력해 주세요.")
    val email: String,
    @field:NotBlank(message = "비밀번호를 입력해 주세요.")
    val password: String,
)
